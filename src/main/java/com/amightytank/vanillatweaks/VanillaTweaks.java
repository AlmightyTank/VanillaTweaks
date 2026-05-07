package com.amightytank.vanillatweaks;

import com.amightytank.vanillatweaks.client.MediumBoatRenderer;
import com.amightytank.vanillatweaks.client.SailBoatRenderer;
import com.amightytank.vanillatweaks.client.model.MediumBoatModel;
import com.amightytank.vanillatweaks.client.model.SailBoatModel;
import com.amightytank.vanillatweaks.registry.ModCreativeTabs;
import com.amightytank.vanillatweaks.registry.ModEntities;
import com.amightytank.vanillatweaks.registry.ModItems;
import com.amightytank.vanillatweaks.registry.ModRecipeSerializers;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

@Mod(VanillaTweaks.MODID)
public class VanillaTweaks {
    public static final String MODID = "vanillatweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VanillaTweaks(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @EventBusSubscriber(modid = VanillaTweaks.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class ClientModEvents {

        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(
                    ModEntities.MEDIUM_BOAT.get(),
                    MediumBoatRenderer::new
            );
            event.registerEntityRenderer(
                    ModEntities.SAIL_BOAT.get(),
                    SailBoatRenderer::new
            );
        }

        @SubscribeEvent
        static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(
                    MediumBoatModel.LAYER_LOCATION,
                    MediumBoatModel::createBodyLayer
            );

            event.registerLayerDefinition(
                    SailBoatModel.LAYER_LOCATION,
                    SailBoatModel::createBodyLayer
            );
        }
    }
}