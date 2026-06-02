package com.amightytank.vanillatweaks.entity.ai.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public final class PirateWaterBoarderMoveHelper {
    /*
     * Horizontal water speed only.
     * This should feel around half land chase speed.
     */
    private static final double WATER_TOP_SPEED = 0.20D;

    /*
     * Smooth acceleration toward the desired water speed.
     */
    private static final double WATER_ACCEL = 0.35D;

    private PirateWaterBoarderMoveHelper() {
    }

    public static void moveToward(Mob pirate, Vec3 targetPos) {
        if (pirate == null) {
            return;
        }

        if (pirate.isPassenger()) {
            return;
        }

        if (!pirate.isInWaterOrBubble()) {
            return;
        }

        Vec3 toTarget = new Vec3(
                targetPos.x - pirate.getX(),
                0.0D,
                targetPos.z - pirate.getZ()
        );

        if (toTarget.lengthSqr() < 0.0001D) {
            return;
        }

        Vec3 direction = toTarget.normalize();
        Vec3 motion = pirate.getDeltaMovement();

        double wantedX = direction.x * WATER_TOP_SPEED;
        double wantedZ = direction.z * WATER_TOP_SPEED;

        double newX = Mth.lerp(WATER_ACCEL, motion.x, wantedX);
        double newZ = Mth.lerp(WATER_ACCEL, motion.z, wantedZ);

        /*
         * Do not touch Y.
         * FloatGoal handles bobbing / staying above water.
         */
        pirate.setDeltaMovement(newX, motion.y, newZ);
        pirate.hasImpulse = true;
    }
}