package com.amightytank.vanillatweaks.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PiratePatrolSpawner {
    private static int piratePatrolDelay = 20 * 60 * 5; // 5 minutes

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        piratePatrolDelay--;

        if (piratePatrolDelay > 0) {
            return;
        }

        piratePatrolDelay = 20 * 60 * 5;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            trySpawnPiratePatrol(level);
        }
    }

    private static void trySpawnPiratePatrol(ServerLevel level) {
        RandomSource random = level.random;

        if (level.players().isEmpty()) {
            return;
        }

        ServerPlayer player = level.players().get(random.nextInt(level.players().size()));

        if (!isPlayerSailing(player)) {
            return;
        }

        // 25% chance every 5-minute check while sailing.
        if (random.nextFloat() > 0.25F) {
            return;
        }

        BlockPos spawnPos = findWaterSpawnPos(level, player.blockPosition(), random);

        if (spawnPos == null) {
            return;
        }

        PiratePatrolSize size = pickPatrolSize(random);

        PiratePatrolFormation.spawn(level, spawnPos, player, size);
    }

    public static void spawnTestPatrol(ServerLevel level, BlockPos spawnPos, Entity user) {
        if (user instanceof ServerPlayer player) {
            PiratePatrolFormation.spawn(level, spawnPos, player, PiratePatrolSize.MEDIUM);
            return;
        }

        if (!level.players().isEmpty()) {
            ServerPlayer player = level.players().get(0);
            PiratePatrolFormation.spawn(level, spawnPos, player, PiratePatrolSize.MEDIUM);
        }
    }

    private static boolean isPlayerSailing(ServerPlayer player) {
        Entity vehicle = player.getVehicle();

        return vehicle instanceof Boat;
    }

    private static BlockPos findWaterSpawnPos(ServerLevel level, BlockPos playerPos, RandomSource random) {
        for (int i = 0; i < 20; i++) {
            int distance = 48 + random.nextInt(32); // 48-80 blocks away
            double angle = random.nextDouble() * Math.PI * 2.0D;

            int x = playerPos.getX() + (int) (Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int) (Math.sin(angle) * distance);

            BlockPos pos = new BlockPos(x, level.getSeaLevel(), z);
            BlockPos surface = findWaterSurface(level, pos);

            if (surface != null) {
                return surface;
            }
        }

        return null;
    }

    private static BlockPos findWaterSurface(ServerLevel level, BlockPos pos) {
        for (int y = level.getSeaLevel() + 6; y >= level.getSeaLevel() - 8; y--) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());

            boolean water = level.getBlockState(checkPos).is(Blocks.WATER);
            boolean airAbove = level.getBlockState(checkPos.above()).isAir();

            if (water && airAbove) {
                return checkPos;
            }
        }

        return null;
    }

    private static PiratePatrolSize pickPatrolSize(RandomSource random) {
        float roll = random.nextFloat();

        if (roll < 0.55F) {
            return PiratePatrolSize.SMALL;
        }

        if (roll < 0.85F) {
            return PiratePatrolSize.MEDIUM;
        }

        return PiratePatrolSize.LARGE;
    }
}