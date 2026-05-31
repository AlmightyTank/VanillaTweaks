package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

public class PirateBoatPilotGoal extends Goal {
    private static final double BOAT_CHASE_SPEED = 0.08D;
    private static final double BOAT_HOLD_SPEED = 0.045D;
    private static final double MAX_BOAT_SPEED = 0.32D;

    private static final double BOARDING_STOP_RANGE = 9.0D;
    private static final double RANGED_STOP_RANGE = 20.0D;
    private static final double SAFE_LAND_HOLD_RANGE = 28.0D;

    private static final double TARGET_SEARCH_DISTANCE = 80.0D;

    /*
     * Natural steering:
     * Do not instantly snap the boat to the target.
     * These values make the boat visibly row/turn into position.
     */
    private static final float MAX_TURN_DEGREES_PER_TICK = 4.0F;
    private static final float SHARP_TURN_DEGREES = 22.0F;
    private static final float TURN_IN_PLACE_DEGREES = 70.0F;

    private final AbstractPirateEntity pirate;

    private LivingEntity target;
    private int retargetCooldown;

    public PirateBoatPilotGoal(AbstractPirateEntity pirate) {
        this.pirate = pirate;

        /*
         * Only claim MOVE.
         * Ranged/casting goals still need LOOK so they can aim.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(this.pirate.getVehicle() instanceof ModBoatEntity boat)) {
            return false;
        }

        if (!this.isSelectedDriver(boat)) {
            return false;
        }

        LivingEntity foundTarget = this.getOrFindBoatTarget(boat);

        if (!AbstractPirateEntity.canPirateAttack(foundTarget)) {
            return false;
        }

        this.target = foundTarget;
        this.shareTargetWithCrew(boat, foundTarget);

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(this.pirate.getVehicle() instanceof ModBoatEntity boat)) {
            return false;
        }

        if (!this.isSelectedDriver(boat)) {
            return false;
        }

        if (!AbstractPirateEntity.canPirateAttack(this.target)) {
            LivingEntity foundTarget = this.getOrFindBoatTarget(boat);

            if (!AbstractPirateEntity.canPirateAttack(foundTarget)) {
                return false;
            }

            this.target = foundTarget;
            this.shareTargetWithCrew(boat, foundTarget);
        }

        return true;
    }

    @Override
    public void start() {
        this.retargetCooldown = 0;
    }

    @Override
    public void stop() {
        this.target = null;
        this.retargetCooldown = 0;
    }

    @Override
    public void tick() {
        if (!(this.pirate.getVehicle() instanceof ModBoatEntity boat)) {
            return;
        }

        if (!this.isSelectedDriver(boat)) {
            return;
        }

        if (!AbstractPirateEntity.canPirateAttack(this.target)) {
            this.target = this.getOrFindBoatTarget(boat);

            if (!AbstractPirateEntity.canPirateAttack(this.target)) {
                slowBoat(boat);
                return;
            }
        }

        boolean targetSafeOnLand = PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.pirate, this.target);
        double stopRange = getBoatStopRange(boat, targetSafeOnLand);

        Vec3 boatPos = boat.position();
        Vec3 targetPos = this.target.position();

        double distanceSqr = boat.distanceToSqr(this.target);
        double stopRangeSqr = stopRange * stopRange;

        Vec3 moveDirection;

        if (targetSafeOnLand) {
            /*
             * Player escaped inland:
             * - do not beach the boat
             * - hold offshore
             * - circle instead of driving directly into land
             */
            if (distanceSqr < stopRangeSqr) {
                moveDirection = PirateRaidAiUtil.horizontalAwayFrom(targetPos, boatPos);
            } else if (distanceSqr > (stopRange + 8.0D) * (stopRange + 8.0D)) {
                moveDirection = PirateRaidAiUtil.horizontalDirection(boatPos, targetPos);
            } else {
                moveDirection = getCircleDirection(boatPos, targetPos);
            }

            moveBoatSafely(boat, moveDirection, BOAT_HOLD_SPEED);
            return;
        }

