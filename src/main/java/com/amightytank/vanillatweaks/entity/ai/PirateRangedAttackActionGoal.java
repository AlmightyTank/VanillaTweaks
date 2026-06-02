package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class PirateRangedAttackActionGoal extends Goal {
    private final Mob pirate;
    private final double attackRange;
    private final int attackCooldownTicks;
    private final boolean mountedOnly;

    private int attackCooldown;
    private int seeTime;

    public PirateRangedAttackActionGoal(
            Mob pirate,
            double attackRange,
            int attackCooldownTicks,
            boolean mountedOnly
    ) {
        this.pirate = pirate;
        this.attackRange = attackRange;
        this.attackCooldownTicks = attackCooldownTicks;
        this.mountedOnly = mountedOnly;

        /*
         * No flags.
         * This goal only performs the attack action.
         */
    }

    @Override
    public boolean canUse() {
        if (this.mountedOnly && !this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        return AbstractPirateEntity.canPirateAttack(target)
                && this.pirate instanceof RangedAttackMob
                && this.pirate.distanceToSqr(target) <= this.attackRange * this.attackRange;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.attackCooldown = 10;
        this.seeTime = 0;
    }

    @Override
    public void stop() {
        this.seeTime = 0;
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

        boolean canSeeTarget = this.pirate.getSensing().hasLineOfSight(target);

        if (canSeeTarget) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        if (this.seeTime < 5) {
            return;
        }

        if (this.attackCooldown > 0) {
            return;
        }

        if (this.pirate instanceof RangedAttackMob rangedAttackMob) {
            rangedAttackMob.performRangedAttack(target, 1.0F);
            this.attackCooldown = this.attackCooldownTicks;
        }
    }
}