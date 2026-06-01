package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class PirateMountedThrowAttackGoal extends Goal {
    private static final double ATTACK_RANGE = 28.0D;
    private static final int ATTACK_COOLDOWN_TICKS = 45;

    private final Mob pirate;

    private int attackCooldown;
    private int seeTime;

    public PirateMountedThrowAttackGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * Mounted ranged pressure.
         * LOOK only.
         *
         * Never claim MOVE while mounted.
         * PirateBoatPilotGoal owns MOVE.
         */
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        return AbstractPirateEntity.canPirateAttack(target)
                && this.pirate instanceof RangedAttackMob
                && this.pirate.distanceToSqr(target) <= ATTACK_RANGE * ATTACK_RANGE;
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

        PirateLookHelper.lookAtEntity(this.pirate, target);

        boolean canSeeTarget = this.pirate.getSensing().hasLineOfSight(target);

        if (canSeeTarget) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.seeTime < 5) {
            return;
        }

        if (this.attackCooldown > 0) {
            return;
        }

        if (this.pirate instanceof RangedAttackMob rangedAttackMob) {
            PirateLookHelper.lookAtEntity(this.pirate, target);
            rangedAttackMob.performRangedAttack(target, 1.0F);
            this.attackCooldown = ATTACK_COOLDOWN_TICKS;
        }
    }
}