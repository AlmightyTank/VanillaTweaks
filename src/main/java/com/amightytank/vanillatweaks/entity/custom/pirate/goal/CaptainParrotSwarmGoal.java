package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateParrotEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class CaptainParrotSwarmGoal extends Goal {
    private final PirateCaptainEntity captain;

    private int castTime;
    private int cooldown;

    public CaptainParrotSwarmGoal(PirateCaptainEntity captain) {
        this.captain = captain;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.captain.getTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        return this.captain.distanceToSqr(target) <= 32.0D * 32.0D;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.captain.getTarget();
        return this.castTime > 0 && target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.castTime = 30;
        this.captain.getNavigation().stop();
        this.captain.setAggressive(true);
    }

    @Override
    public void stop() {
        this.castTime = 0;
        this.cooldown = 240;
        this.captain.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.captain.getTarget();

        if (target == null) {
            return;
        }

        this.captain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.castTime--;

        if (this.castTime == 15 && !this.captain.level().isClientSide) {
            this.summonParrots(target);
        }
    }

    private void summonParrots(LivingEntity target) {
        Level level = this.captain.level();

        // Hide shoulder parrot model while swarm is active.
        if (this.captain.hasShoulderParrot()) {
            this.captain.setShoulderParrot(false);
            this.spawnShoulderParrot(level, target);
        }

        // Normal swarm parrots.
        for (int i = 0; i < 4; i++) {
            PirateParrotEntity parrot = ModEntities.PIRATE_PARROT.get().create(level);

            if (parrot == null) {
                continue;
            }

            double x = this.captain.getX() + this.captain.getRandom().nextDouble() * 4.0D - 2.0D;
            double y = this.captain.getY() + 1.5D + this.captain.getRandom().nextDouble();
            double z = this.captain.getZ() + this.captain.getRandom().nextDouble() * 4.0D - 2.0D;

            parrot.moveTo(x, y, z, this.captain.getRandom().nextFloat() * 360.0F, 0.0F);
            parrot.setOwner(this.captain);
            parrot.setTarget(target);
            parrot.setFromShoulder(false);

            level.addFreshEntity(parrot);
        }
    }

    private void spawnShoulderParrot(Level level, LivingEntity target) {
        PirateParrotEntity parrot = ModEntities.PIRATE_PARROT.get().create(level);

        if (parrot == null) {
            return;
        }

        double yawRad = Math.toRadians(this.captain.getYRot());

        double shoulderX = this.captain.getX() - Math.sin(yawRad) * 0.45D;
        double shoulderY = this.captain.getY() + 1.75D;
        double shoulderZ = this.captain.getZ() + Math.cos(yawRad) * 0.45D;

        parrot.moveTo(shoulderX, shoulderY, shoulderZ, this.captain.getYRot(), 0.0F);
        parrot.setOwner(this.captain);
        parrot.setTarget(target);
        parrot.setFromShoulder(true);

        level.addFreshEntity(parrot);
    }
}