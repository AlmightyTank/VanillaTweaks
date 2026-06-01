package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class PiratePatrolSpawner {
    private static final String PATROL_COOLDOWN_TAG = "PiratePatrolCooldown";

    private static final int MIN_PLAYERS_REQUIRED = 1;
    private static final int PATROL_DELAY_TICKS = 20 * 60 * 5;
    private static final int PLAYER_PATROL_COOLDOWN_TICKS = 20 * 60 * 10;
    private static final int SPAWN_CHANCE_PERCENT = 35;

    /*
     * How close the player must be to surface water before a patrol can spawn.
     * Keeping this at 64 means land can be safer, but shores/coasts still feel dangerous.
     */
    private static final int WATER_SEARCH_RADIUS = 64;

    private static int piratePatrolDelay = PATROL_DELAY_TICKS;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        /*
         * Tick queued pirate boat mounts every server tick.
         *
         * PiratePatrolFormation and PirateShipSpawner queue boarding by 1 tick so
         * the client receives the boat entity before it receives the passenger packet.
         * This helps prevent:
         *
         * "Received passengers for unknown entity"
         */
        for (ServerLevel level : event.getServer().getAllLevels()) {
            PirateBoatPassengerHelper.tickQueuedMounts(level);
            tickPlayerCooldowns(level);
        }

        piratePatrolDelay--;

        if (piratePatrolDelay > 0) {
            return;
        }

        piratePatrolDelay = PATROL_DELAY_TICKS;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            trySpawnPiratePatrol(level);
        }
    }

    private static void trySpawnPiratePatrol(ServerLevel level) {
        List<ServerPlayer> candidates = getValidPatrolTargets(level);

        if (candidates.size() < MIN_PLAYERS_REQUIRED) {
            return;
        }

        ServerPlayer target = candidates.get(level.random.nextInt(candidates.size()));

        if (target.getPersistentData().getInt(PATROL_COOLDOWN_TAG) > 0) {
            return;
        }

        if (level.random.nextInt(100) >= SPAWN_CHANCE_PERCENT) {
            return;
        }

        if (!hasSurfaceWaterNear(level, target.blockPosition(), WATER_SEARCH_RADIUS)) {
            return;
        }

        if (!PiratePatrolFormation.spawnPatrol(level, target).isEmpty()) {
            target.getPersistentData().putInt(PATROL_COOLDOWN_TAG, PLAYER_PATROL_COOLDOWN_TICKS);
            target.displayClientMessage(Component.literal("Pirate patrol incoming!"), false);
        }
    }

    private static void tickPlayerCooldowns(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            int cooldown = player.getPersistentData().getInt(PATROL_COOLDOWN_TAG);

            if (cooldown > 0) {
                player.getPersistentData().putInt(PATROL_COOLDOWN_TAG, Math.max(0, cooldown - 1));
            }
        }
    }

    private static List<ServerPlayer> getValidPatrolTargets(ServerLevel level) {
        List<ServerPlayer> candidates = new ArrayList<>();

        for (ServerPlayer player : level.players()) {
            if (!player.isAlive()) {
                continue;
            }

            if (player.isSpectator() || player.isCreative()) {
                continue;
            }

            if (player.getPersistentData().getInt(PATROL_COOLDOWN_TAG) > 0) {
                continue;
            }

            candidates.add(player);
        }

        return candidates;
    }

    private static boolean hasSurfaceWaterNear(ServerLevel level, BlockPos center, int radius) {
        for (int r = 0; r <= radius; r += 4) {
            for (int x = -r; x <= r; x += 4) {
                for (int z = -r; z <= r; z += 4) {
                    if (Math.abs(x) != r && Math.abs(z) != r) {
                        continue;
                    }

                    if (findWaterSurfaceAt(level, center.getX() + x, center.getZ() + z)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean findWaterSurfaceAt(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        int topY = level.getMaxBuildHeight() - 1;
        int bottomY = level.getMinBuildHeight();

        for (int y = topY; y >= bottomY; y--) {
            mutable.set(x, y, z);

            boolean isWater = level.getFluidState(mutable).is(FluidTags.WATER);
            boolean aboveIsAir = level.getBlockState(mutable.above()).isAir();
            boolean twoAboveIsAir = level.getBlockState(mutable.above(2)).isAir();

            if (isWater && aboveIsAir && twoAboveIsAir) {
                return true;
            }
        }

        return false;
    }
}