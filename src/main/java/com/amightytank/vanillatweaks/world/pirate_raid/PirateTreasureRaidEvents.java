package com.amightytank.vanillatweaks.world.pirate_raid;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PirateTreasureRaidEvents {
    private static final int CHECK_COOLDOWN_TICKS = 20;
    private static final int TRIGGER_RADIUS = 24;

    private static final ResourceLocation BURIED_TREASURE_LOOT =
            new ResourceLocation("minecraft", "chests/buried_treasure");

    private static final Map<UUID, Integer> playerCheckCooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        PirateTreasureRaidManager.tick(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (player.level().dimension() != Level.OVERWORLD) {
            return;
        }

        UUID playerId = player.getUUID();

        tickCheckCooldown(playerId);

        if (playerCheckCooldowns.getOrDefault(playerId, 0) > 0) {
            return;
        }

        playerCheckCooldowns.put(playerId, CHECK_COOLDOWN_TICKS);

        if (!player.hasEffect(MobEffects.BAD_OMEN)) {
            return;
        }

        BlockPos treasurePos = findNearbyBuriedTreasureChest(player, TRIGGER_RADIUS);

        if (treasurePos == null) {
            return;
        }

        PirateTreasureRaidSavedData savedData = PirateTreasureRaidSavedData.get(player.serverLevel());

        if (savedData.hasTriggered(treasurePos)) {
            return;
        }

        savedData.markTriggered(treasurePos);

        player.removeEffect(MobEffects.BAD_OMEN);

        PirateTreasureRaidManager.startRaid(
                player.serverLevel(),
                player,
                treasurePos
        );
    }

    private static void tickCheckCooldown(UUID playerId) {
        int checkCooldown = playerCheckCooldowns.getOrDefault(playerId, 0);

        if (checkCooldown > 0) {
            playerCheckCooldowns.put(playerId, checkCooldown - 1);
        }
    }

    private static BlockPos findNearbyBuriedTreasureChest(ServerPlayer player, int radius) {
        BlockPos playerPos = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-radius, -8, -radius),
                playerPos.offset(radius, 8, radius)
        )) {
            if (!player.level().getBlockState(pos).is(Blocks.CHEST)) {
                continue;
            }

            BlockEntity blockEntity = player.level().getBlockEntity(pos);

            if (!(blockEntity instanceof ChestBlockEntity chest)) {
                continue;
            }

            if (isBuriedTreasureChest(chest)) {
                return pos.immutable();
            }
        }

        return null;
    }

    private static boolean isBuriedTreasureChest(ChestBlockEntity chest) {
        CompoundTag tag = chest.saveWithFullMetadata();

        if (!tag.contains("LootTable")) {
            return false;
        }

        String lootTable = tag.getString("LootTable");

        return lootTable.equals(BURIED_TREASURE_LOOT.toString());
    }

    @SubscribeEvent
    public static void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
        PirateTreasureRaidCommands.register(event.getDispatcher());
    }
}