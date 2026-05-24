package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateMarauderEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class PirateMarauderCloseMeleeGoal extends MeleeAttackGoal {
    private final PirateMarauderEntity marauder;
    private final double startRangeSqr;
    private final double stopRangeSqr;

    public PirateMarauderCloseMeleeGoal(PirateMarauderEntity marauder, double speedModifier, boolean followingTargetEvenIfNotSeen,
                                     double startRange, double stopRange) {
        super(marauder, speedModifier, followingTargetEvenIfNotSeen);

        this.marauder = marauder;
        this.startRangeSqr = startRange * startRange;
        this.stopRangeSqr = stopRange * stopRange;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.marauder.getTarget();

        if (!this.marauder.isValidBruteTarget(target)) {
            return false;
        }

        if (this.marauder.distanceToSqr(target) > this.startRangeSqr) {
            return false;
        }

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.marauder.getTarget();

        if (!this.marauder.isValidBruteTarget(target)) {
            return false;
        }

        if (this.marauder.distanceToSqr(target) > this.stopRangeSqr) {
            return false;
        }

        return super.canContinueToUse();
    }
}