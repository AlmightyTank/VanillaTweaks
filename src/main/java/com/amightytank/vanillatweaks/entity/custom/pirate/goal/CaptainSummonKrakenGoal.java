package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class CaptainSummonKrakenGoal extends Goal {
    private final PirateCaptainEntity captain;

    private int castTime;
    private int cooldown;

    public CaptainSummonKrakenGoal(PirateCaptainEntity captain) {
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

        return this.captain.distanceToSqr(target) <= 24.0D * 24.0D;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.captain.getTarget();
        return this.castTime > 0 && target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.castTime = 35;
        this.captain.getNavigation().stop();
        this.captain.setAggressive(true);
    }

    @Override
    public void stop() {
        this.castTime = 0;
        this.cooldown = 140;
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

        if (this.castTime == 18 && !this.captain.level().isClientSide) {
            this.summonEvokerStyleTentacles(target);
        }
    }

    private void summonEvokerStyleTentacles(LivingEntity target) {
        double captainX = this.captain.getX();
        double captainZ = this.captain.getZ();

        double targetX = target.getX();
        double targetZ = target.getZ();

        double angle = Math.atan2(targetZ - captainZ, targetX - captainX);

        /*
         * Main line of tentacles toward target.
         */
        for (int i = 1; i <= 8; i++) {
            double distance = 1.25D * i;

            double x = captainX + Math.cos(angle) * distance;
            double z = captainZ + Math.sin(angle) * distance;

            int delay = i * 2;

            this.spawnTentacleAt(x, z, angle, delay);
        }

        /*
         * Small side spread near the target, like Evoker's wider fang burst.
         */
        for (int i = 0; i < 5; i++) {
            double sideAngle = angle + Mth.PI / 2.0F;
            double sideOffset = (i - 2) * 1.2D;

            double forwardDistance = 8.5D;

            double x = captainX
                    + Math.cos(angle) * forwardDistance
                    + Math.cos(sideAngle) * sideOffset;

            double z = captainZ
                    + Math.sin(angle) * forwardDistance
                    + Math.sin(sideAngle) * sideOffset;

            int delay = 18 + i;

            this.spawnTentacleAt(x, z, angle, delay);
        }
    }

    private void spawnTentacleAt(double x, double z, double angle, int delay) {
        Level level = this.captain.level();

        double y = this.findGroundY(level, x, z);

        KrakenTentacleEntity tentacle = ModEntities.KRAKEN_TENTACLE.get().create(level);

        if (tentacle == null) {
            return;
        }

        float yaw = (float) (angle * 180.0D / Math.PI) - 90.0F;

        tentacle.moveTo(x, y, z, yaw, 0.0F);
        tentacle.setOwner(this.captain);
        tentacle.setWarmupDelay(delay);

        level.addFreshEntity(tentacle);
    }

    private double findGroundY(Level level, double x, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(
                Mth.floor(x),
                Mth.floor(this.captain.getY()),
                Mth.floor(z)
        );

        /*
         * Search downward until we find a solid block or water surface area.
         */
        for (int i = 0; i < 8; i++) {
            BlockState stateBelow = level.getBlockState(pos.below());

            if (stateBelow.isSolidRender(level, pos.below()) || !level.getFluidState(pos.below()).isEmpty()) {
                return pos.getY();
            }

            pos.move(0, -1, 0);
        }

        return this.captain.getY();
    }
}