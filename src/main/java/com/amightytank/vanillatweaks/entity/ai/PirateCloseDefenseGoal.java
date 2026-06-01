package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class PirateCloseDefenseGoal extends Goal {
    private static final double ATTACK_REACH_DISTANCE = 2.6D;
    private static final double ATTACK_REACH_DISTANCE_SQR =
            ATTACK_REACH_DISTANCE * ATTACK_REACH_DISTANCE;

    private static final int ATTACK_COOLDOWN_TICKS = 20;

    private final Mob pirate;

    private int attackCooldown;

    public PirateCloseDefenseGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * No MOVE.
         * No LOOK.
         *
         * This is only a close slap if the target is already beside the pirate.
         * It will not turn the pirate around mounted or dismounted.
         */
    }

    @Override
    public boolean canUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        return AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= ATTACK_REACH_DISTANCE_SQR;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        LivingEntity target = this.pirate.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        if (this.pirate.distanceToSqr(target) > ATTACK_REACH_DISTANCE_SQR) {
            return;
        }

        if (this.attackCooldown > 0) {
            return;
        }

        this.pirate.swing(InteractionHand.MAIN_HAND);
        this.pirate.doHurtTarget(target);
        this.attackCooldown = ATTACK_COOLDOWN_TICKS;
    }
}