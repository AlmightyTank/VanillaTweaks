package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateParrotEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateParrotPeckGoal extends Goal {
    private final PirateParrotEntity parrot;
    private final double speed;

    private int attackCooldown;
    private int retreatTicks;

    public PirateParrotPeckGoal(PirateParrotEntity parrot, double speed) {
        this.parrot = parrot;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.parrot.shouldReturnToOwner()) {
            return false;
        }

        LivingEntity target = this.parrot.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.parrot.shouldReturnToOwner()) {
            return false;
        }

        LivingEntity target = this.parrot.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = this.parrot.getTarget();

        if (target == null || !target.isAlive()) {
            return;
        }

        this.parrot.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.retreatTicks > 0) {
            this.retreatTicks--;
            moveAwayFromTarget(target);
            return;
        }

        moveTowardTarget(target);

        double distance = this.parrot.distanceToSqr(target);

        // 2.25D = about 1.5 blocks away
        if (distance <= 2.25D && this.attackCooldown <= 0) {
            peckTarget(target);
        }
    }

    private void moveTowardTarget(LivingEntity target) {
        this.parrot.getMoveControl().setWantedPosition(
                target.getX(),
                target.getEyeY() + 0.15D,
                target.getZ(),
                this.speed
        );
    }

    private void moveAwayFromTarget(LivingEntity target) {
        Vec3 away = this.parrot.position().subtract(target.position());

        if (away.lengthSqr() < 0.01D) {
            away = new Vec3(
                    this.parrot.getRandom().nextDouble() - 0.5D,
                    0.2D,
                    this.parrot.getRandom().nextDouble() - 0.5D
            );
        }

        away = away.normalize();

        double retreatX = this.parrot.getX() + away.x * 2.0D;
        double retreatY = this.parrot.getY() + 0.8D;
        double retreatZ = this.parrot.getZ() + away.z * 2.0D;

        this.parrot.getMoveControl().setWantedPosition(
                retreatX,
                retreatY,
                retreatZ,
                this.speed * 1.15D
        );
    }

    private void peckTarget(LivingEntity target) {
        /*
         * Damage must happen on the server.
         * 2.0F = 1 heart before armor.
         * Use 1.0F if you want half-heart pecks.
         */
        if (!this.parrot.level().isClientSide) {
            target.hurt(this.parrot.damageSources().mobAttack(this.parrot), 2.0F);
        }

        // Tiny knock so the peck feels physical.
        double knockX = this.parrot.getX() - target.getX();
        double knockZ = this.parrot.getZ() - target.getZ();
        target.knockback(0.12D, knockX, knockZ);

        // Parrot bounces back after pecking.
        Vec3 bounce = this.parrot.position().subtract(target.position());

        if (bounce.lengthSqr() > 0.01D) {
            bounce = bounce.normalize();

            this.parrot.setDeltaMovement(
                    bounce.x * 0.35D,
                    0.24D,
                    bounce.z * 0.35D
            );

            this.parrot.hasImpulse = true;
        }

        this.attackCooldown = 14;
        this.retreatTicks = 8;
    }
}