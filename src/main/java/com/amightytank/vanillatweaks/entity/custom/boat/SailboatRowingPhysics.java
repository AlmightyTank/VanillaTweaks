package com.amightytank.vanillatweaks.entity.custom.boat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public final class SailboatRowingPhysics {
    /*
     * Base speed is intentionally a little lower on larger boats.
     * Extra player rowers add both push power and a higher speed cap.
     */
    private static final double SMALL_BASE_TOP_SPEED = 0.40D;
    private static final double MEDIUM_BASE_TOP_SPEED = 0.38D;
    private static final double LARGE_BASE_TOP_SPEED = 0.34D;

    private static final double EXTRA_TOP_SPEED_PER_EXTRA_PLAYER = 0.055D;
    private static final double EXTRA_ACCELERATION_PER_EXTRA_PLAYER = 0.0035D;

    private SailboatRowingPhysics() {
    }

    public static void apply(
            Boat boat,
            boolean frontPlayerPressingForward,
            boolean mediumSailboat,
            boolean largeSailboat
    ) {
        int rowers = getPlayerRowerCount(boat);
        if (rowers <= 0) {
            return;
        }

        int extraRowers = Math.max(0, rowers - 1);

        if (frontPlayerPressingForward && extraRowers > 0) {
            double acceleration = EXTRA_ACCELERATION_PER_EXTRA_PLAYER * extraRowers;

            double yawRadians = Math.toRadians(boat.getYRot());
            double xPower = -Math.sin(yawRadians) * acceleration;
            double zPower = Math.cos(yawRadians) * acceleration;

            Vec3 motion = boat.getDeltaMovement();
            boat.setDeltaMovement(motion.add(xPower, 0.0D, zPower));
        }

        clampHorizontalSpeed(boat, getTopSpeed(rowers, mediumSailboat, largeSailboat));
    }

    private static int getPlayerRowerCount(Boat boat) {
        int count = 0;

        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Player player && !player.isSpectator()) {
                count++;
            }
        }

        return count;
    }

    private static double getTopSpeed(int rowers, boolean mediumSailboat, boolean largeSailboat) {
        double baseSpeed = getBaseTopSpeed(mediumSailboat, largeSailboat);
        int extraRowers = Math.max(0, rowers - 1);

        return baseSpeed + extraRowers * EXTRA_TOP_SPEED_PER_EXTRA_PLAYER;
    }

    private static double getBaseTopSpeed(boolean mediumSailboat, boolean largeSailboat) {
        if (largeSailboat) {
            return LARGE_BASE_TOP_SPEED;
        }

        if (mediumSailboat) {
            return MEDIUM_BASE_TOP_SPEED;
        }

        return SMALL_BASE_TOP_SPEED;
    }

    private static void clampHorizontalSpeed(Boat boat, double maxSpeed) {
        Vec3 motion = boat.getDeltaMovement();

        double horizontalSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        if (horizontalSpeed <= maxSpeed || horizontalSpeed <= 0.0001D) {
            return;
        }

        double scale = maxSpeed / horizontalSpeed;
        boat.setDeltaMovement(
                motion.x * scale,
                motion.y,
                motion.z * scale
        );
    }
}
