package com.amightytank.vanillatweaks.entity.custom.pirate;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class KrakenTentacleEntity extends Monster {
    private static final EntityDataAccessor<Integer> WARMUP_DELAY =
            SynchedEntityData.defineId(KrakenTentacleEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ATTACK_TYPE =
            SynchedEntityData.defineId(KrakenTentacleEntity.class, EntityDataSerializers.INT);

    public static final int TYPE_SMALL_CHASE = 0;
    public static final int TYPE_BIG_STRIKE = 1;

    private static final int MAX_LIFE_TICKS = 40;

    private LivingEntity owner;
    private int lifeTicks = MAX_LIFE_TICKS;
    private boolean hasHit = false;
    private boolean activated = false;

    public KrakenTentacleEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WARMUP_DELAY, 0);
        this.entityData.define(ATTACK_TYPE, TYPE_BIG_STRIKE);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public LivingEntity getOwner() {
        return this.owner;
    }

    public void setWarmupDelay(int delay) {
        this.entityData.set(WARMUP_DELAY, delay);
    }

    public int getWarmupDelay() {
        return this.entityData.get(WARMUP_DELAY);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public boolean isSmallChaseTentacle() {
        return this.getAttackType() == TYPE_SMALL_CHASE;
    }

    public boolean isBigStrikeTentacle() {
        return this.getAttackType() == TYPE_BIG_STRIKE;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public float getLifeProgress() {
        if (!this.activated) {
            return 0.0F;
        }

        return 1.0F - ((float) this.lifeTicks / (float) MAX_LIFE_TICKS);
    }

    @Override
    public void tick() {
        super.tick();

        this.setDeltaMovement(0.0D, 0.0D, 0.0D);

        int warmup = this.getWarmupDelay();

        if (warmup > 0) {
            this.setWarmupDelay(warmup - 1);
            return;
        }

        this.activated = true;

        // Must run on client too so the model animation moves.
        this.lifeTicks--;

        if (!this.level().isClientSide) {
            // Small chase tentacles are mostly visual. Big strike tentacles hurt.
            if (this.isBigStrikeTentacle() && !this.hasHit && this.lifeTicks <= 30) {
                this.hitNearbyTargets();
                this.hasHit = true;
            }

            if (this.lifeTicks <= 0) {
                this.discard();
            }
        }
    }

    private void hitNearbyTargets() {
        AABB hitBox = this.getBoundingBox().inflate(1.25D, 1.6D, 1.25D);

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
                LivingEntity.class,
                hitBox,
                entity -> entity.isAlive()
                        && entity != this
                        && entity != this.owner
                        && !(entity instanceof AbstractPirateEntity)
                        && !(entity instanceof PirateParrotEntity)
                        && !(entity instanceof KrakenTentacleEntity)
        );

        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().mobAttack(this), 7.0F);

            double xKnock = this.getX() - target.getX();
            double zKnock = this.getZ() - target.getZ();

            target.knockback(0.8D, xKnock, zKnock);
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.45D, 0.0D));
            target.hasImpulse = true;
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(net.minecraft.world.entity.Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 0.0D);
    }
}