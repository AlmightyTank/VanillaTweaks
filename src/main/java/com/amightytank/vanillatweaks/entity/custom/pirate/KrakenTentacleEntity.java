package com.amightytank.vanillatweaks.entity.custom.pirate;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
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

    private static final int BIG_STRIKE_LIFE_TICKS = 70;
    private static final int SMALL_CHASE_LIFE_TICKS = 80;

    private static final int BIG_STRIKE_HIT_ACTIVE_TICK = 40;

    public final AnimationState bigStrikeAnimationState = new AnimationState();
    public final AnimationState smallChaseAnimationState = new AnimationState();

    private LivingEntity owner;

    private int lifeTicks = BIG_STRIKE_LIFE_TICKS;
    private int activeTicks = 0;

    private boolean hasHit = false;
    private int lastAnimatedAttackType = -1;

    public KrakenTentacleEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setNoGravity(true);
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
        this.entityData.set(WARMUP_DELAY, Math.max(0, delay));
    }

    public int getWarmupDelay() {
        return this.entityData.get(WARMUP_DELAY);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);

        if (this.activeTicks == 0) {
            this.lifeTicks = getMaxLifeTicksForType(type);
        }
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
        return this.activeTicks > 0;
    }

    public int getLifeTicks() {
        return this.lifeTicks;
    }

    public int getActiveTicks() {
        return this.activeTicks;
    }

    public int getMaxLifeTicks() {
        return getMaxLifeTicksForType(this.getAttackType());
    }

    private static int getMaxLifeTicksForType(int type) {
        if (type == TYPE_SMALL_CHASE) {
            return SMALL_CHASE_LIFE_TICKS;
        }

        return BIG_STRIKE_LIFE_TICKS;
    }

    public float getLifeProgress() {
        return Mth.clamp((float) this.activeTicks / (float) this.getMaxLifeTicks(), 0.0F, 1.0F);
    }

    public float getLifeProgress(float partialTick) {
        return Mth.clamp(((float) this.activeTicks + partialTick) / (float) this.getMaxLifeTicks(), 0.0F, 1.0F);
    }

    /*
     * Turning is disabled for now.
     * Keep this method so old spawn code can still call tentacle.faceTarget(player)
     * without causing compile errors or rotating the model.
     */
    public void faceTarget(LivingEntity target) {
        // Disabled for now.
    }

    private void lockRotation() {
        this.setYRot(0.0F);
        this.yRotO = 0.0F;

        this.yBodyRot = 0.0F;
        this.yBodyRotO = 0.0F;

        this.yHeadRot = 0.0F;
        this.yHeadRotO = 0.0F;
    }

    private void startCorrectAnimationImmediately() {
        int currentAttackType = this.getAttackType();

        if (this.lastAnimatedAttackType == currentAttackType) {
            return;
        }

        this.lastAnimatedAttackType = currentAttackType;

        this.bigStrikeAnimationState.stop();
        this.smallChaseAnimationState.stop();

        if (currentAttackType == TYPE_SMALL_CHASE) {
            this.smallChaseAnimationState.start(this.tickCount);
        } else {
            this.bigStrikeAnimationState.start(this.tickCount);
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.setNoGravity(true);
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);

        /*
         * Turning disabled.
         * This keeps the rendered tentacle facing its default Blockbench direction.
         */
        this.lockRotation();

        /*
         * Animation starts immediately so it does not spawn big and then shrink.
         */
        this.startCorrectAnimationImmediately();

        this.activeTicks++;

        if (this.lifeTicks > 0) {
            this.lifeTicks--;
        }

        int warmup = this.getWarmupDelay();

        if (warmup > 0) {
            this.setWarmupDelay(warmup - 1);
        }

        if (!this.level().isClientSide) {
            if (warmup <= 0 && this.isBigStrikeTentacle() && !this.hasHit && this.activeTicks >= BIG_STRIKE_HIT_ACTIVE_TICK) {
                this.hitNearbyTargets();
                this.hasHit = true;
            }

            if (this.lifeTicks <= 0) {
                this.discard();
            }
        }
    }

    private void hitNearbyTargets() {
        AABB hitBox = this.getBoundingBox().inflate(2.25D, 4.0D, 2.25D);

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

            double xKnock = target.getX() - this.getX();
            double zKnock = target.getZ() - this.getZ();

            target.knockback(0.8D, -xKnock, -zKnock);
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.45D, 0.0D));
            target.hasImpulse = true;
            target.hurtMarked = true;
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
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