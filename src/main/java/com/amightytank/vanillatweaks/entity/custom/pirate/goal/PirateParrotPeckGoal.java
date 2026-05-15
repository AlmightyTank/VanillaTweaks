package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateParrotEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PirateParrotPeckGoal extends Goal {
    private final PirateParrotEntity parrot;
    private final double speed;
    private int attackCooldown;

    public PirateParrotPeckGoal(PirateParrotEntity parrot, double speed) {
        this.parrot = parrot;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.parrot.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.parrot.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = this.parrot.getTarget();

        if (target == null) {
            return;
        }

        this.parrot.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.parrot.getMoveControl().setWantedPosition(
                target.getX(),
                target.getEyeY(),
                target.getZ(),
                this.speed
        );

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        double distance = this.parrot.distanceToSqr(target);

        if (distance <= 1.4D && this.attackCooldown <= 0) {
            target.hurt(this.parrot.damageSources().mobAttack(this.parrot), 2.0F);
            this.attackCooldown = 20;
        }
    }
}