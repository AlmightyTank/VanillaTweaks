package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.custom.pirate.goal.PirateParrotPeckGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PirateParrotEntity extends Parrot {
    private static final EntityDataAccessor<Boolean> FROM_SHOULDER =
            SynchedEntityData.defineId(PirateParrotEntity.class, EntityDataSerializers.BOOLEAN);

    private LivingEntity owner;

    private int lifeTicks = 260;
    private int attackTicks = 170;

    public PirateParrotEntity(EntityType<? extends Parrot> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_SHOULDER, false);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public LivingEntity getOwner() {
        return this.owner;
    }

    public void setFromShoulder(boolean value) {
        this.entityData.set(FROM_SHOULDER, value);
    }

    public boolean isFromShoulder() {
        return this.entityData.get(FROM_SHOULDER);
    }

    public boolean shouldReturnToOwner() {
        return this.isFromShoulder() && this.attackTicks <= 0;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PirateParrotPeckGoal(this, 1.65D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            return;
        }

        this.lifeTicks--;

        if (this.isFromShoulder()) {
            this.tickShoulderParrot();
        } else {
            this.tickNormalSwarmParrot();
        }
    }

    private void tickNormalSwarmParrot() {
        if (this.lifeTicks <= 0) {
            this.discard();
        }
    }

    private void tickShoulderParrot() {
        if (!(this.owner instanceof PirateCaptainEntity captain) || !captain.isAlive()) {
            this.discard();
            return;
        }

        this.attackTicks--;

        if (this.attackTicks > 0 && this.getTarget() != null && this.getTarget().isAlive()) {
            return;
        }

        this.setTarget(null);

        double yawRad = Math.toRadians(captain.getYRot());

        double shoulderX = captain.getX() - Math.sin(yawRad) * 0.45D;
        double shoulderY = captain.getY() + 1.75D;
        double shoulderZ = captain.getZ() + Math.cos(yawRad) * 0.45D;

        this.getMoveControl().setWantedPosition(shoulderX, shoulderY, shoulderZ, 1.7D);

        double distance = this.distanceToSqr(shoulderX, shoulderY, shoulderZ);

        if (distance <= 0.8D) {
            captain.setShoulderParrot(true);
            this.discard();
            return;
        }

        if (this.lifeTicks <= 0) {
            captain.setShoulderParrot(true);
            this.discard();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Parrot.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
}