        /*
         * Normal chase while the player is in water, on a boat, or near shore.
         */
        if (distanceSqr > stopRangeSqr) {
            moveDirection = PirateRaidAiUtil.horizontalDirection(boatPos, targetPos);
            moveBoatSafely(boat, moveDirection, BOAT_CHASE_SPEED);
        } else {
            slowBoat(boat);
        }
    }

    private boolean isSelectedDriver(ModBoatEntity boat) {
        Entity selectedDriver = this.getSelectedDriver(boat);
        return selectedDriver == this.pirate;
    }

    private Entity getSelectedDriver(ModBoatEntity boat) {
        Entity bestDriver = null;
        int bestPriority = Integer.MIN_VALUE;

        for (Entity passenger : boat.getPassengers()) {
            if (!(passenger instanceof AbstractPirateEntity piratePassenger)) {
                continue;
            }

            if (!passenger.isAlive()) {
                continue;
            }

            int priority = getDriverPriority(piratePassenger);

            if (priority > bestPriority) {
                bestPriority = priority;
                bestDriver = passenger;
            }
        }

        return bestDriver;
    }

    private int getDriverPriority(AbstractPirateEntity piratePassenger) {
        if (PirateRaidAiUtil.isCaptain(piratePassenger)) {
            return 100;
        }

        if (PirateRaidAiUtil.isRanged(piratePassenger)) {
            return 80;
        }

        if (PirateRaidAiUtil.isBoarder(piratePassenger)) {
            return 60;
        }

        if (PirateRaidAiUtil.isRaidPirate(piratePassenger)) {
            return 50;
        }

        return 10;
    }

    private LivingEntity getOrFindBoatTarget(ModBoatEntity boat) {
        LivingEntity currentTarget = this.pirate.getTarget();

        if (AbstractPirateEntity.canPirateAttack(currentTarget)) {
            return currentTarget;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Mob mob) {
                LivingEntity crewTarget = mob.getTarget();

                if (AbstractPirateEntity.canPirateAttack(crewTarget)) {
                    this.pirate.setTarget(crewTarget);
                    return crewTarget;
                }
            }
        }

        if (boat.level() instanceof ServerLevel serverLevel) {
            CompoundTag boatData = boat.getPersistentData();

            if (boatData.hasUUID(PirateRaidAiUtil.TARGET_UUID_TAG)) {
                UUID targetId = boatData.getUUID(PirateRaidAiUtil.TARGET_UUID_TAG);
                Entity entity = serverLevel.getEntity(targetId);

                if (entity instanceof LivingEntity livingEntity
                        && AbstractPirateEntity.canPirateAttack(livingEntity)) {
                    this.pirate.setTarget(livingEntity);
                    return livingEntity;
                }
            }
        }

        if (this.retargetCooldown > 0) {
            this.retargetCooldown--;
            return null;
        }

        this.retargetCooldown = 20;

        Player player = this.pirate.level().getNearestPlayer(
                this.pirate.getX(),
                this.pirate.getY(),
                this.pirate.getZ(),
                TARGET_SEARCH_DISTANCE,
                entity -> entity instanceof Player foundPlayer
                        && foundPlayer.isAlive()
                        && !foundPlayer.isCreative()
                        && !foundPlayer.isSpectator()
        );

        if (player != null) {
            this.pirate.setTarget(player);
            boat.getPersistentData().putUUID(PirateRaidAiUtil.TARGET_UUID_TAG, player.getUUID());
            return player;
        }

        return null;
    }

    private void shareTargetWithCrew(ModBoatEntity boat, LivingEntity target) {
        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        boat.getPersistentData().putUUID(PirateRaidAiUtil.TARGET_UUID_TAG, target.getUUID());

        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Mob mob) {
                mob.setTarget(target);
            }
        }
    }

    private double getBoatStopRange(ModBoatEntity boat, boolean targetSafeOnLand) {
        if (targetSafeOnLand) {
            return SAFE_LAND_HOLD_RANGE;
        }

        if (PirateRaidAiUtil.boatHasBoarders(boat)) {
            return BOARDING_STOP_RANGE;
        }

        if (PirateRaidAiUtil.boatHasRangedPirates(boat)) {
            return RANGED_STOP_RANGE;
        }

        return RANGED_STOP_RANGE;
    }

    private void moveBoatSafely(ModBoatEntity boat, Vec3 direction, double speed) {
        if (direction.lengthSqr() < 0.0001D) {
            slowBoat(boat);
            return;
        }

        Vec3 wantedDirection = direction.normalize();

        /*
         * If the desired path would beach the boat, try turning along the shore.
         */
        if (!PirateRaidAiUtil.isWaterAhead(boat.level(), boat.position(), wantedDirection, 3.0D)) {
            Vec3 left = PirateRaidAiUtil.leftOf(wantedDirection);
            Vec3 right = PirateRaidAiUtil.rightOf(wantedDirection);

            boolean leftWater = PirateRaidAiUtil.isWaterAhead(boat.level(), boat.position(), left, 3.0D);
            boolean rightWater = PirateRaidAiUtil.isWaterAhead(boat.level(), boat.position(), right, 3.0D);

            if (leftWater) {
                wantedDirection = left.normalize();
            } else if (rightWater) {
                wantedDirection = right.normalize();
            } else {
                slowBoat(boat);
                boat.setPaddleState(false, false);
                return;
            }
        }

        /*
         * Turn first. The boat then moves based on its actual facing direction.
         * This removes the unnatural sideways sliding.
         */
        float remainingTurn = turnBoatToward(boat, wantedDirection);
        Vec3 forward = getBoatForwardDirection(boat);

        double alignment = Mth.clamp(forward.dot(wantedDirection), -1.0D, 1.0D);

        /*
         * If the boat is not facing the target yet, it should mostly turn,
         * not instantly shove sideways toward the target.
         */
        double thrustScale = Mth.clamp((alignment + 0.15D) / 1.15D, 0.0D, 1.0D);

        if (Math.abs(remainingTurn) > TURN_IN_PLACE_DEGREES) {
            thrustScale *= 0.25D;
        }

        /*
         * If the boat's nose is pointed at land, row to turn but do not push forward.
         */
        if (!PirateRaidAiUtil.isWaterAhead(boat.level(), boat.position(), forward, 2.25D)) {
            thrustScale = 0.0D;
        }

        Vec3 currentMotion = boat.getDeltaMovement();

        double drag = thrustScale > 0.01D ? 0.82D : 0.90D;
        Vec3 wantedMotion = currentMotion.scale(drag).add(forward.scale(speed * thrustScale));

        if (wantedMotion.horizontalDistanceSqr() > MAX_BOAT_SPEED * MAX_BOAT_SPEED) {
            Vec3 horizontal = new Vec3(wantedMotion.x, 0.0D, wantedMotion.z)
                    .normalize()
                    .scale(MAX_BOAT_SPEED);

            wantedMotion = new Vec3(horizontal.x, wantedMotion.y, horizontal.z);
        }

        boat.setDeltaMovement(wantedMotion);
        applyPaddleState(boat, remainingTurn, thrustScale > 0.01D);
    }

    private void slowBoat(ModBoatEntity boat) {
        Vec3 motion = boat.getDeltaMovement();
        boat.setDeltaMovement(motion.x * 0.8D, motion.y, motion.z * 0.8D);
        boat.setPaddleState(false, false);
    }

    private Vec3 getCircleDirection(Vec3 boatPos, Vec3 targetPos) {
        Vec3 towardTarget = PirateRaidAiUtil.horizontalDirection(boatPos, targetPos);

        if (towardTarget.lengthSqr() < 0.0001D) {
            return Vec3.ZERO;
        }

        return PirateRaidAiUtil.leftOf(towardTarget).normalize();
    }

    private float turnBoatToward(ModBoatEntity boat, Vec3 direction) {
        if (direction.lengthSqr() < 0.0001D) {
            return 0.0F;
        }

        float wantedYaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float currentYaw = boat.getYRot();

        float yawDifference = Mth.wrapDegrees(wantedYaw - currentYaw);
        float clampedTurn = Mth.clamp(
                yawDifference,
                -MAX_TURN_DEGREES_PER_TICK,
                MAX_TURN_DEGREES_PER_TICK
        );

        boat.setYRot(currentYaw + clampedTurn);

        /*
         * Do not set yRotO here.
         * Let Minecraft interpolate the rotation instead of visually snapping it.
         */

        return yawDifference;
    }

    private Vec3 getBoatForwardDirection(ModBoatEntity boat) {
        double radians = Math.toRadians(boat.getYRot() + 90.0F);
        return new Vec3(Math.cos(radians), 0.0D, Math.sin(radians)).normalize();
    }

    private void applyPaddleState(ModBoatEntity boat, float remainingTurn, boolean movingForward) {
        float absTurn = Math.abs(remainingTurn);

        if (absTurn > SHARP_TURN_DEGREES) {
            /*
             * One-oar steering for sharp turns.
             *
             * If this looks backwards in-game, swap these two setPaddleState calls.
             */
            if (remainingTurn > 0.0F) {
                boat.setPaddleState(true, false);
            } else {
                boat.setPaddleState(false, true);
            }

            return;
        }

        if (movingForward) {
            boat.setPaddleState(true, true);
            return;
        }

        if (absTurn > 3.0F) {
            if (remainingTurn > 0.0F) {
                boat.setPaddleState(true, false);
            } else {
                boat.setPaddleState(false, true);
            }

            return;
        }

        boat.setPaddleState(false, false);
    }
}