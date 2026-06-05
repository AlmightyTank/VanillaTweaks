package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class PirateMeleeAttackActionGoal extends Goal {
    private static final double MELEE_REACH_DISTANCE = 2.4D;
    private static final double MELEE_REACH_DISTANCE_SQR =
            MELEE_REACH_DISTANCE * MELEE_REACH_DISTANCE;

    private static final int MELEE_COOLDOWN_TICKS = 20;

    private final Mob pirate;

    private int attackCooldown;

    public PirateMeleeAttackActionGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * No flags.
         * This goal only performs melee attacks and aims during its own tick.
         */
    }

    @Override
    public boolean canUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        return AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= MELEE_REACH_DISTANCE_SQR;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
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

        this.lookAtCombatTarget(target);

        if (this.pirate.distanceToSqr(target) > MELEE_REACH_DISTANCE_SQR) {
            return;
        }

        if (this.attackCooldown > 0) {
            return;
        }

        this.pirate.swing(InteractionHand.MAIN_HAND);
        this.pirate.doHurtTarget(target);
        this.attackCooldown = MELEE_COOLDOWN_TICKS;
    }

    private void lookAtCombatTarget(LivingEntity target) {
        this.pirate.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }
}