package com.amightytank.vanillatweaks.entity.client;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.model.*;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.amightytank.vanillatweaks.entity.custom.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.ModChestBoatEntity;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Map;
import java.util.stream.Stream;

public class ModBoatRenderer extends BoatRenderer {
    private final Map<ModBoatEntity.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public ModBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat);

        this.boatResources = Stream.of(ModBoatEntity.Type.values())
                .collect(ImmutableMap.toImmutableMap(
                        type -> type,
                        type -> Pair.of(
                                new ResourceLocation(VanillaTweaks.MOD_ID, getTextureLocation(type, chestBoat)),
                                this.createBoatModel(context, type, chestBoat)
                        )
                ));
    }

    private static String getTextureLocation(ModBoatEntity.Type type, boolean chestBoat) {
        return chestBoat
                ? "textures/entity/chest_boat/" + type.getTextureName() + ".png"
                : "textures/entity/boat/" + type.getTextureName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, ModBoatEntity.Type type, boolean chestBoat) {
        ModelLayerLocation layerLocation = chestBoat
                ? createChestBoatModelName(type)
                : createBoatModelName(type);

        ModelPart root = context.bakeLayer(layerLocation);

        return switch (type.getBoatSize()) {
            case SAILBOAT -> chestBoat
                    ? new SailboatChestBoatModel(root)
                    : new SailboatModel(root);

            case MEDIUM_SAILBOAT -> chestBoat
                    ? new MediumSailboatChestBoatModel(root)
                    : new MediumSailboatModel(root);

            case LARGE_SAILBOAT -> chestBoat
                    ? new LargeChestSailboatModel(root)
                    : new LargeSailboatModel(root);
        };
    }

    public static ModelLayerLocation createBoatModelName(ModBoatEntity.Type type) {
        return createLocation("boat/" + type.getBoatSize().getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(ModBoatEntity.Type type) {
        return createLocation("chest_boat/" + type.getBoatSize().getName(), "main");
    }

    private static ModelLayerLocation createLocation(String path, String model) {
        return new ModelLayerLocation(new ResourceLocation(VanillaTweaks.MOD_ID, path), model);
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        if (boat instanceof ModBoatEntity modBoat) {
            return this.boatResources.get(modBoat.getModVariant());
        }

        if (boat instanceof ModChestBoatEntity modChestBoat) {
            return this.boatResources.get(modChestBoat.getModVariant());
        }

        return null;
    }
}
