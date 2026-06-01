package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateBoatPilotGoal extends Goal {
    private static final double TARGET_SEARCH_RANGE = 96.0D;

    private static final double BOAT_ACCELERATION = 0.055D;
    private static final double RESCUE_BOAT_ACCELERATION = 0.045D;

    private static final double MAX_BOAT_SPEED = 0.34D;
    private static final double MAX_RESCUE_SPEED = 0.25D;

    private static final double TARGET_STOP_RANGE = 10.0D;
    private static final double RESCUE_STOP_RANGE = 5.0D;

    private static final float TURN_SPEED = 6.0F;

    private final AbstractPirateEntity pirate;

    private LivingEntity target;

    public PirateBoatPilotGoal(AbstractPirateEntity pirate) {
        this.pirate = pirate;

        /*
         * Do not claim LOOK.
         * Gunner/captain casting goals can still aim while the boat moves.
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

        /*
         * A rescue boat should move even if there is no player target.
         */
        if (PirateBoatPassengerHelper.hasCaptainRescueTarget(boat)) {
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

        if (PirateBoatPassengerHelper.hasCaptainRescueTarget(boat)) {
            return true;
        }

        this.target = findTarget();
        return this.target != null;
    }

    @Override
    public void stop() {
        if (this.pirate.getVehicle() instanceof Boat boat) {
            setBoatInput(boat, false, false, false, false);
        }

        this.target = null;
    }

    @Override
    public void tick() {
        if (!(this.pirate.getVehicle() instanceof Boat boat)) {
            return;
        }

        if (boat.getControllingPassenger() != this.pirate) {
            return;
        }

        /*
         * Captain rescue overrides chasing.
         * The boat goes back to the captain and waits.
         */
        Entity rescueCaptain = PirateBoatPassengerHelper.getCaptainRescueTarget(boat);

        if (rescueCaptain instanceof LivingEntity livingCaptain && livingCaptain.isAlive() && !livingCaptain.isPassenger()) {
            this.pirate.getLookControl().setLookAt(livingCaptain, 30.0F, 30.0F);

            double distanceSqr = boat.distanceToSqr(livingCaptain);

            if (distanceSqr <= RESCUE_STOP_RANGE * RESCUE_STOP_RANGE) {
                slowBoat(boat);

                PirateBoatPassengerHelper.queueBoard((net.minecraft.world.entity.Mob) livingCaptain, boat, true);
                PirateBoatPassengerHelper.tryBoardQueuedNow((net.minecraft.world.entity.Mob) livingCaptain);

                return;
            }

            driveBoatToward(boat, livingCaptain.position(), RESCUE_BOAT_ACCELERATION, MAX_RESCUE_SPEED);
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

        driveBoatToward(boat, this.target.position(), BOAT_ACCELERATION, MAX_BOAT_SPEED);
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
        Vec3 boatPosition = boat.position();

        double dx = targetPosition.x - boatPosition.x;
        double dz = targetPosition.z - boatPosition.z;

        if (dx * dx + dz * dz < 0.001D) {
            slowBoat(boat);
            return;
        }

        float wantedYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float yawDifference = Mth.wrapDegrees(wantedYaw - boat.getYRot());
        float yawStep = Mth.clamp(yawDifference, -TURN_SPEED, TURN_SPEED);

        boat.setYRot(boat.getYRot() + yawStep);

        boolean turningLeft = yawDifference < -4.0F;
        boolean turningRight = yawDifference > 4.0F;
        boolean mostlyFacingTarget = Math.abs(yawDifference) < 80.0F;

        setBoatInput(boat, turningLeft, turningRight, mostlyFacingTarget, false);

        if (!mostlyFacingTarget) {
            Vec3 currentSlowMotion = boat.getDeltaMovement();
            boat.setDeltaMovement(currentSlowMotion.x * 0.92D, currentSlowMotion.y, currentSlowMotion.z * 0.92D);
            boat.hasImpulse = true;
            return;
        }

        Vec3 forward = Vec3.directionFromRotation(0.0F, boat.getYRot());
        Vec3 currentMotion = boat.getDeltaMovement();

        double newX = currentMotion.x + forward.x * acceleration;
        double newZ = currentMotion.z + forward.z * acceleration;

        double horizontalSpeed = Math.sqrt(newX * newX + newZ * newZ);

        if (horizontalSpeed > maxSpeed) {
            double scale = maxSpeed / horizontalSpeed;
            newX *= scale;
            newZ *= scale;
        }

        boat.setDeltaMovement(newX, currentMotion.y, newZ);
        boat.hasImpulse = true;
    }

    private void slowBoat(Boat boat) {
        setBoatInput(boat, false, false, false, false);

        Vec3 motion = boat.getDeltaMovement();
        boat.setDeltaMovement(motion.x * 0.82D, motion.y, motion.z * 0.82D);
        boat.hasImpulse = true;
    }

    private void setBoatInput(Boat boat, boolean left, boolean right, boolean forward, boolean back) {
        boat.setInput(left, right, forward, back);
    }
}