package com.amightytank.vanillatweaks.util;

import com.amightytank.vanillatweaks.VanillaTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class PirateLootHelper {

    public static final ResourceLocation PIRATE_LOOT_SHIP =
            new ResourceLocation(VanillaTweaks.MOD_ID, "chests/pirate_loot_ship");

    public static void fillLootShipChest(ServerLevel level, Entity boatEntity, Container container) {
        var lootTable = level.getServer()
                .getLootData()
                .getLootTable(PIRATE_LOOT_SHIP);

        LootParams lootParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, boatEntity.position())
                .withParameter(LootContextParams.THIS_ENTITY, boatEntity)
                .create(LootContextParamSets.CHEST);

        lootTable.fill(container, lootParams, level.random.nextLong());
    }
}