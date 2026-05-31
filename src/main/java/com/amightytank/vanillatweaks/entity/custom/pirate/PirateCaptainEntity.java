package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ai.CaptainParrotSwarmGoal;
import com.amightytank.vanillatweaks.entity.ai.CaptainSummonKrakenGoal;
import com.amightytank.vanillatweaks.entity.ai.PirateBoatPilotGoal;
import com.amightytank.vanillatweaks.entity.ai.PirateMountedAwareMeleeAttackGoal;
import com.amightytank.vanillatweaks.entity.ai.util.PirateRaidAiUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class PirateCaptainEntity extends AbstractPirateEntity {
    private static final EntityDataAccessor<Boolean> HAS_SHOULDER_PARROT =
            SynchedEntityData.defineId(PirateCaptainEntity.class, EntityDataSerializers.BOOLEAN);

    public PirateCaptainEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);

        /*
         * Captain is a ranged/caster pirate.
         * It is NOT a boarder.
         */
        this.addTag(PirateRaidAiUtil.RAID_PIRATE_TAG);
        this.addTag(PirateRaidAiUtil.RANGED_TAG);
        this.addTag(PirateRaidAiUtil.CAPTAIN_TAG);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHOULDER_PARROT, true);
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {

    }

    private int captainSpellCooldown;

    public boolean isCaptainSpellOnCooldown() {
        return this.captainSpellCooldown > 0;
    }

    public void setCaptainSpellCooldown(int ticks) {
        this.captainSpellCooldown = Math.max(this.captainSpellCooldown, ticks);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.captainSpellCooldown > 0) {
            this.captainSpellCooldown--;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    public boolean hasShoulderParrot() {
        return this.entityData.get(HAS_SHOULDER_PARROT);
    }

    public boolean hasActiveParrotSwarm() {
        if (this.level().isClientSide) {
            return false;
        }

        return !this.level().getEntitiesOfClass(
                PirateParrotEntity.class,
                this.getBoundingBox().inflate(96.0D),
                parrot -> parrot.isAlive()
                        && !parrot.isFromShoulder()
                        && this.getUUID().equals(parrot.getOwnerUUID())
        ).isEmpty();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        /*
         * Pilot first.
         * PirateBoatPilotGoal only claims MOVE, so captain casting goals can still use LOOK
         * if those goals are set up correctly.
         */
        this.goalSelector.addGoal(3, new PirateBoatPilotGoal(this));

        /*
         * Captain pressure abilities.
         * These should act like ranged/offshore pressure, not boarder behavior.
         */
        this.goalSelector.addGoal(4, new CaptainSummonKrakenGoal(this));
        this.goalSelector.addGoal(5, new CaptainParrotSwarmGoal(this));

        /*
         * Fallback only if the captain gets knocked off the boat.
         * This goal already refuses to run while mounted.
         */
        this.goalSelector.addGoal(6, new PirateMountedAwareMeleeAttackGoal(this, 1.0D, false));
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerArmPose.SPELLCASTING;
        }

        return IllagerArmPose.CROSSED;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }

    @Override
    public double getBoatPilotStopRange() {
        /*
         * Captain should hold closer than gunners but not beach.
         * PirateBoatPilotGoal's safe-land hold range still overrides when the player is inland.
         */
        return 12.0D;
    }

    @Override
    public double getBoatPilotStartRange() {
        return 18.0D;
    }
}