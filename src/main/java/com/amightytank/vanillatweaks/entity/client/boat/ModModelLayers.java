package com.amightytank.vanillatweaks.entity.client.boat;

import com.amightytank.vanillatweaks.VanillaTweaks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation SAILBOAT_LAYER = create("boat/sailboat");
    public static final ModelLayerLocation SAILBOAT_CHEST_LAYER = create("chest_boat/sailboat");

    public static final ModelLayerLocation MEDIUM_SAILBOAT_LAYER = create("boat/medium_sailboat");
    public static final ModelLayerLocation MEDIUM_SAILBOAT_CHEST_LAYER = create("chest_boat/medium_sailboat");

    public static final ModelLayerLocation LARGE_SAILBOAT_LAYER = create("boat/large_sailboat");
    public static final ModelLayerLocation LARGE_SAILBOAT_CHEST_LAYER = create("chest_boat/large_sailboat");

    private static ModelLayerLocation create(String path) {
        return new ModelLayerLocation(new ResourceLocation(VanillaTweaks.MOD_ID, path), "main");
    }
}
