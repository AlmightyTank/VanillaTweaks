package com.amightytank.vanillatweaks.entity.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public final class SailboatPaddleAnimator {
    private SailboatPaddleAnimator() {
    }

    public record PaddlePose(
            float baseXRot,
            float baseYRot,
            float baseZRot
    ) {
        public static PaddlePose from(ModelPart part) {
            return new PaddlePose(part.xRot, part.yRot, part.zRot);
        }
    }

    public static void animatePaddle(Boat boat, int side, ModelPart paddle, float limbSwing, PaddlePose pose) {
        animatePaddle(boat, side, paddle, limbSwing, pose, 0.0F);
    }

    public static void animatePaddle(Boat boat, int side, ModelPart paddle, float limbSwing, PaddlePose pose, float phaseOffset) {
        int rowingSide = side == 1 ? 1 : 0;

        float f = boat.getRowingTime(rowingSide, limbSwing) + phaseOffset;

        float xAmount = 0.55F;

        // Animate only X so the paddle does not drift away from its PartPose offset.
        paddle.xRot = Mth.clampedLerp(
                pose.baseXRot() - xAmount,
                pose.baseXRot() + xAmount,
                (Mth.sin(-f) + 1.0F) / 2.0F
        );

        // Keep these locked to the Blockbench pose.
        paddle.yRot = pose.baseYRot();
        paddle.zRot = pose.baseZRot();
    }
}