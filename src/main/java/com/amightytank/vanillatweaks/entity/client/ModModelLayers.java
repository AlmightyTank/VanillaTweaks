package com.amightytank.vanillatweaks.entity.client;

import com.amightytank.vanillatweaks.VanillaTweaks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation SAILBOAT_LAYER = create("boat/sailboat");
    public static final ModelLayerLocation MEDIUM_SAILBOAT_LAYER = create("boat/medium_sailboat");
    public static final ModelLayerLocation LARGE_SAILBOAT_LAYER = create("boat/large_sailboat");

    public static final ModelLayerLocation BAMBOO_SAILBOAT_LAYER = create("boat/bamboo_sailboat");
    public static final ModelLayerLocation BAMBOO_MEDIUM_SAILBOAT_LAYER = create("boat/bamboo_medium_sailboat");
    public static final ModelLayerLocation BAMBOO_LARGE_SAILBOAT_LAYER = create("boat/bamboo_large_sailboat");

    public static final ModelLayerLocation KRAKEN_TENTACLE_LAYER = create("entity/pirate/kraken_tentacle");
    public static final ModelLayerLocation PIRATE_CAPTAIN_LAYER = create("entity/pirate/pirate_captain");

    public static final ModelLayerLocation SHOULDER_PIRATE_PARROT_LAYER = create("entity/pirate/shoulder_pirate_parrot");

    private static ModelLayerLocation create(String path) {
        return new ModelLayerLocation(new ResourceLocation(VanillaTweaks.MOD_ID, path), "main");
    }
}
