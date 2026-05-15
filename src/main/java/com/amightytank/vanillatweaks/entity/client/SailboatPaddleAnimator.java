package com.amightytank.vanillatweaks.entity.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public final class SailboatPaddleAnimator {
    private SailboatPaddleAnimator() {
    }

    public static void animatePaddleFromBase(
            Boat boat,
            int side,
            ModelPart paddle,
            float limbSwing,
            PaddleBase base,
            float xAmount,
            float yAmount
    ) {
        float f = boat.getRowingTime(side, limbSwing);

        float xDelta = Mth.sin(-f) * xAmount;
        float yDelta = (Mth.sin(-f + 1.0F) - Mth.sin(1.0F)) * yAmount;

        paddle.xRot = base.x + xDelta;
        paddle.yRot = base.y + yDelta;
        paddle.zRot = base.z;
    }

    public static class PaddleBase {
        public final float x;
        public final float y;
        public final float z;

        public PaddleBase(ModelPart paddle) {
            this.x = paddle.xRot;
            this.y = paddle.yRot;
            this.z = paddle.zRot;
        }
    }
}