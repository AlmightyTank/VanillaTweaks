package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateWaterBoarderMoveHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PirateBoarderChargeGoal extends Goal {
    private static final double MAX_FOOT_CHASE_DISTANCE = 34.0D;
    private static final int REPATH_COOLDOWN_TICKS = 10;

    private final Mob pirate;

    private int repathCooldown;

    public PirateBoarderChargeGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * MOVE only.
         * This goal only moves the pirate.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        return AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= MAX_FOOT_CHASE_DISTANCE * MAX_FOOT_CHASE_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
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

        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }

        if (this.repathCooldown <= 0) {
            this.pirate.getNavigation().moveTo(target, 1.15D);
            PirateWaterBoarderMoveHelper.moveToward(this.pirate, target.position());
            this.repathCooldown = REPATH_COOLDOWN_TICKS;
        }
    }
}