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

    private static final int SMALL_CHASE_COUNT = 7;
    private static final int CIRCLE_STRIKE_COUNT = 3;

    /*
     * Distance from the player for the 3 big strike tentacles.
     * 1.75D - 2.25D usually looks good.
     */
    private static final double CIRCLE_RADIUS = 2.0D;

    /*
     * If your tentacles point the wrong way in game:
     * try 90.0F, -90.0F, or 180.0F.
     */
    private static final float TENTACLE_YAW_OFFSET = 0.0F;

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
            this.summonChaseThenCircleStrike(target);
        }
    }

    private void summonChaseThenCircleStrike(LivingEntity target) {
        double captainX = this.captain.getX();
        double captainZ = this.captain.getZ();

        double targetX = target.getX();
        double targetZ = target.getZ();

        double dx = targetX - captainX;
        double dz = targetZ - captainZ;

        double pathAngle = Math.atan2(dz, dx);
        double sideAngle = pathAngle + Math.PI / 2.0D;

        /*
         * Small chase tentacles.
         * These are spaced from the captain toward the player.
         */
        for (int i = 1; i <= SMALL_CHASE_COUNT; i++) {
            double progress = (double) i / (double) (SMALL_CHASE_COUNT + 1);

            double sideNoise = (this.captain.getRandom().nextDouble() - 0.5D) * 0.75D;

            double x = captainX + dx * progress + Math.cos(sideAngle) * sideNoise;
            double z = captainZ + dz * progress + Math.sin(sideAngle) * sideNoise;

            int delay = i * 3;

            float yaw = this.getYawFacing(x, z, targetX, targetZ);

            this.spawnTentacleAt(
                    x,
                    z,
                    delay,
                    KrakenTentacleEntity.TYPE_SMALL_CHASE,
                    yaw
            );
        }

        /*
         * 3 big strike tentacles.
         * These circle the player and face inward toward the player.
         */
        double startAngle = this.captain.getRandom().nextDouble() * Math.PI * 2.0D;

        for (int i = 0; i < CIRCLE_STRIKE_COUNT; i++) {
            double angle = startAngle + i * ((Math.PI * 2.0D) / CIRCLE_STRIKE_COUNT);

            double x = targetX + Math.cos(angle) * CIRCLE_RADIUS;
            double z = targetZ + Math.sin(angle) * CIRCLE_RADIUS;

            int delay = 26;

            float yaw = this.getYawFacing(x, z, targetX, targetZ);

            this.spawnTentacleAt(
                    x,
                    z,
                    delay,
                    KrakenTentacleEntity.TYPE_BIG_STRIKE,
                    yaw
            );
        }
    }

    private void spawnTentacleAt(double x, double z, int delay, int attackType, float yaw) {
        Level level = this.captain.level();

        double y = this.findGroundY(level, x, z);

        KrakenTentacleEntity tentacle = ModEntities.KRAKEN_TENTACLE.get().create(level);

        if (tentacle == null) {
            return;
        }

        tentacle.moveTo(x, y, z, yaw, 0.0F);

        tentacle.setOwner(this.captain);
        tentacle.setWarmupDelay(delay);
        tentacle.setAttackType(attackType);

        level.addFreshEntity(tentacle);
    }

    private float getYawFacing(double fromX, double fromZ, double toX, double toZ) {
        double dx = toX - fromX;
        double dz = toZ - fromZ;

        return Mth.wrapDegrees(
                (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F + TENTACLE_YAW_OFFSET
        );
    }

    private double findGroundY(Level level, double x, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(
                Mth.floor(x),
                Mth.floor(this.captain.getY()),
                Mth.floor(z)
        );

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