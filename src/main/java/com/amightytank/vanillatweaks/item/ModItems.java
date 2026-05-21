package com.amightytank.vanillatweaks.item;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.item.custom.ModBoatItem;
import com.amightytank.vanillatweaks.item.custom.PirateDynamiteArrowItem;
import com.amightytank.vanillatweaks.item.custom.PiratePatrolSpawnEggItem;
import com.amightytank.vanillatweaks.item.custom.PirateSpearItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VanillaTweaks.MOD_ID);

    public static final Map<ModBoatEntity.Type, RegistryObject<Item>> BOATS =
            new EnumMap<>(ModBoatEntity.Type.class);

    public static final Map<ModBoatEntity.Type, RegistryObject<Item>> CHEST_BOATS =
            new EnumMap<>(ModBoatEntity.Type.class);

    static {
        for (ModBoatEntity.Type type : ModBoatEntity.Type.values()) {
            BOATS.put(type, ITEMS.register(type.getItemName(),
                    () -> new ModBoatItem(false, type, getBoatEntity(type, false), new Item.Properties())));

            CHEST_BOATS.put(type, ITEMS.register(type.getChestItemName(),
                    () -> new ModBoatItem(true, type, getBoatEntity(type, true), new Item.Properties())));
        }
    }

    public static final RegistryObject<Item> PIRATE_DECKHAND_SPAWN_EGG = ITEMS.register("pirate_deckhand_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PIRATE_DECKHAND, 0x3A2A1A, 0xBFA76A, new Item.Properties()));

    public static final RegistryObject<Item> PIRATE_BRUTE_SPAWN_EGG = ITEMS.register("pirate_brute_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PIRATE_BRUTE, 0x4A2C1A, 0xC0392B, new Item.Properties()));

    public static final RegistryObject<Item> PIRATE_CAPTAIN_SPAWN_EGG = ITEMS.register("pirate_captain_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PIRATE_CAPTAIN, 0x111827, 0xD4AF37, new Item.Properties()));

    public static final RegistryObject<Item> PIRATE_PARROT_SPAWN_EGG = ITEMS.register("pirate_parrot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PIRATE_PARROT, 0xC62828, 0x2E7D32, new Item.Properties()));

    public static final RegistryObject<Item> PIRATE_GUNNER_SPAWN_EGG = ITEMS.register("pirate_gunner_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PIRATE_GUNNER, 0x1B1B1B, 0x8B2E2E, new Item.Properties()));

    public static final RegistryObject<Item> PIRATE_PATROL_SPAWN_EGG = ITEMS.register("pirate_patrol_spawn_egg",
            () -> new PiratePatrolSpawnEggItem(
                    ModEntities.PIRATE_CAPTAIN,
                    0x0B1F33,
                    0xD4AF37,
                    new Item.Properties()
            ));

    public static final RegistryObject<Item> PIRATE_SPEAR = ITEMS.register("pirate_spear",
            () -> new PirateSpearItem(new Item.Properties().durability(250)));

    public static final RegistryObject<Item> PIRATE_DYNAMITE_ARROW = ITEMS.register("pirate_dynamite_arrow",
            () -> new PirateDynamiteArrowItem(new Item.Properties()));

    public static final RegistryObject<Item> BOAT_UPGRADE_TEMPLATE = ITEMS.register("boat_upgrade_template",
            () -> new SmithingTemplateItem(
                    Component.translatable("item.vanillatweaks.smithing_template.boat_upgrade.applies_to"),
                    Component.translatable("item.vanillatweaks.smithing_template.boat_upgrade.ingredients"),
                    Component.translatable("item.vanillatweaks.smithing_template.boat_upgrade.title"),
                    Component.translatable("item.vanillatweaks.smithing_template.boat_upgrade.base_slot_description"),
                    Component.translatable("item.vanillatweaks.smithing_template.boat_upgrade.additions_slot_description"),
                    List.of(
                            new ResourceLocation("minecraft", "item/empty_slot_chestplate")
                    ),
                    List.of(
                            new ResourceLocation("minecraft", "item/empty_slot_planks")
                    )
            ));

    private static Supplier<? extends EntityType<? extends Boat>> getBoatEntity(ModBoatEntity.Type type, boolean chest) {
        return switch (type.getBoatSize()) {
            case LARGE_SAILBOAT -> chest
                    ? ModEntities.LARGE_MOD_CHEST_BOAT
                    : ModEntities.LARGE_MOD_BOAT;

            case MEDIUM_SAILBOAT -> chest
                    ? ModEntities.MEDIUM_MOD_CHEST_BOAT
                    : ModEntities.MEDIUM_MOD_BOAT;

            case SAILBOAT -> chest
                    ? ModEntities.MOD_CHEST_BOAT
                    : ModEntities.MOD_BOAT;
        };
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