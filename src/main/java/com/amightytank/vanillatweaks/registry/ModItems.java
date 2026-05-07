package com.amightytank.vanillatweaks.registry;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.item.MediumBoatItem;
import com.amightytank.vanillatweaks.item.SailBoatItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(VanillaTweaks.MODID);

    public static final DeferredItem<Item> SAIL_BOAT =
            ITEMS.register("sail_boat", () ->
                    new SailBoatItem(new Item.Properties().stacksTo(1))
            );

    public static final DeferredItem<Item> SAIL_BOAT_UPGRADE_TEMPLATE =
            ITEMS.register("sail_boat_upgrade_template", () ->
                    new Item(new Item.Properties())
            );

    public static final DeferredItem<Item> MEDIUM_BOAT =
            ITEMS.register("medium_boat", () ->
                    new MediumBoatItem(new Item.Properties().stacksTo(1))
            );
}