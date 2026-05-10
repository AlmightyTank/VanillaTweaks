package com.amightytank.vanillatweaks.entity.client;

import com.amightytank.vanillatweaks.entity.custom.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.ModChestBoatEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

public final class SailboatPaddleAnimator {
    private SailboatPaddleAnimator() {
    }

    public static void animatePaddle(Boat boat, int side, ModelPart paddle, float limbSwing) {
        animatePaddle(boat, side, paddle, limbSwing, 0.0F);
    }

    public static void animatePaddle(Boat boat, int side, ModelPart paddle, float limbSwing, float phaseOffset) {
        int rowingSide = side == 1 ? 1 : 0;

        ModBoatEntity.BoatSize boatSize = getBoatSize(boat);
        int rowers = getRowerCount(boat);

        double dx = boat.getDeltaMovement().x;
        double dz = boat.getDeltaMovement().z;
        float speed = (float) Math.sqrt(dx * dx + dz * dz);

        float minCycleSpeed;
        float maxCycleSpeed;
        float minStrength;
        float maxStrength;
        float speedForMaxAnim;

        switch (boatSize) {
            case SAILBOAT -> {
                minCycleSpeed = 1.15F;
                maxCycleSpeed = 1.85F;
                minStrength = 1.15F;
                maxStrength = 1.55F;
                speedForMaxAnim = 0.18F;
            }
            case MEDIUM_SAILBOAT -> {
                minCycleSpeed = 0.55F;
                maxCycleSpeed = 1.45F;
                minStrength = 0.85F;
                maxStrength = 1.30F;
                speedForMaxAnim = 0.26F;
            }
            case LARGE_SAILBOAT -> {
                minCycleSpeed = 0.30F;
                maxCycleSpeed = 1.05F;
                minStrength = 0.65F;
                maxStrength = 1.05F;
                speedForMaxAnim = 0.36F;
            }
            default -> {
                minCycleSpeed = 1.0F;
                maxCycleSpeed = 1.4F;
                minStrength = 1.0F;
                maxStrength = 1.4F;
                speedForMaxAnim = 0.22F;
            }
        }

        float speedPercent = Mth.clamp(speed / speedForMaxAnim, 0.0F, 1.0F);
        speedPercent = speedPercent * speedPercent * (3.0F - 2.0F * speedPercent);

        // Extra rowers increase animation speed and strength.
        float rowerSpeedBonus = 1.0F + Math.max(0, rowers - 1) * 0.25F;
        float rowerStrengthBonus = 1.0F + Math.max(0, rowers - 1) * 0.15F;

        float cycleSpeed = Mth.lerp(speedPercent, minCycleSpeed, maxCycleSpeed) * rowerSpeedBonus;
        float strength = Mth.lerp(speedPercent, minStrength, maxStrength) * rowerStrengthBonus;

        float f = boat.getRowingTime(rowingSide, limbSwing);
        f = f * cycleSpeed + phaseOffset;

        float vanillaX = Mth.clampedLerp(
                -(float) Math.PI / 3F,
                -0.2617994F,
                (Mth.sin(-f) + 1.0F) / 2.0F
        );

        float vanillaY = Mth.clampedLerp(
                -(float) Math.PI / 4F,
                (float) Math.PI / 4F,
                (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F
        );

        if (rowingSide == 1) {
            vanillaY = (float) Math.PI - vanillaY;
        }

        float baseX = 2.1642F;
        float baseY = rowingSide == 1 ? -0.8727F : 0.8727F;
        float baseZ = rowingSide == 1 ? -2.8798F : 2.8798F;

        float centerX = (-(float) Math.PI / 3F + -0.2617994F) / 2.0F;
        float centerY = rowingSide == 1 ? (float) Math.PI : 0.0F;

        paddle.xRot = baseX + (vanillaX - centerX) * strength;
        paddle.yRot = baseY + (vanillaY - centerY) * strength;
        paddle.zRot = baseZ;
    }

    private static int getRowerCount(Boat boat) {
        int count = 0;

        for (var passenger : boat.getPassengers()) {
            if (passenger instanceof Player) {
                count++;
            }
        }

        return Math.max(1, count);
    }

    private static ModBoatEntity.BoatSize getBoatSize(Boat boat) {
        if (boat instanceof ModBoatEntity modBoat) {
            return modBoat.getModVariant().getBoatSize();
        }

        if (boat instanceof ModChestBoatEntity chestBoat) {
            return chestBoat.getModVariant().getBoatSize();
        }

        return ModBoatEntity.BoatSize.SAILBOAT;
    }
}