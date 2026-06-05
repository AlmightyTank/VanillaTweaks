package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateBoatPilotGoal extends Goal {
    private static final double TARGET_SEARCH_RANGE = 96.0D;

    private static final double CREW_PICKUP_RANGE = 72.0D;
    private static final double CREW_PICKUP_STOP_RANGE = 5.0D;

    private static final double BOAT_ACCELERATION = 0.045D;
    private static final double PICKUP_ACCELERATION = 0.035D;

    private static final double MAX_BOAT_SPEED = 0.34D;
    private static final double MAX_PICKUP_SPEED = 0.25D;

    private static final double TARGET_STOP_RANGE = 10.0D;

    private static final float TURN_DEAD_ZONE = 6.0F;
    private static final float FORWARD_ARC = 135.0F;
    private static final float FALLBACK_TURN_SPEED = 4.0F;

    /*
     * Prevent pirate boats from holding forward into land/walls.
     */
    private static final double OBSTACLE_CHECK_DISTANCE = 1.45D;
    private static final double OBSTACLE_CHECK_INFLATE = 0.12D;
    private static final int OBSTACLE_AVOID_TICKS = 18;
    private static final double OBSTACLE_REVERSE_PUSH = 0.035D;
    private static final double OBSTACLE_BRAKE_DRAG = 0.45D;

    private final AbstractPirateEntity pirate;

    private LivingEntity target;
    private Mob pickupPirate;

    private int obstacleAvoidTicks;
    private boolean obstacleTurnLeft;

    public PirateBoatPilotGoal(AbstractPirateEntity pirate) {
        this.pirate = pirate;

        /*
         * MOVE only.
         * Other combat goals can own LOOK without fighting this goal.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(this.pirate.getVehicle() instanceof Boat boat)) {
            return false;
        }

        if (boat.getControllingPassenger() != this.pirate) {
            return false;
        }

        this.pickupPirate = findPickupPirate(boat);

        if (this.pickupPirate != null) {
            return true;
        }

        this.target = findTarget();
        return this.target != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(this.pirate.getVehicle() instanceof Boat boat)) {
            return false;
        }

        if (boat.getControllingPassenger() != this.pirate) {
            return false;
        }

        this.pickupPirate = findPickupPirate(boat);

        if (this.pickupPirate != null) {
            return true;
        }

        this.target = findTarget();
        return this.target != null;
    }

    @Override
    public void stop() {
        if (this.pirate.getVehicle() instanceof Boat boat) {
            slowBoat(boat);
        }

        this.target = null;
        this.pickupPirate = null;
        this.obstacleAvoidTicks = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!(this.pirate.getVehicle() instanceof Boat boat)) {
            return;
        }

        if (boat.getControllingPassenger() != this.pirate) {
            return;
        }

        this.pickupPirate = findPickupPirate(boat);

        if (this.pickupPirate != null) {
            double distanceSqr = boat.distanceToSqr(this.pickupPirate);

            if (distanceSqr <= CREW_PICKUP_STOP_RANGE * CREW_PICKUP_STOP_RANGE) {
                slowBoat(boat);
                PirateBoatPassengerHelper.attemptBoard(this.pickupPirate, boat, false);
                return;
            }

            driveBoatToward(
                    boat,
                    this.pickupPirate.position(),
                    PICKUP_ACCELERATION,
                    MAX_PICKUP_SPEED
            );
            return;
        }

        this.target = findTarget();

        if (this.target == null) {
            slowBoat(boat);
            return;
        }

        double distanceSqr = boat.distanceToSqr(this.target);

        if (distanceSqr <= TARGET_STOP_RANGE * TARGET_STOP_RANGE) {
            slowBoat(boat);
            return;
        }

        driveBoatToward(
                boat,
                this.target.position(),
                BOAT_ACCELERATION,
                MAX_BOAT_SPEED
        );
    }

    private Mob findPickupPirate(Boat boat) {
        if (!PirateBoatPassengerHelper.hasAvailableReturnSeat(boat)) {
            return null;
        }

        return PirateBoatPassengerHelper.findBestPickupPirate(boat, CREW_PICKUP_RANGE);
    }

    private LivingEntity findTarget() {
        LivingEntity currentTarget = this.pirate.getTarget();

        if (AbstractPirateEntity.canPirateAttack(currentTarget)) {
            return currentTarget;
        }

        Player nearestPlayer = this.pirate.level().getNearestPlayer(this.pirate, TARGET_SEARCH_RANGE);

        if (nearestPlayer != null && AbstractPirateEntity.canPirateAttack(nearestPlayer)) {
            return nearestPlayer;
        }

        return null;
    }

    private void driveBoatToward(Boat boat, Vec3 targetPosition, double acceleration, double maxSpeed) {
        double dx = targetPosition.x - boat.getX();
        double dz = targetPosition.z - boat.getZ();

        if (dx * dx + dz * dz < 0.001D) {
            slowBoat(boat);
            return;
        }

        float wantedYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float yawDifference = Mth.wrapDegrees(wantedYaw - boat.getYRot());

        boolean turningLeft = yawDifference < -TURN_DEAD_ZONE;
        boolean turningRight = yawDifference > TURN_DEAD_ZONE;
        boolean forward = Math.abs(yawDifference) <= FORWARD_ARC;

        /*
         * If the front of the boat is blocked, do not keep holding W.
         * Back up and turn until the hull has room again.
         */
        if ((forward && isForwardPathBlocked(boat)) || this.obstacleAvoidTicks > 0) {
            if (this.obstacleAvoidTicks <= 0) {
                this.obstacleAvoidTicks = OBSTACLE_AVOID_TICKS;

                if (Math.abs(yawDifference) > TURN_DEAD_ZONE) {
                    this.obstacleTurnLeft = yawDifference < 0.0F;
                } else {
                    this.obstacleTurnLeft = (boat.getId() & 1) == 0;
                }
            }

            this.obstacleAvoidTicks--;

            boolean left = this.obstacleTurnLeft;
            boolean right = !this.obstacleTurnLeft;

            setBoatInput(boat, left, right, false, true);
            pushBoatBackwardFromObstacle(boat);
            return;
        }

        setBoatInput(boat, turningLeft, turningRight, forward, false);

        if (boat instanceof ModBoatEntity) {
            return;
        }

        float yawStep = Mth.clamp(yawDifference, -FALLBACK_TURN_SPEED, FALLBACK_TURN_SPEED);
        boat.setYRot(boat.getYRot() + yawStep);

        if (!forward) {
            Vec3 motion = boat.getDeltaMovement();
            boat.setDeltaMovement(motion.x * 0.92D, motion.y, motion.z * 0.92D);
            boat.hasImpulse = true;
            return;
        }

        Vec3 forwardVec = Vec3.directionFromRotation(0.0F, boat.getYRot());
        Vec3 motion = boat.getDeltaMovement();

        double newX = motion.x * 0.96D + forwardVec.x * acceleration;
        double newZ = motion.z * 0.96D + forwardVec.z * acceleration;

        double horizontalSpeed = Math.sqrt(newX * newX + newZ * newZ);

        if (horizontalSpeed > maxSpeed) {
            double scale = maxSpeed / horizontalSpeed;
            newX *= scale;
            newZ *= scale;
        }

        boat.setDeltaMovement(newX, motion.y, newZ);
        boat.hasImpulse = true;
    }

    private boolean isForwardPathBlocked(Boat boat) {
        Vec3 forwardVec = Vec3.directionFromRotation(0.0F, boat.getYRot());

        AABB checkBox = boat.getBoundingBox()
                .inflate(OBSTACLE_CHECK_INFLATE, 0.05D, OBSTACLE_CHECK_INFLATE)
                .move(
                        forwardVec.x * OBSTACLE_CHECK_DISTANCE,
                        0.0D,
                        forwardVec.z * OBSTACLE_CHECK_DISTANCE
                );

        /*
         * Block collisions only.
         * This avoids detecting passengers or sailboat collision-part entities.
         */
        return boat.level().getBlockCollisions(boat, checkBox).iterator().hasNext();
    }

    private void pushBoatBackwardFromObstacle(Boat boat) {
        Vec3 forwardVec = Vec3.directionFromRotation(0.0F, boat.getYRot());
        Vec3 motion = boat.getDeltaMovement();

        double newX = motion.x * OBSTACLE_BRAKE_DRAG - forwardVec.x * OBSTACLE_REVERSE_PUSH;
        double newZ = motion.z * OBSTACLE_BRAKE_DRAG - forwardVec.z * OBSTACLE_REVERSE_PUSH;

        boat.setDeltaMovement(newX, motion.y, newZ);
        boat.hasImpulse = true;
    }

    private void slowBoat(Boat boat) {
        clearBoatInput(boat);

        Vec3 motion = boat.getDeltaMovement();
        boat.setDeltaMovement(motion.x * 0.82D, motion.y, motion.z * 0.82D);
        boat.hasImpulse = true;
    }

    private void setBoatInput(Boat boat, boolean left, boolean right, boolean forward, boolean back) {
        if (boat instanceof ModBoatEntity modBoat) {
            modBoat.setPirateRaidInput(left, right, forward, back);
            return;
        }

        boat.setInput(left, right, forward, back);

        boolean leftPaddle = forward || back || right;
        boolean rightPaddle = forward || back || left;

        boat.setPaddleState(leftPaddle, rightPaddle);
    }

    private void clearBoatInput(Boat boat) {
        if (boat instanceof ModBoatEntity modBoat) {
            modBoat.clearPirateRaidInput();
            return;
        }

        boat.setInput(false, false, false, false);
        boat.setPaddleState(false, false);
    }
}