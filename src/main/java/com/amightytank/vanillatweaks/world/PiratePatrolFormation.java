package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.world.pirate_raid.PirateShipSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PiratePatrolFormation {
    private static final double SHIP_SPACING = 8.0D;

    /*
     * Used when picking the main patrol center near the player.
     */
    private static final int CENTER_WATER_SEARCH_RADIUS = 64;

    /*
     * Used for each individual boat in the formation.
     * This lets boats slide slightly onto nearby surface water instead of spawning under it.
     */
    private static final int BOAT_WATER_SEARCH_RADIUS = 8;

    /*
     * Spawn at the air block directly above the surface water block.
     * This prevents boats from starting submerged.
     */
    private static final double BOAT_SURFACE_Y_OFFSET = 1.0D;

    private PiratePatrolFormation() {
    }

    public static List<Mob> spawnPatrol(ServerLevel level, ServerPlayer target) {
        if (level == null || target == null) {
            return List.of();
        }

        PiratePatrolSize size = pickPatrolSize(level);
        return spawn(level, target.blockPosition(), target, size);
    }

    public static List<Mob> spawn(ServerLevel level, BlockPos spawnPos, ServerPlayer target, PiratePatrolSize size) {
        if (level == null || spawnPos == null || size == null) {
            return List.of();
        }

        Vec3 center = Vec3.atBottomCenterOf(spawnPos);

        if (target != null) {
            Vec3 awayFromTarget = center.subtract(target.position());
            awayFromTarget = new Vec3(awayFromTarget.x, 0.0D, awayFromTarget.z);

            if (awayFromTarget.lengthSqr() > 0.001D) {
                awayFromTarget = awayFromTarget.normalize();
                center = target.position().add(awayFromTarget.scale(34.0D));
            }
        }

        /*
         * Do not trust the player's Y.
         * Snap the patrol center onto actual surface water.
         */
        Vec3 surfaceCenter = snapToSurfaceWater(level, center, CENTER_WATER_SEARCH_RADIUS);

        if (surfaceCenter == null) {
            return List.of();
        }

        return spawn(level, surfaceCenter, target, size);
    }

    public static List<Mob> spawn(ServerLevel level, Vec3 center, ServerPlayer target, PiratePatrolSize size) {
        if (level == null || center == null || size == null) {
            return List.of();
        }

        Vec3 surfaceCenter = snapToSurfaceWater(level, center, CENTER_WATER_SEARCH_RADIUS);

        if (surfaceCenter == null) {
            return List.of();
        }

        UUID fleetId = UUID.randomUUID();
        Boat.Type fleetWoodType = getRandomBoatType(level);

        Vec3 forward = getForwardDirection(surfaceCenter, target);
        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);

        return switch (size) {
            case SMALL -> spawnSmallPatrol(level, surfaceCenter, forward, right, fleetWoodType, fleetId);
            case MEDIUM -> spawnMediumPatrol(level, surfaceCenter, forward, right, fleetWoodType, fleetId);
            case LARGE -> spawnLargePatrol(level, surfaceCenter, forward, right, fleetWoodType, fleetId);
        };
    }

    private static List<Mob> spawnSmallPatrol(
            ServerLevel level,
            Vec3 center,
            Vec3 forward,
            Vec3 right,
            Boat.Type woodType,
            UUID fleetId
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        ModBoatEntity captainBoat = spawnCaptainBoatOnWater(
                level,
                center,
                woodType,
                fleetId,
                true
        );

        ModBoatEntity leftCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.SMALL,
                fleetId
        );

        ModBoatEntity rightCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).subtract(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.SMALL,
                fleetId
        );

        faceFleetTowardForward(new ModBoatEntity[]{
                captainBoat,
                leftCombat,
                rightCombat
        }, forward);

        collectBoatPirates(captainBoat, spawnedPirates);
        collectBoatPirates(leftCombat, spawnedPirates);
        collectBoatPirates(rightCombat, spawnedPirates);

        return spawnedPirates;
    }

    private static List<Mob> spawnMediumPatrol(
            ServerLevel level,
            Vec3 center,
            Vec3 forward,
            Vec3 right,
            Boat.Type woodType,
            UUID fleetId
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        ModBoatEntity captainBoat = spawnCaptainBoatOnWater(
                level,
                center,
                woodType,
                fleetId,
                false
        );

        ModBoatEntity leftCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity rightCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).subtract(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity lootBoat = spawnLootBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING * 2.0D)),
                woodType,
                PirateShipSpawner.ShipSize.SMALL,
                fleetId
        );

        faceFleetTowardForward(new ModBoatEntity[]{
                captainBoat,
                leftCombat,
                rightCombat,
                lootBoat
        }, forward);

        collectBoatPirates(captainBoat, spawnedPirates);
        collectBoatPirates(leftCombat, spawnedPirates);
        collectBoatPirates(rightCombat, spawnedPirates);
        collectBoatPirates(lootBoat, spawnedPirates);

        return spawnedPirates;
    }

    private static List<Mob> spawnLargePatrol(
            ServerLevel level,
            Vec3 center,
            Vec3 forward,
            Vec3 right,
            Boat.Type woodType,
            UUID fleetId
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        ModBoatEntity captainBoat = spawnCaptainBoatOnWater(
                level,
                center,
                woodType,
                fleetId,
                false
        );

        ModBoatEntity leftCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING * 1.25D)),
                woodType,
                PirateShipSpawner.ShipSize.LARGE,
                fleetId
        );

        ModBoatEntity middleCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.LARGE,
                fleetId
        );

        ModBoatEntity rightCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).subtract(right.scale(SHIP_SPACING * 1.25D)),
                woodType,
                PirateShipSpawner.ShipSize.LARGE,
                fleetId
        );

        ModBoatEntity leftLoot = spawnLootBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING * 2.0D)).add(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity rightLoot = spawnLootBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING * 2.0D)).subtract(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.MEDIUM,
                fleetId
        );

        faceFleetTowardForward(new ModBoatEntity[]{
                captainBoat,
                leftCombat,
                middleCombat,
                rightCombat,
                leftLoot,
                rightLoot
        }, forward);

        collectBoatPirates(captainBoat, spawnedPirates);
        collectBoatPirates(leftCombat, spawnedPirates);
        collectBoatPirates(middleCombat, spawnedPirates);
        collectBoatPirates(rightCombat, spawnedPirates);
        collectBoatPirates(leftLoot, spawnedPirates);
        collectBoatPirates(rightLoot, spawnedPirates);

        return spawnedPirates;
    }

    private static ModBoatEntity spawnCaptainBoatOnWater(
            ServerLevel level,
            Vec3 intendedPosition,
            Boat.Type woodType,
            UUID fleetId,
            boolean chestBoat
    ) {
        Vec3 spawnPosition = snapToSurfaceWater(level, intendedPosition, BOAT_WATER_SEARCH_RADIUS);

        if (spawnPosition == null) {
            return null;
        }

        return PirateShipSpawner.spawnCaptainShip(
                level,
                spawnPosition,
                woodType,
                fleetId,
                chestBoat
        );
    }

    private static ModBoatEntity spawnCombatBoatOnWater(
            ServerLevel level,
            Vec3 intendedPosition,
            Boat.Type woodType,
            PirateShipSpawner.ShipSize shipSize,
            UUID fleetId
    ) {
        Vec3 spawnPosition = snapToSurfaceWater(level, intendedPosition, BOAT_WATER_SEARCH_RADIUS);

        if (spawnPosition == null) {
            return null;
        }

        return PirateShipSpawner.spawnCombatShip(
                level,
                spawnPosition,
                woodType,
                shipSize,
                fleetId
        );
    }

    private static ModBoatEntity spawnLootBoatOnWater(
            ServerLevel level,
            Vec3 intendedPosition,
            Boat.Type woodType,
            PirateShipSpawner.ShipSize shipSize,
            UUID fleetId
    ) {
        Vec3 spawnPosition = snapToSurfaceWater(level, intendedPosition, BOAT_WATER_SEARCH_RADIUS);

        if (spawnPosition == null) {
            return null;
        }

        return PirateShipSpawner.spawnLootShip(
                level,
                spawnPosition,
                woodType,
                shipSize,
                fleetId
        );
    }

    private static Vec3 snapToSurfaceWater(ServerLevel level, Vec3 position, int radius) {
        if (level == null || position == null) {
            return null;
        }

        int centerX = (int) Math.floor(position.x);
        int centerZ = (int) Math.floor(position.z);

        BlockPos exactSurface = findSurfaceWaterAt(level, centerX, centerZ);

        if (exactSurface != null) {
            return surfaceBlockToBoatPosition(exactSurface);
        }

        for (int r = 1; r <= radius; r++) {
            for (int x = centerX - r; x <= centerX + r; x++) {
                for (int z = centerZ - r; z <= centerZ + r; z++) {
                    if (Math.abs(x - centerX) != r && Math.abs(z - centerZ) != r) {
                        continue;
                    }

                    BlockPos surface = findSurfaceWaterAt(level, x, z);

                    if (surface != null) {
                        return surfaceBlockToBoatPosition(surface);
                    }
                }
            }
        }

        return null;
    }

    private static Vec3 surfaceBlockToBoatPosition(BlockPos waterSurfaceBlock) {
        return new Vec3(
                waterSurfaceBlock.getX() + 0.5D,
                waterSurfaceBlock.getY() + BOAT_SURFACE_Y_OFFSET,
                waterSurfaceBlock.getZ() + 0.5D
        );
    }

    private static BlockPos findSurfaceWaterAt(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        int topY = level.getMaxBuildHeight() - 3;
        int bottomY = level.getMinBuildHeight();

        for (int y = topY; y >= bottomY; y--) {
            mutable.set(x, y, z);

            boolean isWater = level.getFluidState(mutable).is(FluidTags.WATER);
            boolean aboveIsAir = level.getBlockState(mutable.above()).isAir();
            boolean twoAboveIsAir = level.getBlockState(mutable.above(2)).isAir();

            if (isWater && aboveIsAir && twoAboveIsAir) {
                return mutable.immutable();
            }
        }

        return null;
    }

    private static void collectBoatPirates(ModBoatEntity boat, List<Mob> spawnedPirates) {
        if (boat == null || spawnedPirates == null) {
            return;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Mob mob) {
                spawnedPirates.add(mob);
            }
        }
    }

    private static Vec3 getForwardDirection(Vec3 center, ServerPlayer target) {
        if (target == null) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }

        Vec3 towardTarget = target.position().subtract(center);
        towardTarget = new Vec3(towardTarget.x, 0.0D, towardTarget.z);

        if (towardTarget.lengthSqr() < 0.001D) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }

        return towardTarget.normalize();
    }

    private static void faceFleetTowardForward(ModBoatEntity[] boats, Vec3 forward) {
        float yaw = directionToYaw(forward);

        for (ModBoatEntity boat : boats) {
            if (boat == null) {
                continue;
            }

            boat.setYRot(yaw);
            boat.yRotO = yaw;
        }
    }

    private static float directionToYaw(Vec3 direction) {
        return (float) (Math.atan2(direction.z, direction.x) * 180.0D / Math.PI) - 90.0F;
    }

    public static Boat.Type getRandomBoatType(ServerLevel level) {
        Boat.Type[] values = Boat.Type.values();
        return values[level.random.nextInt(values.length)];
    }

    private static PiratePatrolSize pickPatrolSize(ServerLevel level) {
        int roll = level.random.nextInt(100);

        if (roll < 55) {
            return PiratePatrolSize.SMALL;
        }

        if (roll < 85) {
            return PiratePatrolSize.MEDIUM;
        }

        return PiratePatrolSize.LARGE;
    }
}