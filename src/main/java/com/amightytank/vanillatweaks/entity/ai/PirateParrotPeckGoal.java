package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateParrotEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateParrotPeckGoal extends Goal {
    private static final double PECK_DISTANCE = 1.6D;
    private static final float PECK_DAMAGE = 2.0F;

    private final PirateParrotEntity parrot;
    private final double speed;

    private int attackCooldown;
    private int retreatTicks;

    public PirateParrotPeckGoal(PirateParrotEntity parrot, double speed) {
        this.parrot = parrot;
        this.speed = speed;

        /*
         * MOVE = fly/chase.
         * LOOK = face the target while diving/pecking.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.parrot.shouldReturnToOwner()) {
            return false;
        }

        LivingEntity target = this.parrot.getTarget();
        return AbstractPirateEntity.canPirateAttack(target);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.parrot.shouldReturnToOwner()) {
            return false;
        }

        LivingEntity target = this.parrot.getTarget();
        return AbstractPirateEntity.canPirateAttack(target);
    }

    @Override
    public void start() {
        this.attackCooldown = 0;
        this.retreatTicks = 0;
    }

    @Override
    public void stop() {
        this.parrot.getNavigation().stop();
        this.retreatTicks = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.parrot.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        PirateLookHelper.lookAtEntity(this.parrot, target);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.retreatTicks > 0) {
            this.retreatTicks--;
            this.flyAwayFromTarget(target);
            return;
        }

        double distanceSqr = this.parrot.distanceToSqr(target);

        if (distanceSqr <= PECK_DISTANCE * PECK_DISTANCE) {
            this.parrot.getNavigation().stop();

            if (this.attackCooldown <= 0) {
                target.hurt(this.parrot.damageSources().mobAttack(this.parrot), PECK_DAMAGE);

                this.attackCooldown = 18;
                this.retreatTicks = 10;
            }

            return;
        }

        this.parrot.getNavigation().moveTo(target, this.speed);
    }

    private void flyAwayFromTarget(LivingEntity target) {
        Vec3 away = this.parrot.position()
                .subtract(target.position())
                .normalize();

        if (away.lengthSqr() < 0.01D) {
            away = new Vec3(
                    this.parrot.getRandom().nextDouble() - 0.5D,
                    0.4D,
                    this.parrot.getRandom().nextDouble() - 0.5D
            ).normalize();
        }

        Vec3 retreatPos = this.parrot.position().add(
                away.x * 3.0D,
                1.2D,
                away.z * 3.0D
        );

        this.parrot.getMoveControl().setWantedPosition(
                retreatPos.x,
                retreatPos.y,
                retreatPos.z,
                this.speed
        );
    }
}