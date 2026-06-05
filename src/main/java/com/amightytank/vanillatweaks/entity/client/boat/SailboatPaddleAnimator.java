package com.amightytank.vanillatweaks.entity.client.boat;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;

public final class SailboatPaddleAnimator {
    private static final float ROW_DIRECTION = -1.0F;

    private static final float X_AMOUNT = 0.55F;
    private static final float Y_AMOUNT = 0.75F;

    private SailboatPaddleAnimator() {
    }

    public static void animatePaddle(
            Boat boat,
            int side,
            int oarSetIndex,
            ModelPart paddle,
            float partialTick,
            PaddleBase base
    ) {
        resetPaddle(paddle, base);

        /*
         * One living passenger activates one oar set.
         * Medium/large models call this for more sets, so no list helper is needed.
         */
        if (oarSetIndex >= getLivingPassengerCount(boat)) {
            return;
        }

        /*
         * side 0 = left-side oars
         * side 1 = right-side oars
         */
        if (!boat.getPaddleState(side)) {
            return;
        }

        float f = boat.getRowingTime(side, partialTick) * ROW_DIRECTION;

        float xDelta = Mth.sin(f) * X_AMOUNT;
        float yDelta = (Mth.sin(f + 1.0F) - Mth.sin(1.0F)) * Y_AMOUNT;

        if (side == 0) {
            yDelta = -yDelta;
        }

        paddle.xRot = base.xRot + xDelta;
        paddle.yRot = base.yRot + yDelta;
        paddle.zRot = base.zRot;
    }

    private static int getLivingPassengerCount(Boat boat) {
        return (int) boat.getPassengers()
                .stream()
                .filter(entity -> entity instanceof LivingEntity livingEntity && livingEntity.isAlive())
                .count();
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
}