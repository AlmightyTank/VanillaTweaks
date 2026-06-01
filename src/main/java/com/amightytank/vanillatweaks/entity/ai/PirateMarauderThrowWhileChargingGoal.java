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

    /*
     * If the target is farther than melee range but inside this range,
     * the marauder can throw its axe/trident.
     */
    private static final double THROW_RANGE = 28.0D;

    private static final double MELEE_REACH_DISTANCE = 2.4D;

    private static final int THROW_COOLDOWN_TICKS = 45;
    private static final int MELEE_COOLDOWN_TICKS = 20;
    private static final int REPATH_COOLDOWN_TICKS = 10;

    private final Mob pirate;

    private int throwCooldown;
    private int meleeCooldown;
    private int repathCooldown;

    public PirateMarauderThrowWhileChargingGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * This goal fully replaces PirateBoarderChargeGoal for marauders.
         * MOVE = charge target.
         * LOOK = face target while chasing/throwing/meleeing.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        if (!(this.pirate instanceof RangedAttackMob)) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        return AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= MAX_CHASE_DISTANCE * MAX_CHASE_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        return AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= MAX_CHASE_DISTANCE * MAX_CHASE_DISTANCE;
    }

    @Override
    public void start() {
        this.throwCooldown = 10;
        this.meleeCooldown = 0;
        this.repathCooldown = 0;
    }

    @Override
    public void stop() {
        this.pirate.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.pirate.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        PirateLookHelper.lookAtEntity(this.pirate, target);

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
        double meleeRangeSqr = MELEE_REACH_DISTANCE * MELEE_REACH_DISTANCE;
        double throwRangeSqr = THROW_RANGE * THROW_RANGE;

        /*
         * Close enough = stop and melee.
         */
        if (distanceSqr <= meleeRangeSqr) {
            this.pirate.getNavigation().stop();

            if (this.meleeCooldown <= 0) {
                PirateLookHelper.lookAtEntity(this.pirate, target);
                this.pirate.swing(InteractionHand.MAIN_HAND);
                this.pirate.doHurtTarget(target);
                this.meleeCooldown = MELEE_COOLDOWN_TICKS;
            }

            return;
        }

        /*
         * Too far to melee = throw axe/trident if in throw range.
         *
         * This is the important part:
         * - target must be outside melee range
         * - target must be inside throw range
         * - line of sight required
         * - cooldown required
         */
        if (distanceSqr <= throwRangeSqr
                && this.throwCooldown <= 0
                && this.pirate.getSensing().hasLineOfSight(target)
                && this.pirate instanceof RangedAttackMob rangedAttackMob) {
            PirateLookHelper.lookAtEntity(this.pirate, target);
            this.pirate.swing(InteractionHand.MAIN_HAND);
            rangedAttackMob.performRangedAttack(target, 1.0F);
            this.throwCooldown = THROW_COOLDOWN_TICKS;
        }

        /*
         * Keep charging even while throwing.
         */
        if (this.repathCooldown <= 0) {
            this.pirate.getNavigation().moveTo(target, 1.2D);
            this.repathCooldown = REPATH_COOLDOWN_TICKS;
        }
    }
}