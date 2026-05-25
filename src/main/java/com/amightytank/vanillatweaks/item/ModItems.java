package com.amightytank.vanillatweaks.item;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.item.custom.ModBoatItem;
import com.amightytank.vanillatweaks.item.custom.PirateDynamiteArrowItem;
import com.amightytank.vanillatweaks.item.custom.PiratePatrolSpawnEggItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VanillaTweaks.MOD_ID);

    /*
     * Wood-type sailboat maps.
     *
     * Small  = bannerCount 1
     * Medium = bannerCount 2
     * Large  = bannerCount 3
     *
     * Chests are no longer separate items.
     * Players add chests by right-clicking the placed sailboat with a chest.
     */

    public static final Map<Boat.Type, RegistryObject<Item>> BOATS =
            new EnumMap<>(Boat.Type.class);

    public static final Map<Boat.Type, RegistryObject<Item>> MEDIUM_BOATS =
            new EnumMap<>(Boat.Type.class);

    public static final Map<Boat.Type, RegistryObject<Item>> LARGE_BOATS =
            new EnumMap<>(Boat.Type.class);

    static {
        for (Boat.Type type : Boat.Type.values()) {
            BOATS.put(type, ITEMS.register(type.getName() + "_sailboat",
                    () -> new ModBoatItem(
                            type,
                            1,
                            ModEntities.MOD_BOAT,
                            new Item.Properties().stacksTo(1)
                    )));

            MEDIUM_BOATS.put(type, ITEMS.register(type.getName() + "_medium_sailboat",
                    () -> new ModBoatItem(
                            type,
                            2,
                            ModEntities.MEDIUM_MOD_BOAT,
                            new Item.Properties().stacksTo(1)
                    )));

            LARGE_BOATS.put(type, ITEMS.register(type.getName() + "_large_sailboat",
                    () -> new ModBoatItem(
                            type,
                            3,
                            ModEntities.LARGE_MOD_BOAT,
                            new Item.Properties().stacksTo(1)
                    )));
        }
    }

    /*
     * Oak aliases.
     *
     * These keep your existing ModBoatEntity#getDropItem() code compiling
     * if it still references MOD_BOAT, MEDIUM_SAILBOAT, and LARGE_SAILBOAT.
     */

    public static final RegistryObject<Item> MOD_BOAT = BOATS.get(Boat.Type.OAK);
    public static final RegistryObject<Item> MEDIUM_SAILBOAT = MEDIUM_BOATS.get(Boat.Type.OAK);
    public static final RegistryObject<Item> LARGE_SAILBOAT = LARGE_BOATS.get(Boat.Type.OAK);

    public static final RegistryObject<Item> PIRATE_DECKHAND_SPAWN_EGG = ITEMS.register("pirate_deckhand_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.PIRATE_DECKHAND,
                    0x3A2A1A,
                    0xBFA76A,
                    new Item.Properties()
            ));

    public static final RegistryObject<Item> PIRATE_MARAUDER_SPAWN_EGG = ITEMS.register("pirate_marauder_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.PIRATE_MARAUDER,
                    0x2f3133,
                    0x8b1e1e,
                    new Item.Properties()
            ));

    public static final RegistryObject<Item> PIRATE_CAPTAIN_SPAWN_EGG = ITEMS.register("pirate_captain_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.PIRATE_CAPTAIN,
                    0x111827,
                    0xD4AF37,
                    new Item.Properties()
            ));

    public static final RegistryObject<Item> PIRATE_PARROT_SPAWN_EGG = ITEMS.register("pirate_parrot_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.PIRATE_PARROT,
                    0xC62828,
                    0x2E7D32,
                    new Item.Properties()
            ));

    public static final RegistryObject<Item> PIRATE_GUNNER_SPAWN_EGG = ITEMS.register("pirate_gunner_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.PIRATE_GUNNER,
                    0x1B1B1B,
                    0x8B2E2E,
                    new Item.Properties()
            ));

    public static final RegistryObject<Item> PIRATE_PATROL_SPAWN_EGG = ITEMS.register("pirate_patrol_spawn_egg",
            () -> new PiratePatrolSpawnEggItem(
                    ModEntities.PIRATE_CAPTAIN,
                    0x0B1F33,
                    0xD4AF37,
                    new Item.Properties()
            ));

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

    public static RegistryObject<Item> getBoatItem(Boat.Type type) {
        return BOATS.get(type);
    }

    public static RegistryObject<Item> getMediumBoatItem(Boat.Type type) {
        return MEDIUM_BOATS.get(type);
    }

    public static RegistryObject<Item> getLargeBoatItem(Boat.Type type) {
        return LARGE_BOATS.get(type);
    }

    public static Map<Boat.Type, RegistryObject<Item>> getBoatItems() {
        return Collections.unmodifiableMap(BOATS);
    }

    public static Map<Boat.Type, RegistryObject<Item>> getMediumBoatItems() {
        return Collections.unmodifiableMap(MEDIUM_BOATS);
    }

    public static Map<Boat.Type, RegistryObject<Item>> getLargeBoatItems() {
        return Collections.unmodifiableMap(LARGE_BOATS);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}