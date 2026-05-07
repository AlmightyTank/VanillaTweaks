package com.amightytank.vanillatweaks.item;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.ModBoatEntity;
import com.amightytank.vanillatweaks.item.custom.ModBoatItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VanillaTweaks.MOD_ID);

    public static final Map<ModBoatEntity.Type, RegistryObject<Item>> BOATS = new EnumMap<>(ModBoatEntity.Type.class);
    public static final Map<ModBoatEntity.Type, RegistryObject<Item>> CHEST_BOATS = new EnumMap<>(ModBoatEntity.Type.class);

    static {
        for (ModBoatEntity.Type type : ModBoatEntity.Type.values()) {
            BOATS.put(type, ITEMS.register(type.getItemName(),
                    () -> new ModBoatItem(false, type, new Item.Properties())));

            CHEST_BOATS.put(type, ITEMS.register(type.getChestItemName(),
                    () -> new ModBoatItem(true, type, new Item.Properties())));
        }
    }

    public static RegistryObject<Item> getBoatItem(ModBoatEntity.Type type) {
        return BOATS.get(type);
    }

    public static RegistryObject<Item> getChestBoatItem(ModBoatEntity.Type type) {
        return CHEST_BOATS.get(type);
    }

    public static Map<ModBoatEntity.Type, RegistryObject<Item>> getBoatItems() {
        return Collections.unmodifiableMap(BOATS);
    }

    public static Map<ModBoatEntity.Type, RegistryObject<Item>> getChestBoatItems() {
        return Collections.unmodifiableMap(CHEST_BOATS);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
