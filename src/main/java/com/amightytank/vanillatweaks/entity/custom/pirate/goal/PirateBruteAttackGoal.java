package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateBruteEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateBruteAttackGoal extends Goal {
    private final AbstractPirateBruteEntity brute;
    private final Settings settings;

    private int attackTick;
    private int cooldown;
    private boolean hasHit;

    public PirateBruteAttackGoal(AbstractPirateBruteEntity brute, Settings settings) {
        this.brute = brute;
        this.settings = settings;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public static Settings spear() {
        return new Settings(
                AbstractPirateBruteEntity.SPEAR_WINDUP,
                AbstractPirateBruteEntity.SPEAR_LUNGE,
                AbstractPirateBruteEntity.SPEAR_RECOVER,
                10,
                14,
                26,
                32,
                3.4D,
                0.72D,
                1.25D
        );
    }

    public static Settings axe() {
        return new Settings(
                AbstractPirateBruteEntity.AXE_WINDUP,
                AbstractPirateBruteEntity.AXE_CHOP,
                AbstractPirateBruteEntity.AXE_RECOVER,
                14,
                18,
                36,
                44,
                2.6D,
                0.52D,
                1.65D
        );
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
        this.brute.setAttackState(AbstractPirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
    }

    @Override
    public void stop() {
        this.attackTick = 0;
        this.hasHit = false;
        this.brute.setAttackState(AbstractPirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
        this.brute.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.brute.getTarget();
        if (target == null) return;

        double distanceSqr = this.brute.distanceToSqr(target);
        double attackRangeSqr = this.settings.attackRange * this.settings.attackRange;

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
                this.beginAttack();
            }
            return;
        }

        this.attackTick++;
        this.brute.setAttackTick(this.attackTick);

        if (this.attackTick < this.settings.lungeTick) {
            this.brute.setAttackState(this.settings.windupState);
            this.brute.getNavigation().stop();
        } else if (this.attackTick == this.settings.lungeTick) {
            this.brute.setAttackState(this.settings.hitState);
            this.lungeAt(target);
        } else if (this.attackTick > this.settings.lungeTick && this.attackTick <= this.settings.recoverStartTick) {
            this.brute.setAttackState(this.settings.hitState);

            if (!this.hasHit && distanceSqr <= attackRangeSqr + 1.5D) {
                this.hasHit = true;

                boolean hit = this.brute.doHurtTarget(target);

                if (hit && this.settings.knockback > 0.0D) {
                    Vec3 push = target.position().subtract(this.brute.position()).normalize().scale(this.settings.knockback);
                    target.push(push.x, 0.18D, push.z);
                    target.hurtMarked = true;
                }
            }
        } else if (this.attackTick < this.settings.totalAttackTicks) {
            this.brute.setAttackState(this.settings.recoverState);
        } else {
            this.endAttack();
        }
    }

    private void beginAttack() {
        this.attackTick = 0;
        this.hasHit = false;
        this.brute.setAttackState(this.settings.windupState);
        this.brute.setAttackTick(0);
        this.brute.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
    }

    private void endAttack() {
        this.attackTick = 0;
        this.hasHit = false;
        this.cooldown = this.settings.cooldownTicks;
        this.brute.setAttackState(AbstractPirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
    }

    private void lungeAt(LivingEntity target) {
        Vec3 direction = target.position().subtract(this.brute.position());

        if (direction.lengthSqr() > 0.001D) {
            direction = direction.normalize();

            double x = direction.x * this.settings.lungePower;
            double z = direction.z * this.settings.lungePower;

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

    public record Settings(
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
    }
}