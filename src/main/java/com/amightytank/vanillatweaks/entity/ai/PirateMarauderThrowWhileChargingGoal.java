package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class PirateMarauderThrowWhileChargingGoal extends Goal {
    private static final double MAX_CHASE_DISTANCE = 34.0D;
    private static final double THROW_RANGE = 28.0D;
    private static final double MELEE_REACH_DISTANCE = 2.4D;

    private static final int THROW_COOLDOWN_TICKS = 45;
    private static final int MELEE_COOLDOWN_TICKS = 20;
    private static final int REPATH_COOLDOWN_TICKS = 10;

    private final Mob pirate;

    private int throwCooldown;
    private int meleeCooldown;
    private int repathCooldown;
    private int seeTime;

    public PirateMarauderThrowWhileChargingGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * Dismounted charge only.
         * MOVE only.
         *
         * Do not claim LOOK here.
         * Constant LOOK control is what was turning pirates around.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return false;
        }

        return this.pirate.distanceToSqr(target) <= MAX_CHASE_DISTANCE * MAX_CHASE_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return false;
        }

        return this.pirate.distanceToSqr(target) <= MAX_CHASE_DISTANCE * MAX_CHASE_DISTANCE;
    }

    @Override
    public void start() {
        this.throwCooldown = 10;
        this.meleeCooldown = 0;
        this.repathCooldown = 0;
        this.seeTime = 0;
    }

    @Override
    public void stop() {
        this.pirate.getNavigation().stop();
        this.seeTime = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.pirate.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        if (this.throwCooldown > 0) {
            this.throwCooldown--;
        }

        if (this.meleeCooldown > 0) {
            this.meleeCooldown--;
        }

        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }

        double distanceSqr = this.pirate.distanceToSqr(target);
        boolean canSeeTarget = this.pirate.getSensing().hasLineOfSight(target);

        if (canSeeTarget) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        if (this.repathCooldown <= 0) {
            this.pirate.getNavigation().moveTo(target, 1.15D);
            this.repathCooldown = REPATH_COOLDOWN_TICKS;
        }

        if (distanceSqr <= MELEE_REACH_DISTANCE * MELEE_REACH_DISTANCE) {
            this.tryMeleeAttack(target);
            return;
        }

        if (distanceSqr <= THROW_RANGE * THROW_RANGE) {
            this.tryThrowAttack(target);
        }
    }

    private void tryMeleeAttack(LivingEntity target) {
        if (this.meleeCooldown > 0) {
            return;
        }

        /*
         * No look control here.
         * Movement/pathing already turns the body naturally.
         */
        this.pirate.swing(InteractionHand.MAIN_HAND);
        this.pirate.doHurtTarget(target);
        this.meleeCooldown = MELEE_COOLDOWN_TICKS;
    }

    private void tryThrowAttack(LivingEntity target) {
        if (this.throwCooldown > 0) {
            return;
        }

        if (this.seeTime < 5) {
            return;
        }

        if (!(this.pirate instanceof RangedAttackMob rangedAttackMob)) {
            return;
        }

        /*
         * Only look when actually throwing.
         * Do not force look every tick while charging.
         */
        PirateLookHelper.lookAtEntity(this.pirate, target);

        rangedAttackMob.performRangedAttack(target, 1.0F);
        this.throwCooldown = THROW_COOLDOWN_TICKS;
    }
}