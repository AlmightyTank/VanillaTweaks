package com.amightytank.vanillatweaks.event;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.model.*;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VanillaTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.SAILBOAT_LAYER, SailboatModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SAILBOAT_CHEST_LAYER, SailboatChestBoatModel::createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.MEDIUM_SAILBOAT_LAYER, MediumSailboatModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.MEDIUM_SAILBOAT_CHEST_LAYER, MediumSailboatChestBoatModel::createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.LARGE_SAILBOAT_LAYER, LargeSailboatModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.LARGE_SAILBOAT_CHEST_LAYER, LargeChestSailboatModel::createBodyLayer);
    }
}
