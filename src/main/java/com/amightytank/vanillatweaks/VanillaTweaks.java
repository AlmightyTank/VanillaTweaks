package com.amightytank.vanillatweaks;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.client.boat.ModBoatRenderer;
import com.amightytank.vanillatweaks.item.ModCreativeModTabs;
import com.amightytank.vanillatweaks.item.ModItems;
import com.amightytank.vanillatweaks.network.ModMessages;
import com.amightytank.vanillatweaks.screen.ModMenuTypes;
import com.amightytank.vanillatweaks.screen.SailboatChestScreen;
import com.amightytank.vanillatweaks.world.PiratePatrolSpawner;
import com.amightytank.vanillatweaks.world.pirate_raid.PirateTreasureRaidEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
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
        ModMenuTypes.register(modEventBus);
        ModMessages.register();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PiratePatrolSpawner.class);
        MinecraftForge.EVENT_BUS.register(PirateTreasureRaidEvents.class);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                registerBoatRenderers();
                MenuScreens.register(ModMenuTypes.SAILBOAT_CHEST_MENU.get(), SailboatChestScreen::new);
            });
        }

        private static void registerBoatRenderers() {
            EntityRenderers.register(
                    ModEntities.MOD_BOAT.get(),
                    ModBoatRenderer::new
            );

            EntityRenderers.register(
                    ModEntities.MEDIUM_MOD_BOAT.get(),
                    ModBoatRenderer::new
            );

            EntityRenderers.register(
                    ModEntities.LARGE_MOD_BOAT.get(),
                    ModBoatRenderer::new
            );
        }
    }
}