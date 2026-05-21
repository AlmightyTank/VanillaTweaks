package com.amightytank.vanillatweaks.event;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.pirate.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VanillaTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.PIRATE_CAPTAIN.get(), PirateCaptainEntity.createAttributes().build());
        event.put(ModEntities.PIRATE_PARROT.get(), PirateParrotEntity.createAttributes().build());
        event.put(ModEntities.PIRATE_BRUTE.get(), PirateBruteEntity.createAttributes().build());
        event.put(ModEntities.PIRATE_GUNNER.get(), PirateGunnerEntity.createAttributes().build());
        event.put(ModEntities.PIRATE_DECKHAND.get(), PirateDeckhandEntity.createAttributes().build());
    }
}