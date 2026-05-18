package com.amightytank.vanillatweaks.entity.custom.pirate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractPirateBruteEntity extends AbstractPirateEntity {
    public static final int ATTACK_NONE = 0;

    public static final int SPEAR_WINDUP = 1;
    public static final int SPEAR_LUNGE = 2;
    public static final int SPEAR_RECOVER = 3;

    public static final int AXE_WINDUP = 4;
    public static final int AXE_CHOP = 5;
    public static final int AXE_RECOVER = 6;

    private static final EntityDataAccessor<Integer> DATA_ATTACK_STATE =
            SynchedEntityData.defineId(AbstractPirateBruteEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ATTACK_TICK =
            SynchedEntityData.defineId(AbstractPirateBruteEntity.class, EntityDataSerializers.INT);

    protected AbstractPirateBruteEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACK_STATE, ATTACK_NONE);
        this.entityData.define(DATA_ATTACK_TICK, 0);
    }

    public int getAttackState() {
        return this.entityData.get(DATA_ATTACK_STATE);
    }

    public void setAttackState(int state) {
        this.entityData.set(DATA_ATTACK_STATE, state);
    }

    public int getAttackTick() {
        return this.entityData.get(DATA_ATTACK_TICK);
    }

    public void setAttackTick(int tick) {
        this.entityData.set(DATA_ATTACK_TICK, tick);
    }

    public boolean isBruteAttacking() {
        return this.getAttackState() != ATTACK_NONE;
    }

    public abstract ItemStack getBruteWeaponStack();

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, this.getBruteWeaponStack());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AttackState", this.getAttackState());
        tag.putInt("AttackTick", this.getAttackTick());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAttackState(tag.getInt("AttackState"));
        this.setAttackTick(tag.getInt("AttackTick"));
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.96F;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.035F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }
}