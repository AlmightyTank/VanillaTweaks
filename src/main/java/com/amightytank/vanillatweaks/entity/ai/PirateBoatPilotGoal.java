package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.ai.util.PirateRaidAiUtil;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class PirateBoatPilotGoal extends Goal {
    private static final String RAID_PIRATE_TAG = "PirateTreasureRaid";
    private static final String BOARDER_TAG = "PirateRaidBoarder";

    public static final String RETURN_BOAT_UUID_TAG = "PirateReturnBoatUUID";

    private static final double BOARDING_STOP_RANGE = 9.0D;
    private static final double RANGED_STOP_RANGE = 20.0D;
    private static final double SAFE_LAND_HOLD_RANGE = 28.0D;

    /*
     * How far a boat looks for its own missing boarders.
     * This does NOT mean it accepts any nearby boarder.
     * The boarder must be assigned to this exact boat UUID.
     */
    private static final double CREW_WAIT_SEARCH_RANGE = 52.0D;
    private static final double CREW_PICKUP_MOVE_RANGE = 7.0D;

    private static final double TARGET_SEARCH_DISTANCE = 80.0D;

    private static final double WATER_CHECK_DISTANCE = 3.0D;
    private static final double FORWARD_WATER_CHECK_DISTANCE = 2.25D;

    private static final float STEER_DEAD_ZONE_DEGREES = 4.0F;
    private static final float FORWARD_MAX_TURN_DEGREES = 145.0F;

    private static final double BRAKE_SPEED_SQR = 0.0009D;

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
        if (this.pirate.getVehicle() instanceof ModBoatEntity boat) {
            boat.clearPirateRaidInput();
        }

        this.target = null;
        this.retargetCooldown = 0;
    }

    @Override
    public void tick() {
        if (!(this.pirate.getVehicle() instanceof ModBoatEntity boat)) {
            return;
        }

        if (!this.isSelectedDriver(boat)) {
            boat.clearPirateRaidInput();
            return;
        }

        if (!AbstractPirateEntity.canPirateAttack(this.target)) {
            this.target = this.getOrFindBoatTarget(boat);

            if (!AbstractPirateEntity.canPirateAttack(this.target)) {
                this.brakeBoatLikePlayer(boat);
                return;
            }
        }

        /*
         * Before chasing the player, check if THIS boat has one of ITS OWN assigned
         * boarders still on foot.
         *
         * Important:
         * This no longer uses nearest-boarder fallback.
         * A boat only waits for pirates whose saved boat UUID equals this boat UUID.
         */
        AbstractPirateEntity dismountedMate = this.findDismountedMateForBoat(boat);

        if (dismountedMate != null) {
            double mateDistanceSqr = boat.distanceToSqr(dismountedMate);

            if (mateDistanceSqr > CREW_PICKUP_MOVE_RANGE * CREW_PICKUP_MOVE_RANGE) {
                Vec3 moveDirection = PirateRaidAiUtil.horizontalDirection(
                        boat.position(),
                        dismountedMate.position()
                );

                this.steerBoatLikePlayer(boat, moveDirection, true);
            } else {
                this.brakeBoatLikePlayer(boat);
            }

            return;
        }

        boolean targetSafeOnLand = PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.pirate, this.target);
        double stopRange = this.getBoatStopRange(boat, targetSafeOnLand);

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
                this.steerBoatLikePlayer(boat, moveDirection, true);
            } else if (distanceSqr > (stopRange + 8.0D) * (stopRange + 8.0D)) {
                moveDirection = PirateRaidAiUtil.horizontalDirection(boatPos, targetPos);
                this.steerBoatLikePlayer(boat, moveDirection, true);
            } else {
                moveDirection = this.getCircleDirection(boatPos, targetPos);
                this.steerBoatLikePlayer(boat, moveDirection, true);
            }

            return;
        }

        /*
         * Normal chase while the player is in water, on a boat, or near shore.
         */
        if (distanceSqr > stopRangeSqr) {
            moveDirection = PirateRaidAiUtil.horizontalDirection(boatPos, targetPos);
            this.steerBoatLikePlayer(boat, moveDirection, true);
        } else {
            this.brakeBoatLikePlayer(boat);
        }
    }

    private void steerBoatLikePlayer(ModBoatEntity boat, Vec3 direction, boolean allowForward) {
        if (direction.lengthSqr() < 0.0001D) {
            this.brakeBoatLikePlayer(boat);
            return;
        }

        Vec3 wantedDirection = this.getWaterSafeDirection(boat, direction.normalize());

        if (wantedDirection.lengthSqr() < 0.0001D) {
            this.brakeBoatLikePlayer(boat);
            return;
        }

        float turnDegrees = this.getYawDifferenceTo(boat, wantedDirection);
        float absTurnDegrees = Math.abs(turnDegrees);

        boolean left = turnDegrees < -STEER_DEAD_ZONE_DEGREES;
        boolean right = turnDegrees > STEER_DEAD_ZONE_DEGREES;

        Vec3 forward = this.getBoatForwardDirection(boat);

        boolean facingCloseEnoughToRowForward = absTurnDegrees <= FORWARD_MAX_TURN_DEGREES;
        boolean waterInFront = PirateRaidAiUtil.isWaterAhead(
                boat.level(),
                boat.position(),
                forward,
                FORWARD_WATER_CHECK_DISTANCE
        );

        boolean forwardInput = allowForward && facingCloseEnoughToRowForward && waterInFront;

        boat.setPirateRaidInput(left, right, forwardInput, false);
    }

    private Vec3 getWaterSafeDirection(ModBoatEntity boat, Vec3 wantedDirection) {
        if (PirateRaidAiUtil.isWaterAhead(boat.level(), boat.position(), wantedDirection, WATER_CHECK_DISTANCE)) {
            return wantedDirection;
        }

        Vec3 left = PirateRaidAiUtil.leftOf(wantedDirection).normalize();
        Vec3 right = PirateRaidAiUtil.rightOf(wantedDirection).normalize();

        boolean leftWater = PirateRaidAiUtil.isWaterAhead(boat.level(), boat.position(), left, WATER_CHECK_DISTANCE);
        boolean rightWater = PirateRaidAiUtil.isWaterAhead(boat.level(), boat.position(), right, WATER_CHECK_DISTANCE);

        if (leftWater && rightWater) {
            float leftTurn = Math.abs(this.getYawDifferenceTo(boat, left));
            float rightTurn = Math.abs(this.getYawDifferenceTo(boat, right));

            return leftTurn <= rightTurn ? left : right;
        }

        if (leftWater) {
            return left;
        }

        if (rightWater) {
            return right;
        }

        return Vec3.ZERO;
    }

    private void brakeBoatLikePlayer(ModBoatEntity boat) {
        if (boat.getDeltaMovement().horizontalDistanceSqr() > BRAKE_SPEED_SQR) {
            boat.setPirateRaidInput(false, false, false, true);
        } else {
            boat.clearPirateRaidInput();
        }
    }

    private float getYawDifferenceTo(ModBoatEntity boat, Vec3 direction) {
        if (direction.lengthSqr() < 0.0001D) {
            return 0.0F;
        }

        float wantedYaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        return Mth.wrapDegrees(wantedYaw - boat.getYRot());
    }

    private Vec3 getBoatForwardDirection(ModBoatEntity boat) {
        double radians = Math.toRadians(boat.getYRot() + 90.0F);
        return new Vec3(Math.cos(radians), 0.0D, Math.sin(radians)).normalize();
    }

    private Vec3 getCircleDirection(Vec3 boatPos, Vec3 targetPos) {
        Vec3 towardTarget = PirateRaidAiUtil.horizontalDirection(boatPos, targetPos);

        if (towardTarget.lengthSqr() < 0.0001D) {
            return Vec3.ZERO;
        }

        return PirateRaidAiUtil.leftOf(towardTarget).normalize();
    }

    private AbstractPirateEntity findDismountedMateForBoat(ModBoatEntity boat) {
        /*
         * If this boat has no open seat, it should not wait for anyone.
         * This prevents full boats from stopping because another boat's boarder is nearby.
         */
        if (PirateBoatPassengerHelper.isFull(boat)) {
            return null;
        }

        AABB searchBox = boat.getBoundingBox().inflate(CREW_WAIT_SEARCH_RANGE);

        List<AbstractPirateEntity> pirates = boat.level().getEntitiesOfClass(
                AbstractPirateEntity.class,
                searchBox,
                pirateEntity -> pirateEntity.isAlive()
                        && !pirateEntity.isRemoved()
                        && !pirateEntity.isPassenger()
                        && this.isRaidBoarder(pirateEntity)
                        && this.belongsToBoat(pirateEntity, boat)
        );

        AbstractPirateEntity farthestMate = null;
        double farthestDistanceSqr = 0.0D;

        for (AbstractPirateEntity pirateEntity : pirates) {
            double distanceSqr = pirateEntity.distanceToSqr(boat);

            if (distanceSqr > farthestDistanceSqr) {
                farthestDistanceSqr = distanceSqr;
                farthestMate = pirateEntity;
            }
        }

        return farthestMate;
    }

    private boolean belongsToBoat(AbstractPirateEntity pirateEntity, ModBoatEntity boat) {
        /*
         * New permanent assignment tag.
         * This is the preferred check.
         */
        if (pirateEntity.getPersistentData().hasUUID(PirateBoatPassengerHelper.HOME_BOAT_UUID_TAG)) {
            UUID homeBoatUuid = pirateEntity.getPersistentData().getUUID(PirateBoatPassengerHelper.HOME_BOAT_UUID_TAG);
            return homeBoatUuid.equals(boat.getUUID());
        }

        /*
         * Older temporary return tag.
         * Kept so existing spawned pirates can still return correctly.
         */
        if (pirateEntity.getPersistentData().hasUUID(RETURN_BOAT_UUID_TAG)) {
            UUID returnBoatUuid = pirateEntity.getPersistentData().getUUID(RETURN_BOAT_UUID_TAG);
            return returnBoatUuid.equals(boat.getUUID());
        }

        /*
         * Existing raid boat assignment tag.
         */
        if (pirateEntity.getPersistentData().hasUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG)) {
            UUID raidBoatUuid = pirateEntity.getPersistentData().getUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG);
            return raidBoatUuid.equals(boat.getUUID());
        }

        /*
         * Important:
         * Do NOT fall back to distance.
         * Distance fallback lets one boat think another boat's boarder belongs to it.
         */
        return false;
    }

    private boolean isRaidBoarder(Entity entity) {
        return entity.getTags().contains(RAID_PIRATE_TAG)
                || entity.getTags().contains(BOARDER_TAG)
                || PirateRaidAiUtil.isBoarder(entity);
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

            int priority = this.getDriverPriority(piratePassenger);

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
}