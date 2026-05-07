package com.amightytank.vanillatweaks.entity.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class MediumSailboatChestBoatModel extends MediumSailboatModel {
    public MediumSailboatChestBoatModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return MediumSailboatModel.createBodyLayer();
    }
}
