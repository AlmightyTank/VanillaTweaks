package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateMarauderEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class PirateMarauderThrowWhileChargingGoal extends Goal {
    private final PirateMarauderEntity marauder;
    private final RangedAttackMob rangedAttackMob;

    private final int attackInterval;
    private final float maxAttackRange;
    private final float maxAttackRangeSqr;
    private final float minThrowRange;
    private final float minThrowRangeSqr;

    private int attackCooldown;
    private int seeTime;

    public PirateMarauderThrowWhileChargingGoal(
            PirateMarauderEntity marauder,
            int attackInterval,
            float maxAttackRange,
            float minThrowRange
    ) {
        this.marauder = marauder;
        this.rangedAttackMob = marauder;

        this.attackInterval = attackInterval;
        this.maxAttackRange = maxAttackRange;
        this.maxAttackRangeSqr = maxAttackRange * maxAttackRange;
        this.minThrowRange = minThrowRange;
        this.minThrowRangeSqr = minThrowRange * minThrowRange;

        /*
         * Important:
         * Only LOOK.
         * PirateBoarderChargeGoal keeps MOVE and makes the marauder run in.
         */
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.marauder.isPassenger()) {
            return false;
        }

        LivingEntity target = this.marauder.getTarget();

        if (!this.isAllowedTarget(target)) {
            return false;
        }

        double distanceSqr = this.marauder.distanceToSqr(target);

        return distanceSqr <= this.maxAttackRangeSqr
                && distanceSqr >= this.minThrowRangeSqr;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.marauder.isPassenger()) {
            return false;
        }

        LivingEntity target = this.marauder.getTarget();

        if (!this.isAllowedTarget(target)) {
            return false;
        }

        double distanceSqr = this.marauder.distanceToSqr(target);

        /*
         * Stop throwing once close enough for melee.
         */
        return distanceSqr <= this.maxAttackRangeSqr
                && distanceSqr >= this.minThrowRangeSqr * 0.75D;
    }

    @Override
    public void start() {
        this.attackCooldown = 10;
        this.seeTime = 0;
    }

    @Override
    public void stop() {
        this.attackCooldown = 0;
        this.seeTime = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.marauder.getTarget();

        if (!this.isAllowedTarget(target)) {
            return;
        }

        double distanceSqr = this.marauder.distanceToSqr(target);

        if (distanceSqr < this.minThrowRangeSqr) {
            return;
        }

        if (distanceSqr > this.maxAttackRangeSqr) {
            return;
        }

        boolean canSeeTarget = this.marauder.getSensing().hasLineOfSight(target);

        if (canSeeTarget) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        this.marauder.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
            return;
        }

        if (!canSeeTarget || this.seeTime < 5) {
            this.attackCooldown = 5;
            return;
        }

        float distanceFactor = (float) Math.sqrt(distanceSqr) / this.maxAttackRange;
        float clampedDistanceFactor = Math.min(Math.max(distanceFactor, 0.1F), 1.0F);

        this.rangedAttackMob.performRangedAttack(target, clampedDistanceFactor);

        this.attackCooldown = this.attackInterval;
    }

    private boolean isAllowedTarget(LivingEntity target) {
        if (!this.marauder.isValidBruteTarget(target)) {
            return false;
        }

        /*
         * Marauder is a boarder hybrid.
         * If player escaped safely inland, stop throwing and go remount.
         */
        if (PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.marauder, target)) {
            this.marauder.setTarget(null);
            this.marauder.getNavigation().stop();
            PirateRaidAiUtil.markNeedsRemount(this.marauder);
            return false;
        }

        return true;
    }
}