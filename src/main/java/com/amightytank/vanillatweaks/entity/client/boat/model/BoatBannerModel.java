package com.amightytank.vanillatweaks.entity.client.boat.model;

import com.mojang.blaze3d.vertex.PoseStack;

public interface BoatBannerModel {
    void translateToBannerPanel(PoseStack poseStack);

    default void translateToRearBannerPanel(PoseStack poseStack) {
    }

    default boolean hasRearBannerPanel() {
        return false;
    }
}