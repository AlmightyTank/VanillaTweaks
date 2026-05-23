package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PirateThrownWeaponEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Integer> DATA_WEAPON_TYPE =
            SynchedEntityData.defineId(PirateThrownWeaponEntity.class, EntityDataSerializers.INT);

    private float damage = 8.0F;
    private float knockback = 0.7F;

    public PirateThrownWeaponEntity(EntityType<? extends PirateThrownWeaponEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PirateThrownWeaponEntity(Level level, LivingEntity owner) {
        super(ModEntities.PIRATE_THROWN_WEAPON.get(), owner, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WEAPON_TYPE, PirateBruteEntity.BruteWeaponType.TRIDENT.getId());
    }

    public void setWeaponType(PirateBruteEntity.BruteWeaponType type) {
        this.entityData.set(DATA_WEAPON_TYPE, type.getId());

        if (type == PirateBruteEntity.BruteWeaponType.AXE) {
            this.setItem(new net.minecraft.world.item.ItemStack(Items.IRON_AXE));
        } else {
            this.setItem(new net.minecraft.world.item.ItemStack(Items.TRIDENT));
        }
    }

    public PirateBruteEntity.BruteWeaponType getWeaponType() {
        return PirateBruteEntity.BruteWeaponType.byId(this.entityData.get(DATA_WEAPON_TYPE));
    }

    public void setThrownDamage(float damage) {
        this.damage = damage;
    }

    public void setThrownKnockback(float knockback) {
        this.knockback = knockback;
    }

    @Override
    protected Item getDefaultItem() {
        return this.getWeaponType() == PirateBruteEntity.BruteWeaponType.AXE
                ? Items.IRON_AXE
                : Items.TRIDENT;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity hitEntity = result.getEntity();
        Entity owner = this.getOwner();

        if (hitEntity == owner) {
            return;
        }

        if (AbstractPirateEntity.isPirateAlly(hitEntity)) {
            if (!this.level().isClientSide) {
                this.discard();
            }
            return;
        }

        boolean hurt = hitEntity.hurt(this.damageSources().thrown(this, owner), this.damage);

        if (hurt && hitEntity instanceof LivingEntity living) {
            Vec3 push = living.position().subtract(this.position()).normalize().scale(this.knockback);
            living.push(push.x, 0.15D, push.z);
            living.hurtMarked = true;
        }

        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (result.getType() != HitResult.Type.ENTITY && !this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("WeaponType", this.getWeaponType().getId());
        tag.putFloat("ThrownDamage", this.damage);
        tag.putFloat("ThrownKnockback", this.knockback);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.setWeaponType(PirateBruteEntity.BruteWeaponType.byId(tag.getInt("WeaponType")));

        if (tag.contains("ThrownDamage")) {
            this.damage = tag.getFloat("ThrownDamage");
        }

        if (tag.contains("ThrownKnockback")) {
            this.knockback = tag.getFloat("ThrownKnockback");
        }
    }
}