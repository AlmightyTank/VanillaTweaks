package com.amightytank.vanillatweaks.item;

import com.amightytank.vanillatweaks.VanillaTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VanillaTweaks.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB =
            CREATIVE_MODE_TABS.register("vanilla_tweaks_tab",
                    () -> CreativeModeTab.builder()
                            .icon(() -> new ItemStack(ModItems.MOD_BOAT.get()))
                            .title(Component.translatable("creativetab.vanillatweaks.vanilla_tweaks_tab"))
                            .displayItems((parameters, output) -> {
                                for (Boat.Type type : Boat.Type.values()) {
                                    output.accept(ModItems.getBoatItem(type).get());
                                    output.accept(ModItems.getMediumBoatItem(type).get());
                                    output.accept(ModItems.getLargeBoatItem(type).get());
                                }

                                output.accept(ModItems.PIRATE_CAPTAIN_SPAWN_EGG.get());
                                output.accept(ModItems.PIRATE_PARROT_SPAWN_EGG.get());
                                output.accept(ModItems.PIRATE_MARAUDER_SPAWN_EGG.get());
                                output.accept(ModItems.PIRATE_GUNNER_SPAWN_EGG.get());
                                output.accept(ModItems.PIRATE_DECKHAND_SPAWN_EGG.get());
                                output.accept(ModItems.PIRATE_PATROL_SPAWN_EGG.get());
                                output.accept(ModItems.PIRATE_DYNAMITE_ARROW.get());
                                output.accept(ModItems.BOAT_UPGRADE_TEMPLATE.get());
                            })
                            .build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}