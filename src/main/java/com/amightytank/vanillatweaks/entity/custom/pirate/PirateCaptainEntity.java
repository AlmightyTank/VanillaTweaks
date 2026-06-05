package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ai.*;
import com.amightytank.vanillatweaks.entity.ai.util.PirateRaidAiUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    private final Set<UUID> activeSwarmParrots = new HashSet<>();

    public boolean isCaptainSpellOnCooldown() {
        return this.captainSpellCooldown > 0;
    }

    public void setCaptainSpellCooldown(int ticks) {
        this.captainSpellCooldown = Math.max(this.captainSpellCooldown, ticks);
    }

    public void trackSwarmParrot(PirateParrotEntity parrot) {
        if (!this.level().isClientSide && parrot != null) {
            this.activeSwarmParrots.add(parrot.getUUID());
        }
    }

    public boolean hasActiveParrotSwarm() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        this.activeSwarmParrots.removeIf(uuid -> {
            Entity entity = serverLevel.getEntity(uuid);

            if (!(entity instanceof PirateParrotEntity parrot)) {
                return true;
            }

            return !parrot.isAlive() || parrot.isFromShoulder();
        });

        return !this.activeSwarmParrots.isEmpty();
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

    @Override
    protected void registerGoals() {
        super.registerGoals();

        /*
         * Mounted movement.
         * MOVE only.
         */
        this.goalSelector.addGoal(2, new PirateBoatPilotGoal(this));
        /*
         * Captain pressure abilities.
         * These should be LOOK only inside their own goal files.
         */
        this.goalSelector.addGoal(4, new CaptainSummonKrakenGoal(this));
        this.goalSelector.addGoal(5, new CaptainParrotSwarmGoal(this));

        /*
         * Emergency close defense only.
         * No vanilla MeleeAttackGoal.
         * No MOVE.
         * No LOOK.
         */
        this.goalSelector.addGoal(8, new PirateMeleeAttackActionGoal(this));
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
}