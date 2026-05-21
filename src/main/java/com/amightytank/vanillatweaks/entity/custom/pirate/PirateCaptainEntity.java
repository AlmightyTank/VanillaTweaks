package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ai.CaptainParrotSwarmGoal;
import com.amightytank.vanillatweaks.entity.ai.CaptainSummonKrakenGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class PirateCaptainEntity extends AbstractPirateEntity {
    private static final EntityDataAccessor<Boolean> HAS_SHOULDER_PARROT =
            SynchedEntityData.defineId(PirateCaptainEntity.class, EntityDataSerializers.BOOLEAN);

    public PirateCaptainEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHOULDER_PARROT, true);
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    public boolean hasShoulderParrot() {
        return this.entityData.get(HAS_SHOULDER_PARROT);
    }

    public void setShoulderParrot(boolean value) {
        this.entityData.set(HAS_SHOULDER_PARROT, value);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new CaptainSummonKrakenGoal(this));
        this.goalSelector.addGoal(2, new CaptainParrotSwarmGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
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
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }
}