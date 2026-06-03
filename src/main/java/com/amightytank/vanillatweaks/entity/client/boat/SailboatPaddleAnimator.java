package com.amightytank.vanillatweaks.entity.client.boat;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.List;

public final class SailboatPaddleAnimator {
    private static final float ROW_DIRECTION = -1.0F;

    private static final float X_AMOUNT = 0.55F;
    private static final float Y_AMOUNT = 0.75F;

    private static final float CUSTOM_ROW_SPEED = 0.65F;

    /*
     * Small offset so multiple pirates do not row perfectly identically.
     * It makes the boat look more alive.
     */
    private static final float OAR_SET_PHASE_OFFSET = 0.55F;

    private SailboatPaddleAnimator() {
    }

    public static void animateOarSetsByPassengerCount(
            Boat boat,
            List<PaddleSet> paddleSets,
            float partialTick
    ) {
        int activeSets = getActiveOarSetCount(boat, paddleSets.size());

        for (int i = 0; i < paddleSets.size(); i++) {
            PaddleSet set = paddleSets.get(i);

            if (i < activeSets) {
                animatePaddle(boat, 0, i, set.leftPaddle(), partialTick, set.leftBase());
                animatePaddle(boat, 1, i, set.rightPaddle(), partialTick, set.rightBase());
            } else {
                resetPaddle(set.leftPaddle(), set.leftBase());
                resetPaddle(set.rightPaddle(), set.rightBase());
            }
        }
    }

    private static int getActiveOarSetCount(Boat boat, int maxOarSets) {
        if (boat instanceof ModBoatEntity modBoat) {
            return modBoat.getSailboatVisualActiveOarSets(maxOarSets);
        }

        return Math.min(getLivingPassengerCount(boat), maxOarSets);
    }

    private static int getLivingPassengerCount(Boat boat) {
        return (int) boat.getPassengers()
                .stream()
                .filter(entity -> entity instanceof LivingEntity livingEntity && livingEntity.isAlive())
                .count();
    }

    public static void animatePaddle(
            Boat boat,
            int side,
            int oarSetIndex,
            ModelPart paddle,
            float partialTick,
            PaddleBase base
    ) {
        paddle.x = base.x;
        paddle.y = base.y;
        paddle.z = base.z;

        float f = getRowingTimeForVisuals(boat, side, oarSetIndex, partialTick) * ROW_DIRECTION;

        float xDelta = Mth.sin(f) * X_AMOUNT;
        float yDelta = (Mth.sin(f + 1.0F) - Mth.sin(1.0F)) * Y_AMOUNT;

        if (side == 0) {
            yDelta = -yDelta;
        }

        paddle.xRot = base.xRot + xDelta;
        paddle.yRot = base.yRot + yDelta;
        paddle.zRot = base.zRot;
    }

    private static float getRowingTimeForVisuals(Boat boat, int side, int oarSetIndex, float partialTick) {
        if (boat instanceof ModBoatEntity modBoat) {
            if (modBoat.isSailboatVisualPaddleMoving(side)) {
                return (((float) modBoat.tickCount + partialTick) * CUSTOM_ROW_SPEED)
                        + (oarSetIndex * OAR_SET_PHASE_OFFSET);
            }

            return 0.0F;
        }

        return boat.getRowingTime(side, partialTick);
    }

    private static void resetPaddle(ModelPart paddle, PaddleBase base) {
        paddle.x = base.x;
        paddle.y = base.y;
        paddle.z = base.z;

        paddle.xRot = base.xRot;
        paddle.yRot = base.yRot;
        paddle.zRot = base.zRot;
    }

    public static class PaddleBase {
        public final float x;
        public final float y;
        public final float z;

        public final float xRot;
        public final float yRot;
        public final float zRot;

        public PaddleBase(ModelPart paddle) {
            this.x = paddle.x;
            this.y = paddle.y;
            this.z = paddle.z;

            this.xRot = paddle.xRot;
            this.yRot = paddle.yRot;
            this.zRot = paddle.zRot;
        }
    }

    public record PaddleSet(
            ModelPart leftPaddle,
            ModelPart rightPaddle,
            PaddleBase leftBase,
            PaddleBase rightBase
    ) {
    }
}