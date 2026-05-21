package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateBruteEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateBruteAttackGoal extends Goal {
    private final PirateBruteEntity brute;

    private int attackTick;
    private int cooldown;
    private boolean hasHit;

    public PirateBruteAttackGoal(PirateBruteEntity brute) {
        this.brute = brute;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.brute.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.brute.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.attackTick = 0;
        this.cooldown = 10;
        this.hasHit = false;

        this.brute.setAggressive(true);
        this.brute.setAttackState(PirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
    }

    @Override
    public void stop() {
        this.attackTick = 0;
        this.hasHit = false;

        this.brute.setAggressive(false);
        this.brute.setAttackState(PirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
        this.brute.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.brute.getTarget();
        if (target == null) {
            return;
        }

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            this.brute.setTarget(null);
            return;
        }

        this.brute.setAggressive(true);

        Settings settings = this.brute.isSpearBrute() ? Settings.spear() : Settings.axe();

        double distanceSqr = this.brute.distanceToSqr(target);
        double attackRangeSqr = settings.attackRange * settings.attackRange;

        this.brute.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (this.cooldown > 0 && !this.brute.isBruteAttacking()) {
            this.cooldown--;
        }

        if (this.brute.isInWater()) {
            this.swimTowardTarget(target);
        }

        if (!this.brute.isBruteAttacking()) {
            if (distanceSqr > attackRangeSqr) {
                this.brute.getNavigation().moveTo(target, this.brute.isInWater() ? 1.45D : 1.18D);
            } else if (this.cooldown <= 0) {
                this.beginAttack(settings);
            }

            return;
        }

        this.attackTick++;
        this.brute.setAttackTick(this.attackTick);

        if (this.attackTick < settings.lungeTick) {
            this.brute.setAttackState(settings.windupState);
            this.brute.getNavigation().stop();
        } else if (this.attackTick == settings.lungeTick) {
            this.brute.setAttackState(settings.hitState);
            this.lungeAt(target, settings);
        } else if (this.attackTick > settings.lungeTick && this.attackTick <= settings.recoverStartTick) {
            this.brute.setAttackState(settings.hitState);

            if (!this.hasHit && distanceSqr <= attackRangeSqr + 1.5D) {
                this.hasHit = true;

                if (!AbstractPirateEntity.canPirateAttack(target)) {
                    return;
                }
                boolean hit = this.brute.doHurtTarget(target);

                if (hit && settings.knockback > 0.0D) {
                    Vec3 push = target.position().subtract(this.brute.position()).normalize().scale(settings.knockback);
                    target.push(push.x, 0.18D, push.z);
                    target.hurtMarked = true;
                }
            }
        } else if (this.attackTick < settings.totalAttackTicks) {
            this.brute.setAttackState(settings.recoverState);
        } else {
            this.endAttack(settings);
        }
    }

    private void beginAttack(Settings settings) {
        this.attackTick = 0;
        this.hasHit = false;

        this.brute.setAttackState(settings.windupState);
        this.brute.setAttackTick(0);
        this.brute.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
    }

    private void endAttack(Settings settings) {
        this.attackTick = 0;
        this.hasHit = false;
        this.cooldown = settings.cooldownTicks;

        this.brute.setAttackState(PirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
    }

    private void lungeAt(LivingEntity target, Settings settings) {
        Vec3 direction = target.position().subtract(this.brute.position());

        if (direction.lengthSqr() > 0.001D) {
            direction = direction.normalize();

            double x = direction.x * settings.lungePower;
            double z = direction.z * settings.lungePower;

            this.brute.setDeltaMovement(
                    this.brute.getDeltaMovement().add(x, this.brute.onGround() ? 0.12D : 0.04D, z)
            );

            this.brute.hurtMarked = true;
        }
    }

    private void swimTowardTarget(LivingEntity target) {
        Vec3 direction = target.position().subtract(this.brute.position());

        if (direction.lengthSqr() > 0.001D) {
            direction = direction.normalize();

            double x = direction.x * 0.035D;
            double y = Mth.clamp(direction.y * 0.035D, -0.02D, 0.04D);
            double z = direction.z * 0.035D;

            this.brute.setDeltaMovement(this.brute.getDeltaMovement().add(x, y, z));
        }
    }

    private record Settings(
            int windupState,
            int hitState,
            int recoverState,
            int lungeTick,
            int recoverStartTick,
            int totalAttackTicks,
            int cooldownTicks,
            double attackRange,
            double lungePower,
            double knockback
    ) {
        private static Settings spear() {
            return new Settings(
                    PirateBruteEntity.SPEAR_WINDUP,
                    PirateBruteEntity.SPEAR_LUNGE,
                    PirateBruteEntity.SPEAR_RECOVER,
                    10,
                    14,
                    26,
                    30,
                    3.4D,
                    0.72D,
                    1.15D
            );
        }

        private static Settings axe() {
            return new Settings(
                    PirateBruteEntity.AXE_WINDUP,
                    PirateBruteEntity.AXE_CHOP,
                    PirateBruteEntity.AXE_RECOVER,
                    14,
                    18,
                    36,
                    42,
                    2.6D,
                    0.52D,
                    1.65D
            );
        }
    }
}