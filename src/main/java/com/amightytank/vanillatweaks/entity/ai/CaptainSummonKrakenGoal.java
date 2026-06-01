package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
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

    private static final int SHARED_SPELL_COOLDOWN_TIME = 100;

    private static final int CIRCLE_STRIKE_COUNT = 3;
    private static final double CIRCLE_RADIUS = 2.0D;
    private static final float TENTACLE_YAW_OFFSET = 0.0F;

    private static final double KRAKEN_ATTACK_RANGE = 48.0D;

    /*
     * Faster summon timing only.
     */
    private static final int MODEL_SHOW_DELAY_TICKS = 20; // was 60
    private static final int SMALL_CHASE_DELAY_STEP = 2;  // was i * 3
    private static final int BIG_STRIKE_DELAY = 14;
    private static final int CAST_TIME = 24;              // was 35
    private static final int EXTRA_SHARED_COOLDOWN_TICKS = 20 * 5;
    private static final int SUMMON_TIME = 12;            // was 18
    private static final int COOLDOWN_TIME = 100;         // was 140

    public CaptainSummonKrakenGoal(PirateCaptainEntity captain) {
        this.captain = captain;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
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

        if (this.captain.isCaptainSpellOnCooldown()) {
            return false;
        }

        if (this.captain.hasActiveParrotSwarm()) {
            return false;
        }

        return this.captain.distanceToSqr(target) <= KRAKEN_ATTACK_RANGE * KRAKEN_ATTACK_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.captain.getTarget();
        return this.castTime > 0 && target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.castTime = CAST_TIME;

        if (!this.captain.level().isClientSide) {
            this.captain.setCaptainSpellCooldown(CAST_TIME + EXTRA_SHARED_COOLDOWN_TICKS);
        }

        this.captain.getNavigation().stop();
        this.captain.setAggressive(true);
    }

    @Override
    public void stop() {
        this.castTime = 0;
        this.cooldown = COOLDOWN_TIME;

        if (!this.captain.level().isClientSide) {
            this.captain.setCaptainSpellCooldown(SHARED_SPELL_COOLDOWN_TIME);
        }

        this.captain.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.captain.getTarget();

        if (target == null) {
            return;
        }

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            this.captain.setTarget(null);
            return;
        }

        this.captain.getLookControl().setLookAt(target);
        this.castTime--;

        if (this.castTime == SUMMON_TIME && !this.captain.level().isClientSide) {
            this.spawnBasicKrakenAttack(target);
        }
    }

    private void spawnBasicKrakenAttack(LivingEntity target) {
        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        double captainX = this.captain.getX();
        double captainZ = this.captain.getZ();

        /*
         * This is the center of the circle.
         * All big tentacles point toward this point.
         */
        double centerX = target.getX();
        double centerZ = target.getZ();

        double dx = centerX - captainX;
        double dz = centerZ - captainZ;

        /*
         * Small chase tentacles.
         * Keep these straight for now.
         */

        for (int i = 1; i <= 6; i++) {
            double progress = (double) i / 7.0D;

            double x = captainX + dx * progress;
            double z = captainZ + dz * progress;

            this.spawnTentacleAt(
                    x,
                    z,
                    i * SMALL_CHASE_DELAY_STEP,
                    KrakenTentacleEntity.TYPE_SMALL_CHASE,
                    0.0F
            );
        }

        double startAngle = this.captain.getRandom().nextDouble() * Math.PI * 2.0D;

        for (int i = 0; i < CIRCLE_STRIKE_COUNT; i++) {
            double angle = startAngle + i * ((Math.PI * 2.0D) / CIRCLE_STRIKE_COUNT);

            double x = centerX + Math.cos(angle) * CIRCLE_RADIUS;
            double z = centerZ + Math.sin(angle) * CIRCLE_RADIUS;

            float inwardYaw = this.getYawFacingPoint(x, z, centerX, centerZ);

            this.spawnTentacleAt(
                    x,
                    z,
                    BIG_STRIKE_DELAY,
                    KrakenTentacleEntity.TYPE_BIG_STRIKE,
                    inwardYaw
            );
        }
    }

    private void spawnTentacleAt(double x, double z, int warmupDelay, int attackType, float yaw) {
        Level level = this.captain.level();

        KrakenTentacleEntity tentacle = ModEntities.KRAKEN_TENTACLE.get().create(level);

        if (tentacle == null) {
            return;
        }

        double y = this.findGroundY(level, x, z);

        tentacle.moveTo(x, y, z, yaw, 0.0F);
        tentacle.setYRot(yaw);
        tentacle.yRotO = yaw;

        tentacle.setOwner(this.captain);
        tentacle.setWarmupDelay(MODEL_SHOW_DELAY_TICKS + BIG_STRIKE_DELAY);
        tentacle.setAttackType(attackType);

        level.addFreshEntity(tentacle);
    }

    private float getYawFacingPoint(double fromX, double fromZ, double toX, double toZ) {
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