package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PiratePatrolSpawner {
    private static int piratePatrolDelay = 20 * 60 * 5; // 5 minutes

    private static final int MIN_SPAWN_DISTANCE = 52;
    private static final int MAX_SPAWN_DISTANCE = 84;
    private static final int SPAWN_ATTEMPTS = 32;

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

        /*
         * 25% chance every 5-minute check while sailing.
         */
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
            BlockPos surfacePos = findNearestSurfaceWater(level, spawnPos, 16);

            if (surfacePos == null) {
                surfacePos = findWaterSpawnPos(level, player.blockPosition(), level.random);
            }

            if (surfacePos != null) {
                PiratePatrolFormation.spawn(level, surfacePos, player, PiratePatrolSize.MEDIUM);
            }

            return;
        }

        if (!level.players().isEmpty()) {
            ServerPlayer player = level.players().get(0);

            BlockPos surfacePos = findNearestSurfaceWater(level, spawnPos, 16);

            if (surfacePos == null) {
                surfacePos = findWaterSpawnPos(level, player.blockPosition(), level.random);
            }

            if (surfacePos != null) {
                PiratePatrolFormation.spawn(level, surfacePos, player, PiratePatrolSize.MEDIUM);
            }
        }
    }

    private static boolean isPlayerSailing(ServerPlayer player) {
        Entity vehicle = player.getVehicle();

        if (vehicle instanceof ModBoatEntity) {
            return true;
        }

        return vehicle instanceof Boat;
    }

    private static BlockPos findWaterSpawnPos(ServerLevel level, BlockPos playerPos, RandomSource random) {
        double baseAngle = random.nextDouble() * Math.PI * 2.0D;

        for (int i = 0; i < SPAWN_ATTEMPTS; i++) {
            int distance = MIN_SPAWN_DISTANCE + random.nextInt(MAX_SPAWN_DISTANCE - MIN_SPAWN_DISTANCE + 1);

            /*
             * Spread attempts around the player, but keep them biased into one loose approach direction.
             */
            double angle = baseAngle + Math.toRadians((i * 23) + random.nextInt(18) - 9);

            int x = playerPos.getX() + (int) (Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int) (Math.sin(angle) * distance);

            BlockPos surface = findWaterSurfaceAt(level, x, z);

            if (surface != null && hasEnoughOpenWater(level, surface, 6)) {
                return surface;
            }
        }

        return null;
    }

    private static BlockPos findNearestSurfaceWater(ServerLevel level, BlockPos center, int radius) {
        for (int r = 0; r <= radius; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (Math.abs(x) != r && Math.abs(z) != r) {
                        continue;
                    }

                    BlockPos surface = findWaterSurfaceAt(level, center.getX() + x, center.getZ() + z);

                    if (surface != null && hasEnoughOpenWater(level, surface, 4)) {
                        return surface;
                    }
                }
            }
        }

        return null;
    }

    private static BlockPos findWaterSurfaceAt(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        int topY = level.getMaxBuildHeight() - 1;
        int bottomY = level.getMinBuildHeight();

        for (int y = topY; y >= bottomY; y--) {
            mutable.set(x, y, z);

            boolean isWater = level.getFluidState(mutable).is(FluidTags.WATER);
            boolean aboveIsAir = level.getBlockState(mutable.above()).isAir();
            boolean twoAboveIsAir = level.getBlockState(mutable.above(2)).isAir();

            if (isWater && aboveIsAir && twoAboveIsAir) {
                /*
                 * Return the air block directly above the water surface.
                 * This prevents boats spawning underwater.
                 */
                return mutable.immutable().above();
            }
        }

        return null;
    }

    private static boolean hasEnoughOpenWater(ServerLevel level, BlockPos surfacePos, int radius) {
        BlockPos waterPos = surfacePos.below();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos checkWater = waterPos.offset(x, 0, z);
                BlockPos checkAir = checkWater.above();

                if (!level.getFluidState(checkWater).is(FluidTags.WATER)) {
                    return false;
                }

                if (!level.getBlockState(checkAir).isAir()) {
                    return false;
                }
            }
        }

        return true;
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