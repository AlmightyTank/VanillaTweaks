package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ai.PirateBoatBoarderRemountGoal;
import com.amightytank.vanillatweaks.entity.ai.PirateBoatPilotGoal;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPirateEntity extends AbstractIllager {

    protected AbstractPirateEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        //this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        // Pirates can still fight back, but NOT against other pirates.
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static boolean isPirateAlly(@Nullable Entity entity) {
        return entity instanceof AbstractPirateEntity
                || entity instanceof PirateParrotEntity
                || entity instanceof KrakenTentacleEntity;
    }

    public static boolean canPirateAttack(@Nullable LivingEntity target) {
        return target != null && target.isAlive() && !isPirateAlly(target);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (isPirateAlly(target)) {
            return false;
        }

        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (isPirateAlly(target)) {
            super.setTarget(null);
            return;
        }

        super.setTarget(target);
    }


    @Override
    public boolean isAlliedTo(Entity entity) {
        if (isPirateAlly(entity)) {
            return true;
        }

        return super.isAlliedTo(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        Entity directEntity = source.getDirectEntity();

        // Blocks melee damage, arrows, dynamite arrows, thrown spears, etc.
        // as long as the owner/shooter is a pirate.
        if (isPirateAlly(attacker) || isPirateAlly(directEntity)) {
            return false;
        }

        return super.hurt(source, amount);
    }

    public boolean canBoatRangedAttackTarget(LivingEntity target) {
        if (!(this.getVehicle() instanceof Boat boat)) {
            return true;
        }

        // 90 left + 90 right = 180 total attack view.
        // They cannot shoot/throw behind the boat.
        return ModBoatEntity.canBoatPassengerAttackTarget(
                boat,
                this,
                target,
                90.0F
        );
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerArmPose.ATTACKING;
        }

        if (!this.getMainHandItem().isEmpty()) {
            return IllagerArmPose.NEUTRAL;
        }

        return IllagerArmPose.CROSSED;
    }

    public double getBoatPilotStopRange() {
        return 8.0D;
    }

    public double getBoatPilotStartRange() {
        return this.getBoatPilotStopRange() + 4.0D;
    }

    public boolean isCurrentBoatDriver() {
        Entity vehicle = this.getVehicle();

        return vehicle instanceof Boat
                && vehicle.getFirstPassenger() == this;
    }

    public boolean shouldLetBoatPilotHandleTarget(LivingEntity target, double attackRange) {
        if (!canPirateAttack(target)) {
            return false;
        }

        if (!(this.getVehicle() instanceof Boat)) {
            return false;
        }

        if (!this.isCurrentBoatDriver()) {
            return false;
        }

        double pilotStopRange = this.getBoatPilotStopRange();

        /*
         * If the driver is outside the pilot stop range, let the boat move.
         */
        if (this.distanceToSqr(target) > pilotStopRange * pilotStopRange) {
            return true;
        }

        /*
         * If target is outside the boat attack arc, let the boat rotate.
         */
        return !this.canBoatRangedAttackTarget(target);
    }
}