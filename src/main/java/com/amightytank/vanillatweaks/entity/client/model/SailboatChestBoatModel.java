package com.amightytank.vanillatweaks.entity.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class SailboatChestBoatModel extends SailboatModel {
    public SailboatChestBoatModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return SailboatModel.createBodyLayer();
    }
}
