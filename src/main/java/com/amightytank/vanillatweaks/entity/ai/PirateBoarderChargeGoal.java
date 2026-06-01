package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PirateBoarderChargeGoal extends Goal {
    private static final double MAX_FOOT_CHASE_DISTANCE = 34.0D;
    private static final double TARGET_SEARCH_DISTANCE = 80.0D;
    private static final double MELEE_REACH_DISTANCE = 2.4D;

    private static final int MELEE_COOLDOWN_TICKS = 20;
    private static final int REPATH_COOLDOWN_TICKS = 10;

    private final Mob pirate;

    private int attackCooldown;
    private int repathCooldown;

    public PirateBoarderChargeGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * Dismounted foot chase only.
         * MOVE only.
         *
         * No LOOK flag.
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

        double distanceSqr = this.pirate.distanceToSqr(target);

        return distanceSqr <= MAX_FOOT_CHASE_DISTANCE * MAX_FOOT_CHASE_DISTANCE
                && distanceSqr <= TARGET_SEARCH_DISTANCE * TARGET_SEARCH_DISTANCE;
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

        return this.pirate.distanceToSqr(target) <= MAX_FOOT_CHASE_DISTANCE * MAX_FOOT_CHASE_DISTANCE;
    }

    @Override
    public void start() {
        this.attackCooldown = 0;
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

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }

        if (this.repathCooldown <= 0) {
            this.pirate.getNavigation().moveTo(target, 1.15D);
            this.repathCooldown = REPATH_COOLDOWN_TICKS;
        }

        double distanceSqr = this.pirate.distanceToSqr(target);

        if (distanceSqr > MELEE_REACH_DISTANCE * MELEE_REACH_DISTANCE) {
            return;
        }

        if (this.attackCooldown > 0) {
            return;
        }

        /*
         * No forced look.
         */
        this.pirate.swing(InteractionHand.MAIN_HAND);
        this.pirate.doHurtTarget(target);
        this.attackCooldown = MELEE_COOLDOWN_TICKS;
    }
}