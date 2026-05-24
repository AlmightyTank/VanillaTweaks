package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateMarauderEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;

public class PirateMarauderRangedAttackGoal extends RangedAttackGoal {
    private final PirateMarauderEntity marauder;
    private final double minRangeSqr;

    public PirateMarauderRangedAttackGoal(PirateMarauderEntity marauder,
                                          double speedModifier,
                                          int attackInterval,
                                          float attackRadius,
                                          float minRange) {
        super(marauder, speedModifier, attackInterval, attackRadius);

        this.marauder = marauder;
        this.minRangeSqr = minRange * minRange;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.marauder.getTarget();

        return super.canUse()
                && this.marauder.isValidBruteTarget(target)
                && this.marauder.distanceToSqr(target) >= this.minRangeSqr
                && this.marauder.hasLineOfSight(target);
    }


    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.marauder.getTarget();

        return super.canContinueToUse()
                && this.marauder.isValidBruteTarget(target)
                && this.marauder.distanceToSqr(target) >= this.minRangeSqr * 0.75D;
    }
}