package com.amightytank.vanillatweaks.world.pirate_raid;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PirateTreasureRaidManager {
    private static final List<PirateTreasureRaid> ACTIVE_RAIDS = new ArrayList<>();

    public static void startRaid(ServerLevel level, ServerPlayer player, BlockPos treasurePos) {
        PirateRaidSize raidSize = chooseRaidSize(level);

        startRaid(level, player, treasurePos, raidSize);
    }

    public static void startRaid(ServerLevel level, ServerPlayer player, BlockPos treasurePos, PirateRaidSize raidSize) {
        PirateTreasureRaid raid = new PirateTreasureRaid(
                level.dimension(),
                player.getUUID(),
                treasurePos,
                raidSize
        );

        ACTIVE_RAIDS.add(raid);

        player.displayClientMessage(
                Component.literal("Pirates are guarding this treasure!"),
                true
        );

        player.displayClientMessage(
                Component.literal("Pirate Treasure Raid: " + getDisplayName(raidSize)),
                false
        );
    }

    public static void tick(MinecraftServer server) {
        if (server == null) {
            return;
        }

        Iterator<PirateTreasureRaid> iterator = ACTIVE_RAIDS.iterator();

        while (iterator.hasNext()) {
            PirateTreasureRaid raid = iterator.next();

            ServerLevel level = server.getLevel(raid.getDimension());

            if (level == null) {
                iterator.remove();
                continue;
            }

            raid.tick(level);

            if (raid.isFinished()) {
                iterator.remove();
            }
        }
    }

    private static PirateRaidSize chooseRaidSize(ServerLevel level) {
        int roll = level.random.nextInt(100);

        if (roll < 50) {
            return PirateRaidSize.SMALL;
        }

        if (roll < 85) {
            return PirateRaidSize.MEDIUM;
        }

        return PirateRaidSize.LARGE;
    }

    private static String getDisplayName(PirateRaidSize raidSize) {
        return switch (raidSize) {
            case SMALL -> "Small";
            case MEDIUM -> "Medium";
            case LARGE -> "Large";
        };
    }
}