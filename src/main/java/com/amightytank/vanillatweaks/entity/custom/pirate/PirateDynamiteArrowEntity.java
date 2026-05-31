package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class PirateDynamiteArrowEntity extends AbstractArrow {
    private static final float EXPLOSION_POWER = 2.0F;

    private boolean exploded = false;

    public PirateDynamiteArrowEntity(EntityType<? extends PirateDynamiteArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PirateDynamiteArrowEntity(Level level, LivingEntity owner) {
        this(ModEntities.PIRATE_DYNAMITE_ARROW.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
        this.setBaseDamage(3.0D);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.PIRATE_DYNAMITE_ARROW.get());
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        this.explode();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        Entity owner = this.getOwner();

        if (owner != null) {
            if (entity == owner) {
                return false;
            }

            Entity ownerVehicle = owner.getVehicle();

            if (ownerVehicle != null && entity == ownerVehicle) {
                return false;
            }

            if (entity.isPassengerOfSameVehicle(owner)) {
                return false;
            }

            if (entity instanceof AbstractPirateEntity) {
                return false;
            }
        }

        return super.canHitEntity(entity);
    }

    private void explode() {
        if (this.level().isClientSide) {
            return;
        }

        if (this.exploded) {
            return;
        }

        this.exploded = true;

        this.level().explode(
                this,
                this.getX(),
                this.getY(),
                this.getZ(),
                EXPLOSION_POWER,
                Level.ExplosionInteraction.MOB
        );

        this.discard();
    }
}