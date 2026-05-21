package com.amightytank.vanillatweaks.entity.custom.pirate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class KrakenTentacleEntity extends Entity {
    public static final int TYPE_SMALL_CHASE = 0;
    public static final int TYPE_BIG_STRIKE = 1;

    private static final EntityDataAccessor<Integer> DATA_ATTACK_TYPE =
            SynchedEntityData.defineId(KrakenTentacleEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_WARMUP_DELAY =
            SynchedEntityData.defineId(KrakenTentacleEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> DATA_ACTIVE =
            SynchedEntityData.defineId(KrakenTentacleEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState smallChaseAnimationState = new AnimationState();
    public final AnimationState bigStrikeAnimationState = new AnimationState();

    @Nullable
    private Entity owner;

    @Nullable
    private UUID ownerUUID;

    private boolean animationStarted;
    private boolean smallChaseDamageDone;
    private boolean bigStrikeDamageDone;
    private int lifeTicks;

    public KrakenTentacleEntity(EntityType<? extends KrakenTentacleEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ATTACK_TYPE, TYPE_BIG_STRIKE);
        this.entityData.define(DATA_WARMUP_DELAY, 0);
        this.entityData.define(DATA_ACTIVE, false);
    }

    public void setAttackActive(boolean active) {
        this.entityData.set(DATA_ACTIVE, active);
    }

    public boolean isAttackActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    public boolean hasAnimationStarted() {
        return this.animationStarted;
    }

    @Override
    public void tick() {
        super.tick();

        /*
         * Server controls when the tentacle is allowed to appear.
         */
        if (!this.level().isClientSide && !this.isAttackActive()) {
            if (this.getWarmupDelay() > 0) {
                this.setWarmupDelay(this.getWarmupDelay() - 1);
                return;
            }

            this.setAttackActive(true);
        }

        /*
         * Client and server both wait until active.
         * This prevents the model from showing before the animation starts.
         */
        if (!this.isAttackActive()) {
            return;
        }

        if (!this.animationStarted) {
            this.startCorrectAnimation();
            this.animationStarted = true;
        }

        this.lifeTicks++;

        if (!this.level().isClientSide) {
            this.handleDamageTicks();
        }

        if (!this.level().isClientSide && this.lifeTicks > this.getMaxLifeTicks()) {
            this.discard();
        }
    }

    private void handleDamageTicks() {
        if (this.getAttackType() == TYPE_SMALL_CHASE) {
            if (!this.smallChaseDamageDone && this.lifeTicks >= 16) {
                this.dealAttackDamage(this.getSmallChaseDamageAmount());
                this.smallChaseDamageDone = true;
            }

            return;
        }

        /*
         * Big strike hits once as one heavy slam.
         */
        if (!this.bigStrikeDamageDone && this.lifeTicks >= 43) {
            this.dealAttackDamage(this.getBigStrikeDamageAmount());
            this.bigStrikeDamageDone = true;
        }
    }

    private float getSmallChaseDamageAmount() {
        return 1.0F;
    }

    private float getBigStrikeDamageAmount() {
        /*
         * 3 hits x 4 damage = 12 total if all swings connect.
         */
        return 6.0F;
    }

    private void startCorrectAnimation() {
        if (this.getAttackType() == TYPE_SMALL_CHASE) {
            this.smallChaseAnimationState.start(this.tickCount);
        } else {
            this.bigStrikeAnimationState.start(this.tickCount);
        }
    }

    private int getMaxLifeTicks() {
        if (this.getAttackType() == TYPE_SMALL_CHASE) {
            return 45;
        }

        return 75;
    }

    private double getHitRange() {
        if (this.getAttackType() == TYPE_SMALL_CHASE) {
            return 0.9D;
        }

        return 1.75D;
    }

    private void dealAttackDamage(float damageAmount) {
        AABB hitBox = this.getBoundingBox().inflate(this.getHitRange(), 1.0D, this.getHitRange());

        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, hitBox)) {
            if (!this.canDamageEntity(livingEntity)) {
                continue;
            }
            livingEntity.hurt(this.getTentacleDamageSource(), damageAmount);
        }
    }

    private boolean canDamageEntity(LivingEntity livingEntity) {
        if (!livingEntity.isAlive()) {
            return false;
        }

        Entity ownerEntity = this.getOwner();

        if (ownerEntity != null && livingEntity.is(ownerEntity)) {
            return false;
        }

        return true;
    }

    private DamageSource getTentacleDamageSource() {
        Entity ownerEntity = this.getOwner();

        if (ownerEntity instanceof LivingEntity livingOwner) {
            return this.damageSources().mobAttack(livingOwner);
        }

        return this.damageSources().magic();
    }

    public void setAttackType(int attackType) {
        this.entityData.set(DATA_ATTACK_TYPE, attackType);
    }

    public int getAttackType() {
        return this.entityData.get(DATA_ATTACK_TYPE);
    }

    public void setWarmupDelay(int warmupDelay) {
        this.entityData.set(DATA_WARMUP_DELAY, warmupDelay);
    }

    public int getWarmupDelay() {
        return this.entityData.get(DATA_WARMUP_DELAY);
    }

    public void setOwner(@Nullable Entity owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public Entity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            this.owner = serverLevel.getEntity(this.ownerUUID);
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setAttackType(tag.getInt("AttackType"));
        this.setWarmupDelay(tag.getInt("WarmupDelay"));
        this.lifeTicks = tag.getInt("LifeTicks");
        this.smallChaseDamageDone = tag.getBoolean("SmallChaseDamageDone");
        this.bigStrikeDamageDone = tag.getBoolean("BigStrikeDamageDone");
        this.setAttackActive(tag.getBoolean("AttackActive"));

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("AttackType", this.getAttackType());
        tag.putInt("WarmupDelay", this.getWarmupDelay());
        tag.putInt("LifeTicks", this.lifeTicks);
        tag.putBoolean("SmallChaseDamageDone", this.smallChaseDamageDone);
        tag.putBoolean("BigStrikeDamageDone", this.bigStrikeDamageDone);
        tag.putBoolean("AttackActive", this.isAttackActive());

        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}