package com.amightytank.vanillatweaks;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.client.boat.ModBoatRenderer;
import com.amightytank.vanillatweaks.item.ModCreativeModTabs;
import com.amightytank.vanillatweaks.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(VanillaTweaks.MOD_ID)
public class VanillaTweaks {
    public static final String MOD_ID = "vanillatweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VanillaTweaks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            registerBoatRenderers();
        }

        private static void registerBoatRenderers() {
            EntityRenderers.register(ModEntities.MOD_BOAT.get(),
                    context -> new ModBoatRenderer(context, false));

            EntityRenderers.register(ModEntities.MOD_CHEST_BOAT.get(),
                    context -> new ModBoatRenderer(context, true));

            EntityRenderers.register(ModEntities.MEDIUM_MOD_BOAT.get(),
                    context -> new ModBoatRenderer(context, false));

            EntityRenderers.register(ModEntities.MEDIUM_MOD_CHEST_BOAT.get(),
                    context -> new ModBoatRenderer(context, true));

            EntityRenderers.register(ModEntities.LARGE_MOD_BOAT.get(),
                    context -> new ModBoatRenderer(context, false));

            EntityRenderers.register(ModEntities.LARGE_MOD_CHEST_BOAT.get(),
                    context -> new ModBoatRenderer(context, true));
        }
    }
}