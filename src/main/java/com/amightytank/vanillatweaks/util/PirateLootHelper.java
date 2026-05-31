package com.amightytank.vanillatweaks.util;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class PirateLootHelper {

    private static final ResourceLocation PIRATE_LOOT_SHIP =
            new ResourceLocation(VanillaTweaks.MOD_ID, "chests/pirate_loot_ship");

    public static void fillPirateLootShip(ServerLevel level, ModBoatEntity boat) {
        if (boat.getChestCount() <= 0) {
            return;
        }

        LootTable lootTable = level.getServer()
                .getLootData()
                .getLootTable(PIRATE_LOOT_SHIP);

        LootParams lootParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, boat.position())
                .withOptionalParameter(LootContextParams.THIS_ENTITY, boat)
                .create(LootContextParamSets.CHEST);

        lootTable.fill(boat, lootParams, level.random.nextLong());
    }
}