package com.amightytank.vanillatweaks.registry;

import com.amightytank.vanillatweaks.VanillaTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VanillaTweaks.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VANILLA_TWEAKS_TAB =
            CREATIVE_MODE_TABS.register("vanilla_tweaks_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.vanillatweaks"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.MEDIUM_BOAT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SAIL_BOAT.get());
                        output.accept(ModItems.SAIL_BOAT_UPGRADE_TEMPLATE.get());
                        output.accept(ModItems.MEDIUM_BOAT.get());
                    })
                    .build());
}