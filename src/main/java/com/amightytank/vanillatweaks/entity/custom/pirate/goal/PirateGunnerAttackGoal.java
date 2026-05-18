package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateGunnerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class PirateGunnerAttackGoal extends Goal {
    private final PirateGunnerEntity gunner;
    private final RangedAttackMob rangedAttackMob;
    private final double speedModifier;
    private final int attackInterval;
    private final float attackRadius;
    private final float attackRadiusSqr;

    private int attackTime = -1;
    private int seeTime;
    private int chargeTime = 0;
    private boolean chargingShot = false;

    public PirateGunnerAttackGoal(
            PirateGunnerEntity gunner,
            double speedModifier,
            int attackInterval,
            float attackRadius
    ) {
        this.gunner = gunner;
        this.rangedAttackMob = gunner;
        this.speedModifier = speedModifier;
        this.attackInterval = attackInterval;
        this.attackRadius = attackRadius;
        this.attackRadiusSqr = attackRadius * attackRadius;

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.gunner.getTarget() != null && this.gunner.getMainHandItem().is(Items.CROSSBOW);
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        this.seeTime = 0;
        this.attackTime = -1;
        this.chargeTime = 0;
        this.chargingShot = false;
        this.gunner.setChargingCrossbow(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.gunner.getTarget();

        if (target == null) {
            return;
        }

        double distanceToTarget = this.gunner.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean canSeeTarget = this.gunner.getSensing().hasLineOfSight(target);

        if (canSeeTarget) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        if (distanceToTarget <= this.attackRadiusSqr && this.seeTime >= 5) {
            this.gunner.getNavigation().stop();
        } else {
            this.gunner.getNavigation().moveTo(target, this.speedModifier);
        }

        this.gunner.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.gunner.setAggressive(true);

        if (this.chargingShot) {
            this.chargeTime--;
            this.gunner.setChargingCrossbow(true);

            if (this.chargeTime <= 0) {
                if (canSeeTarget) {
                    float distanceFactor = (float) Math.sqrt(distanceToTarget) / this.attackRadius;
                    float clampedDistanceFactor = Math.min(Math.max(distanceFactor, 0.1F), 1.0F);

                    this.rangedAttackMob.performRangedAttack(target, clampedDistanceFactor);
                }

                this.gunner.setChargingCrossbow(false);
                this.chargingShot = false;
                this.attackTime = this.attackInterval;
            }

            return;
        }

        if (--this.attackTime == 0) {
            if (!canSeeTarget) {
                return;
            }

            this.chargingShot = true;
            this.chargeTime = 15;
            this.gunner.setChargingCrossbow(true);
        } else if (this.attackTime < 0) {
            this.attackTime = this.attackInterval;
        }
    }
}