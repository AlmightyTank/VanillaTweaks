package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateBruteEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PirateBruteLungeAttackGoal extends Goal {
    private final PirateBruteEntity brute;
    private final double chaseSpeed;

    private LivingEntity target;
    private int lungeTicks;
    private int cooldownTicks;
    private boolean damagedThisLunge;

    public PirateBruteLungeAttackGoal(PirateBruteEntity brute, double chaseSpeed) {
        this.brute = brute;
        this.chaseSpeed = chaseSpeed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.brute.getTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        this.target = target;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive() && this.brute.isAlive();
    }

    @Override
    public void start() {
        this.lungeTicks = 0;
        this.cooldownTicks = 0;
        this.damagedThisLunge = false;

        this.brute.setAggressive(false);
        this.brute.setLungeTicks(0);
    }

    @Override
    public void stop() {
        this.target = null;
        this.lungeTicks = 0;
        this.cooldownTicks = 0;
        this.damagedThisLunge = false;

        this.brute.setAggressive(false);
        this.brute.setLungeTicks(0);
        this.brute.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        this.brute.getLookControl().setLookAt(this.target, 40.0F, 40.0F);

        if (this.cooldownTicks > 0) {
            this.cooldownTicks--;

            this.brute.setAggressive(false);
            this.brute.setLungeTicks(0);
            this.brute.getNavigation().moveTo(this.target, this.chaseSpeed);

            return;
        }

        double distanceSqr = this.brute.distanceToSqr(this.target);

        double startRange = this.brute.isHoldingSpear() ? 6.0D : 4.75D;
        double startRangeSqr = startRange * startRange;

        double hitRange = this.brute.isHoldingSpear() ? 3.5D : 2.25D;
        double hitRangeSqr = hitRange * hitRange + this.target.getBbWidth();

        if (distanceSqr > startRangeSqr) {
            this.lungeTicks = 0;
            this.damagedThisLunge = false;

            this.brute.setAggressive(false);
            this.brute.setLungeTicks(0);
            this.brute.getNavigation().moveTo(this.target, this.chaseSpeed);

            return;
        }

        this.brute.getNavigation().stop();

        this.lungeTicks++;
        this.brute.setAggressive(true);
        this.brute.setLungeTicks(this.lungeTicks);

        // 1-10: windup
        // 11: lunge movement
        // 12-16: damage window
        // 17-24: recovery

        if (this.lungeTicks == 11) {
            this.lungeAtTarget();
        }

        if (this.lungeTicks >= 12 && this.lungeTicks <= 16 && !this.damagedThisLunge) {
            double currentDistanceSqr = this.brute.distanceToSqr(this.target);

            if (currentDistanceSqr <= hitRangeSqr) {
                this.brute.swing(this.brute.getUsedItemHand());
                this.brute.doHurtTarget(this.target);
                this.damagedThisLunge = true;
            }
        }

        if (this.lungeTicks >= 24) {
            this.lungeTicks = 0;
            this.damagedThisLunge = false;

            this.brute.setAggressive(false);
            this.brute.setLungeTicks(0);

            this.cooldownTicks = this.brute.isHoldingSpear() ? 26 : 22;
        }
    }

    private void lungeAtTarget() {
        if (this.target == null) {
            return;
        }

        double x = this.target.getX() - this.brute.getX();
        double z = this.target.getZ() - this.brute.getZ();

        float angle = (float) Mth.atan2(z, x);

        double power;

        if (this.brute.isInWaterOrBubble()) {
            power = this.brute.isHoldingSpear() ? 1.0D : 0.85D;
        } else {
            power = this.brute.isHoldingSpear() ? 0.85D : 0.7D;
        }

        double yBoost = this.brute.isInWaterOrBubble() ? 0.16D : 0.08D;

        this.brute.setDeltaMovement(
                Mth.cos(angle) * power,
                this.brute.getDeltaMovement().y + yBoost,
                Mth.sin(angle) * power
        );

        this.brute.hasImpulse = true;
    }
}