package com.amightytank.vanillatweaks.entity.custom.pirate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class PirateThrownWeaponEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> DATA_WEAPON_TYPE =
            SynchedEntityData.defineId(PirateThrownWeaponEntity.class, EntityDataSerializers.INT);

    public enum WeaponType {
        TRIDENT,
        AXE;

        public static WeaponType byId(int id) {
            WeaponType[] values = values();

            if (id < 0 || id >= values.length) {
                return TRIDENT;
            }

            return values[id];
        }
    }

    public PirateThrownWeaponEntity(EntityType<? extends PirateThrownWeaponEntity> entityType, Level level) {
        super(entityType, level);

        this.pickup = Pickup.DISALLOWED;
    }

    public PirateThrownWeaponEntity(EntityType<? extends PirateThrownWeaponEntity> entityType,
                                    Level level,
                                    LivingEntity owner) {
        super(entityType, owner, level);

        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_WEAPON_TYPE, WeaponType.TRIDENT.ordinal());
    }

    public void setWeaponType(WeaponType weaponType) {
        this.entityData.set(DATA_WEAPON_TYPE, weaponType.ordinal());
    }

    public WeaponType getWeaponType() {
        return WeaponType.byId(this.entityData.get(DATA_WEAPON_TYPE));
    }

    public ItemStack getRenderStack() {
        if (this.getWeaponType() == WeaponType.AXE) {
            return new ItemStack(Items.IRON_AXE);
        }

        return new ItemStack(Items.TRIDENT);
    }

    @Override
    protected ItemStack getPickupItem() {
        // Brute-thrown weapons should not be picked up.
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity hitEntity = hitResult.getEntity();

        // No pirate friendly fire.
        if (AbstractPirateEntity.isPirateAlly(hitEntity)) {
            if (!this.level().isClientSide) {
                this.discard();
            }

            return;
        }

        super.onHitEntity(hitResult);

        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);

        // Brute has infinite thrown weapons, so do not leave them stuck in blocks.
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("WeaponType", this.getWeaponType().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.setWeaponType(WeaponType.byId(tag.getInt("WeaponType")));
    }
}