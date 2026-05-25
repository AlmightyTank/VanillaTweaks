package com.amightytank.vanillatweaks.screen;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.menu.SailboatChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, VanillaTweaks.MOD_ID);

    public static final RegistryObject<MenuType<SailboatChestMenu>> SAILBOAT_CHEST_MENU =
            MENUS.register("sailboat_chest",
                    () -> IForgeMenuType.create(SailboatChestMenu::new));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}