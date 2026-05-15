package com.amightytank.vanillatweaks.entity.custom.pirate;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class KrakenTentacleEntity extends Monster {
    private LivingEntity owner;
    private int lifeTicks = 60;
    private boolean hasHit = false;

    public KrakenTentacleEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            this.lifeTicks--;

            if (!this.hasHit && this.lifeTicks <= 45) {
                this.hitNearbyTargets();
                this.hasHit = true;
            }

            if (this.lifeTicks <= 0) {
                this.discard();
            }
        }
    }

    private void hitNearbyTargets() {
        AABB hitBox = this.getBoundingBox().inflate(1.0D, 1.5D, 1.0D);

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
                LivingEntity.class,
                hitBox,
                entity -> entity.isAlive()
                        && entity != this.owner
                        && !(entity instanceof AbstractPirateEntity)
                        && !(entity instanceof PirateParrotEntity)
                        && !(entity instanceof KrakenTentacleEntity)
        );

        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().mobAttack(this), 7.0F);
            target.knockback(0.8D, this.getX() - target.getX(), this.getZ() - target.getZ());
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.45D, 0.0D));
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }
}