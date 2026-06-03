package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.amightytank.vanillatweaks.util.PirateLootHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PiratePatrolFormation {
    private static final double SHIP_SPACING = 8.0D;

    private static final int CENTER_WATER_SEARCH_RADIUS = 64;
    private static final int BOAT_WATER_SEARCH_RADIUS = 8;

    /*
     * Spawn at the air block directly above the surface water block.
     * This prevents boats from starting submerged.
     */
    private static final double BOAT_SURFACE_Y_OFFSET = 1.0D;

    private static final String RAID_BOAT_TAG = "PirateRaidBoat";
    private static final String CAPTAIN_TAG = "PirateRaidCaptain";
    private static final String RANGED_TAG = "PirateRaidRanged";
    private static final String BOAT_UUID_TAG_PREFIX = "PirateRaidBoatUUID:";

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

        /*
         * Small patrol:
         * large captain loot boat with 1 chest = 3 pirates
         * 2 small combat boats = 2 pirates each
         * total = 7 pirates
         */
        ModBoatEntity captainBoat = spawnCaptainBoatOnWater(
                level,
                center,
                woodType,
                fleetId,
                1
        );

        ModBoatEntity leftCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING)),
                woodType,
                PatrolShipSize.SMALL,
                fleetId
        );

        ModBoatEntity rightCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).subtract(right.scale(SHIP_SPACING)),
                woodType,
                PatrolShipSize.SMALL,
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

        /*
         * Medium patrol:
         * large captain boat with no chest = 4 pirates
         * 2 medium combat boats = 3 pirates each
         * 1 small loot boat with 1 chest = 1 pirate
         * total = 11 pirates
         */
        ModBoatEntity captainBoat = spawnCaptainBoatOnWater(
                level,
                center,
                woodType,
                fleetId,
                0
        );

        ModBoatEntity leftCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING)),
                woodType,
                PatrolShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity rightCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).subtract(right.scale(SHIP_SPACING)),
                woodType,
                PatrolShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity lootBoat = spawnLootBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING * 2.0D)),
                woodType,
                PatrolShipSize.SMALL,
                1,
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

        /*
         * Large patrol:
         * large captain boat with no chest = 4 pirates
         * 2 large combat boats = 4 pirates each
         * 1 medium combat boat = 3 pirates
         * 1 medium loot boat with 2 chests = 1 pirate
         * total = 16 pirates
         */
        ModBoatEntity captainBoat = spawnCaptainBoatOnWater(
                level,
                center,
                woodType,
                fleetId,
                0
        );

        ModBoatEntity leftCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING * 1.25D)),
                woodType,
                PatrolShipSize.LARGE,
                fleetId
        );

        ModBoatEntity middleCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)),
                woodType,
                PatrolShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity rightCombat = spawnCombatBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).subtract(right.scale(SHIP_SPACING * 1.25D)),
                woodType,
                PatrolShipSize.LARGE,
                fleetId
        );

        ModBoatEntity lootBoat = spawnLootBoatOnWater(
                level,
                center.subtract(forward.scale(SHIP_SPACING * 2.0D)),
                woodType,
                PatrolShipSize.MEDIUM,
                2,
                fleetId
        );

        faceFleetTowardForward(new ModBoatEntity[]{
                captainBoat,
                leftCombat,
                middleCombat,
                rightCombat,
                lootBoat
        }, forward);

        collectBoatPirates(captainBoat, spawnedPirates);
        collectBoatPirates(leftCombat, spawnedPirates);
        collectBoatPirates(middleCombat, spawnedPirates);
        collectBoatPirates(rightCombat, spawnedPirates);
        collectBoatPirates(lootBoat, spawnedPirates);

        return spawnedPirates;
    }

    private static ModBoatEntity spawnCaptainBoatOnWater(
            ServerLevel level,
            Vec3 intendedPosition,
            Boat.Type woodType,
            UUID fleetId,
            int chestCount
    ) {
        Vec3 spawnPosition = snapToSurfaceWater(level, intendedPosition, BOAT_WATER_SEARCH_RADIUS);

        if (spawnPosition == null) {
            return null;
        }

        PatrolShipSize shipSize = PatrolShipSize.LARGE;
        chestCount = clampChestCount(shipSize, chestCount);

        ModBoatEntity boat = spawnPatrolSailboat(
                level,
                spawnPosition,
                woodType,
                shipSize,
                chestCount,
                true,
                fleetId
        );

        if (boat == null) {
            return null;
        }

        if (chestCount > 0) {
            boat.addTag("PirateLootShip");
            PirateLootHelper.fillPirateLootShip(level, boat);
        }

        int seats = getSeatCountForSize(shipSize, chestCount);
        int filledSeats = 0;

        PirateCaptainEntity captain = spawnCaptain(level, spawnPosition.add(0.0D, 0.25D, 0.0D));

        if (captain != null) {
            setupPatrolPirate(captain, boat, fleetId, CAPTAIN_TAG);

            if (addPirateToBoat(boat, captain)) {
                PirateBoatPassengerHelper.assignHomeBoat(captain, boat);
                filledSeats++;
            }
        }

        while (filledSeats < seats) {
            Mob crew;
            String roleTag;

            if (filledSeats % 2 == 0) {
                crew = spawnGunner(level, spawnPosition.add(0.0D, 0.25D, 0.0D));
                roleTag = RANGED_TAG;
            } else {
                crew = spawnDeckhand(level, spawnPosition.add(0.0D, 0.25D, 0.0D));
                roleTag = PirateBoatPassengerHelper.BOARDER_TAG;
            }

            if (crew == null) {
                break;
            }

            setupPatrolPirate(crew, boat, fleetId, roleTag);

            if (addPirateToBoat(boat, crew)) {
                PirateBoatPassengerHelper.assignHomeBoat(crew, boat);
                filledSeats++;
            } else {
                break;
            }
        }

        boat.addTag(PirateBoatPassengerHelper.CAPTAIN_SHIP_TAG);

        return boat;
    }

    private static ModBoatEntity spawnCombatBoatOnWater(
            ServerLevel level,
            Vec3 intendedPosition,
            Boat.Type woodType,
            PatrolShipSize shipSize,
            UUID fleetId
    ) {
        Vec3 spawnPosition = snapToSurfaceWater(level, intendedPosition, BOAT_WATER_SEARCH_RADIUS);

        if (spawnPosition == null) {
            return null;
        }

        int chestCount = 0;

        ModBoatEntity boat = spawnPatrolSailboat(
                level,
                spawnPosition,
                woodType,
                shipSize,
                chestCount,
                false,
                fleetId
        );

        if (boat == null) {
            return null;
        }

        int seats = getSeatCountForSize(shipSize, chestCount);
        int filledSeats = 0;

        Mob pilot = spawnMarauder(level, spawnPosition.add(0.0D, 0.25D, 0.0D));

        if (pilot == null) {
            pilot = spawnDeckhand(level, spawnPosition.add(0.0D, 0.25D, 0.0D));
        }

        if (pilot != null) {
            setupPatrolPirate(pilot, boat, fleetId, PirateBoatPassengerHelper.BOARDER_TAG);

            if (addPirateToBoat(boat, pilot)) {
                PirateBoatPassengerHelper.assignHomeBoat(pilot, boat);
                filledSeats++;
            }
        }

        while (filledSeats < seats) {
            Mob crew;
            String roleTag;

            if (filledSeats % 2 == 0) {
                crew = spawnGunner(level, spawnPosition.add(0.0D, 0.25D, 0.0D));
                roleTag = RANGED_TAG;
            } else {
                crew = spawnDeckhand(level, spawnPosition.add(0.0D, 0.25D, 0.0D));
                roleTag = PirateBoatPassengerHelper.BOARDER_TAG;
            }

            if (crew == null) {
                break;
            }

            setupPatrolPirate(crew, boat, fleetId, roleTag);

            if (addPirateToBoat(boat, crew)) {
                PirateBoatPassengerHelper.assignHomeBoat(crew, boat);
                filledSeats++;
            } else {
                break;
            }
        }

        return boat;
    }

    private static ModBoatEntity spawnLootBoatOnWater(
            ServerLevel level,
            Vec3 intendedPosition,
            Boat.Type woodType,
            PatrolShipSize shipSize,
            int chestCount,
            UUID fleetId
    ) {
        Vec3 spawnPosition = snapToSurfaceWater(level, intendedPosition, BOAT_WATER_SEARCH_RADIUS);

        if (spawnPosition == null) {
            return null;
        }

        chestCount = clampChestCount(shipSize, chestCount);

        ModBoatEntity boat = spawnPatrolSailboat(
                level,
                spawnPosition,
                woodType,
                shipSize,
                chestCount,
                false,
                fleetId
        );

        if (boat == null) {
            return null;
        }

        boat.addTag("PirateLootShip");
        PirateLootHelper.fillPirateLootShip(level, boat);

        int seats = getSeatCountForSize(shipSize, chestCount);
        int filledSeats = 0;

        while (filledSeats < seats) {
            Mob crew;
            String roleTag;

            if (filledSeats == 0) {
                crew = spawnDeckhand(level, spawnPosition.add(0.0D, 0.25D, 0.0D));
                roleTag = PirateBoatPassengerHelper.BOARDER_TAG;
            } else {
                crew = spawnGunner(level, spawnPosition.add(0.0D, 0.25D, 0.0D));
                roleTag = RANGED_TAG;
            }

            if (crew == null) {
                break;
            }

            setupPatrolPirate(crew, boat, fleetId, roleTag);

            if (addPirateToBoat(boat, crew)) {
                PirateBoatPassengerHelper.assignHomeBoat(crew, boat);
                filledSeats++;
            } else {
                break;
            }
        }

        return boat;
    }

    private static ModBoatEntity spawnPatrolSailboat(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            PatrolShipSize shipSize,
            int chestCount,
            boolean captainShip,
            UUID fleetId
    ) {
        ModBoatEntity boat = createBoatForSize(level, shipSize);

        if (boat == null) {
            return null;
        }

        chestCount = clampChestCount(shipSize, chestCount);

        boat.moveTo(position.x, position.y, position.z, level.random.nextFloat() * 360.0F, 0.0F);

        boat.setModVariant(woodType);
        boat.setBannerStack(Raid.getLeaderBannerInstance());
        boat.setBannerCount(getBannerCountForSize(shipSize));
        boat.setChestCount(chestCount);

        setupPatrolBoat(boat, fleetId);

        if (captainShip) {
            boat.addTag(PirateBoatPassengerHelper.CAPTAIN_SHIP_TAG);
        }

        level.addFreshEntity(boat);

        return boat;
    }

    private static ModBoatEntity createBoatForSize(ServerLevel level, PatrolShipSize shipSize) {
        return switch (shipSize) {
            case SMALL -> ModEntities.MOD_BOAT.get().create(level);
            case MEDIUM -> ModEntities.MEDIUM_MOD_BOAT.get().create(level);
            case LARGE -> ModEntities.LARGE_MOD_BOAT.get().create(level);
        };
    }

    private static void setupPatrolBoat(ModBoatEntity boat, UUID fleetId) {
        boat.addTag(PirateBoatPassengerHelper.RAID_PIRATE_TAG);
        boat.addTag(RAID_BOAT_TAG);
        boat.addTag(PirateBoatPassengerHelper.FLEET_TAG_PREFIX + fleetId);
    }

    private static void setupPatrolPirate(Mob pirate, ModBoatEntity boat, UUID fleetId, String roleTag) {
        pirate.addTag(PirateBoatPassengerHelper.RAID_PIRATE_TAG);
        pirate.addTag(PirateBoatPassengerHelper.FLEET_TAG_PREFIX + fleetId);
        pirate.addTag(BOAT_UUID_TAG_PREFIX + boat.getUUID());

        if (roleTag != null && !roleTag.isBlank()) {
            pirate.addTag(roleTag);
        }
    }

    private static boolean addPirateToBoat(ModBoatEntity boat, Mob pirate) {
        if (boat == null || pirate == null) {
            return false;
        }

        if (boat.addMobToSailboat(pirate)) {
            return true;
        }

        /*
         * Do not force startRiding(boat, true).
         * That can bypass your custom sailboat seat limits and overfill boats.
         */
        pirate.discard();
        return false;
    }

    private static PirateCaptainEntity spawnCaptain(ServerLevel level, Vec3 position) {
        PirateCaptainEntity captain = ModEntities.PIRATE_CAPTAIN.get().create(level);

        if (captain == null) {
            return null;
        }

        finishSpawn(level, captain, position);
        return captain;
    }

    private static Mob spawnDeckhand(ServerLevel level, Vec3 position) {
        Mob pirate = ModEntities.PIRATE_DECKHAND.get().create(level);

        if (pirate == null) {
            return null;
        }

        finishSpawn(level, pirate, position);
        return pirate;
    }

    private static Mob spawnGunner(ServerLevel level, Vec3 position) {
        Mob pirate = ModEntities.PIRATE_GUNNER.get().create(level);

        if (pirate == null) {
            return null;
        }

        finishSpawn(level, pirate, position);
        return pirate;
    }

    private static Mob spawnMarauder(ServerLevel level, Vec3 position) {
        Mob pirate = ModEntities.PIRATE_MARAUDER.get().create(level);

        if (pirate == null) {
            return null;
        }

        finishSpawn(level, pirate, position);
        return pirate;
    }

    private static void finishSpawn(ServerLevel level, Mob mob, Vec3 position) {
        mob.moveTo(position.x, position.y, position.z, level.random.nextFloat() * 360.0F, 0.0F);

        DifficultyInstance difficulty = level.getCurrentDifficultyAt(BlockPos.containing(position));

        mob.finalizeSpawn(
                level,
                difficulty,
                MobSpawnType.EVENT,
                (SpawnGroupData) null,
                null
        );

        level.addFreshEntity(mob);
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

        if (roll < 90) {
            return PiratePatrolSize.MEDIUM;
        }

        return PiratePatrolSize.LARGE;
    }

    private static int getMaxChestCountForSize(PatrolShipSize shipSize) {
        return switch (shipSize) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    private static int clampChestCount(PatrolShipSize shipSize, int chestCount) {
        return Math.max(0, Math.min(chestCount, getMaxChestCountForSize(shipSize)));
    }

    private static int getSeatCountForSize(PatrolShipSize shipSize, int chestCount) {
        chestCount = clampChestCount(shipSize, chestCount);

        int baseSeats = switch (shipSize) {
            case SMALL -> 2;
            case MEDIUM -> 3;
            case LARGE -> 4;
        };

        return Math.max(1, baseSeats - chestCount);
    }

    private static int getBannerCountForSize(PatrolShipSize shipSize) {
        return switch (shipSize) {
            case SMALL -> 1;
            case MEDIUM -> 1;
            case LARGE -> 2;
        };
    }

    private enum PatrolShipSize {
        SMALL,
        MEDIUM,
        LARGE
    }
}