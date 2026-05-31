package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateRaidAiUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class PirateMountedAwareMeleeAttackGoal extends MeleeAttackGoal {
    private final PathfinderMob pirate;

    public PirateMountedAwareMeleeAttackGoal(PathfinderMob pirate, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(pirate, speedModifier, followingTargetEvenIfNotSeen);
        this.pirate = pirate;
    }

    @Override
    public boolean canUse() {
        if (this.pirate.getVehicle() != null || this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        if (this.shouldStopForSafeLand(target)) {
            return false;
        }

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.getVehicle() != null || this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        if (this.shouldStopForSafeLand(target)) {
            return false;
        }

        return super.canContinueToUse();
    }

    private boolean shouldStopForSafeLand(LivingEntity target) {
        if (!PirateRaidAiUtil.isBoarder(this.pirate)) {
            return false;
        }

        if (!PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.pirate, target)) {
            return false;
        }

        this.pirate.setTarget(null);
        this.pirate.getNavigation().stop();
        PirateRaidAiUtil.markNeedsRemount(this.pirate);

        return true;
    }
}