package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateParrotEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.List;

public class CaptainParrotSwarmGoal extends Goal {
    private static final double PARROT_SWARM_RANGE = 48.0D;

    private static final int CAST_TIME_TICKS = 40;
    private static final int POST_CAST_COOLDOWN_TICKS = 20 * 5;

    private static final int MAX_ACTIVE_PARROTS = 5;

    private final PirateCaptainEntity captain;

    private int castTime;
    private int cooldown;
    private boolean spawned;

    public CaptainParrotSwarmGoal(PirateCaptainEntity captain) {
        this.captain = captain;

        /*
         * Captain should stare at the target while casting.
         */
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.captain.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return false;
        }

        if (this.captain.hasActiveParrotSwarm()) {
            return false;
        }

        if (this.captain.isCaptainSpellOnCooldown()) {
            return false;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        return this.captain.distanceToSqr(target) <= PARROT_SWARM_RANGE * PARROT_SWARM_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.captain.getTarget();

        return this.castTime > 0
                && AbstractPirateEntity.canPirateAttack(target)
                && this.captain.distanceToSqr(target) <= PARROT_SWARM_RANGE * PARROT_SWARM_RANGE;
    }

    @Override
    public void start() {
        this.castTime = CAST_TIME_TICKS;
        this.spawned = false;
        this.captain.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.castTime = 0;
        this.spawned = false;

        /*
         * Because the goal itself already lasted CAST_TIME_TICKS,
         * a 5 second post-cast cooldown means the next cast is:
         * goal length + 5 seconds later.
         */
        this.cooldown = POST_CAST_COOLDOWN_TICKS;
    }

    @Override
    public void tick() {
        LivingEntity target = this.captain.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        PirateLookHelper.lookAtEntity(this.captain, target);
        this.captain.getNavigation().stop();

        this.castTime--;

        if (!this.spawned && this.castTime <= CAST_TIME_TICKS / 2) {
            this.spawned = true;
            this.spawnParrotSwarm(target);
        }
    }

    private void spawnParrotSwarm(LivingEntity target) {
        Level level = this.captain.level();

        if (level.isClientSide) {
            return;
        }

        int activeParrots = this.countNearbyPirateParrots();
        int parrotsToSpawn = Math.max(0, MAX_ACTIVE_PARROTS - activeParrots);

        RandomSource random = this.captain.getRandom();

        for (int i = 0; i < parrotsToSpawn; i++) {
            PirateParrotEntity parrot = ModEntities.PIRATE_PARROT.get().create(level);

            if (parrot == null) {
                continue;
            }

            double angle = (Math.PI * 2.0D / Math.max(1, parrotsToSpawn)) * i;
            double radius = 1.5D + random.nextDouble() * 1.5D;

            double x = this.captain.getX() + Math.cos(angle) * radius;
            double y = this.captain.getY() + 1.0D + random.nextDouble();
            double z = this.captain.getZ() + Math.sin(angle) * radius;

            parrot.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);

            /*
             * These methods should match your PirateParrotEntity.
             * If your owner method has a different name, only rename this line.
             */
            parrot.setOwner(this.captain);
            parrot.setTarget(target);

            level.addFreshEntity(parrot);
        }
    }

    private int countNearbyPirateParrots() {
        List<PirateParrotEntity> parrots = this.captain.level().getEntitiesOfClass(
                PirateParrotEntity.class,
                this.captain.getBoundingBox().inflate(PARROT_SWARM_RANGE),
                PirateParrotEntity::isAlive
        );

        return parrots.size();
    }
}