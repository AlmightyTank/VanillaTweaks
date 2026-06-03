package com.amightytank.vanillatweaks.world.pirate_raid;

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

public final class PirateShipSpawner {
    public static final String RAID_BOAT_TAG = "PirateRaidBoat";
    public static final String CAPTAIN_TAG = "PirateRaidCaptain";
    public static final String RANGED_TAG = "PirateRaidRanged";
    public static final String BOAT_UUID_TAG_PREFIX = "PirateRaidBoatUUID:";

    private static final int BOAT_WATER_SEARCH_RADIUS = 14;
    private static final double BOAT_SURFACE_Y_OFFSET = 1.0D;

    private PirateShipSpawner() {
    }

    public static List<Mob> spawnWave(
            ServerLevel level,
            ServerPlayer player,
            BlockPos treasurePos,
            int currentWave,
            UUID raidId,
            List<PirateShipSpawnEntry> wavePlan
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        if (level == null || player == null || treasurePos == null || wavePlan == null || wavePlan.isEmpty()) {
            return spawnedPirates;
        }

        UUID fleetId = raidId != null ? raidId : UUID.randomUUID();
        Boat.Type woodType = getRandomBoatType(level);

        Vec3 center = findBestWaveBoatSpawnCenter(level, player, treasurePos, currentWave);
        Vec3 forward = getForwardDirection(center, player);
        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);

        boolean firstShipIsCaptain = isCaptainShip(wavePlan.get(0));

        for (int i = 0; i < wavePlan.size(); i++) {
            PirateShipSpawnEntry entry = wavePlan.get(i);

            Vec3 intendedPosition = getFormationPosition(
                    center,
                    forward,
                    right,
                    i,
                    firstShipIsCaptain
            );

            Vec3 spawnPosition = snapToSurfaceWater(level, intendedPosition, BOAT_WATER_SEARCH_RADIUS);

            if (spawnPosition == null) {
                continue;
            }

            ModBoatEntity boat = spawnShipFromEntry(
                    level,
                    spawnPosition,
                    woodType,
                    fleetId,
                    entry
            );

            faceBoatTowardForward(boat, forward);
            collectBoatPirates(boat, spawnedPirates);
        }

        return spawnedPirates;
    }

    private static ModBoatEntity spawnShipFromEntry(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            UUID fleetId,
            PirateShipSpawnEntry entry
    ) {
        return switch (entry.role()) {
            case COMBAT -> spawnCombatShip(
                    level,
                    position,
                    woodType,
                    entry.size(),
                    fleetId
            );

            case LOOT -> spawnLootShip(
                    level,
                    position,
                    woodType,
                    entry.size(),
                    1,
                    fleetId
            );

            case CAPTAIN -> spawnCaptainShip(
                    level,
                    position,
                    woodType,
                    fleetId,
                    0
            );

            case CAPTAIN_LOOT -> spawnCaptainShip(
                    level,
                    position,
                    woodType,
                    fleetId,
                    1
            );
        };
    }

    private static boolean isCaptainShip(PirateShipSpawnEntry entry) {
        return entry.role() == PirateShipRole.CAPTAIN || entry.role() == PirateShipRole.CAPTAIN_LOOT;
    }

    private static Vec3 getFormationPosition(
            Vec3 center,
            Vec3 forward,
            Vec3 right,
            int index,
            boolean firstShipIsCaptain
    ) {
        if (index == 0 && firstShipIsCaptain) {
            return center;
        }

        int formationIndex = firstShipIsCaptain ? index - 1 : index;

        int row = formationIndex / 2;
        boolean leftSide = formationIndex % 2 == 0;

        double sideDirection = leftSide ? 1.0D : -1.0D;
        double sideOffset = sideDirection * (8.0D + row * 7.0D);
        double backOffset = 8.0D + row * 8.0D;

        return center
                .subtract(forward.scale(backOffset))
                .add(right.scale(sideOffset));
    }

    public static ModBoatEntity spawnCaptainShip(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            UUID fleetId,
            int chestCount
    ) {
        PirateShipSize shipSize = PirateShipSize.LARGE;
        chestCount = clampChestCount(shipSize, chestCount);

        ModBoatEntity boat = spawnSailboat(
                level,
                position,
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

        PirateCaptainEntity captain = spawnCaptain(level, position.add(0.0D, 0.25D, 0.0D));

        if (captain != null) {
            setupPirate(captain, boat, fleetId, CAPTAIN_TAG);

            if (addPirateToBoat(boat, captain)) {
                PirateBoatPassengerHelper.assignHomeBoat(captain, boat);
                filledSeats++;
            }
        }

        while (filledSeats < seats) {
            Mob crew;
            String roleTag;

            if (filledSeats % 2 == 0) {
                crew = spawnGunner(level, position.add(0.0D, 0.25D, 0.0D));
                roleTag = RANGED_TAG;
            } else {
                crew = spawnDeckhand(level, position.add(0.0D, 0.25D, 0.0D));
                roleTag = PirateBoatPassengerHelper.BOARDER_TAG;
            }

            if (crew == null) {
                break;
            }

            setupPirate(crew, boat, fleetId, roleTag);

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

    public static ModBoatEntity spawnCombatShip(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            PirateShipSize shipSize,
            UUID fleetId
    ) {
        int chestCount = 0;

        ModBoatEntity boat = spawnSailboat(
                level,
                position,
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

        Mob pilot = spawnMarauder(level, position.add(0.0D, 0.25D, 0.0D));

        if (pilot == null) {
            pilot = spawnDeckhand(level, position.add(0.0D, 0.25D, 0.0D));
        }

        if (pilot != null) {
            setupPirate(pilot, boat, fleetId, PirateBoatPassengerHelper.BOARDER_TAG);

            if (addPirateToBoat(boat, pilot)) {
                PirateBoatPassengerHelper.assignHomeBoat(pilot, boat);
                filledSeats++;
            }
        }

        while (filledSeats < seats) {
            Mob crew;
            String roleTag;

            if (filledSeats % 2 == 0) {
                crew = spawnGunner(level, position.add(0.0D, 0.25D, 0.0D));
                roleTag = RANGED_TAG;
            } else {
                crew = spawnDeckhand(level, position.add(0.0D, 0.25D, 0.0D));
                roleTag = PirateBoatPassengerHelper.BOARDER_TAG;
            }

            if (crew == null) {
                break;
            }

            setupPirate(crew, boat, fleetId, roleTag);

            if (addPirateToBoat(boat, crew)) {
                PirateBoatPassengerHelper.assignHomeBoat(crew, boat);
                filledSeats++;
            } else {
                break;
            }
        }

        return boat;
    }

    public static ModBoatEntity spawnLootShip(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            PirateShipSize shipSize,
            int chestCount,
            UUID fleetId
    ) {
        chestCount = clampChestCount(shipSize, chestCount);

        ModBoatEntity boat = spawnSailboat(
                level,
                position,
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
                crew = spawnDeckhand(level, position.add(0.0D, 0.25D, 0.0D));
                roleTag = PirateBoatPassengerHelper.BOARDER_TAG;
            } else {
                crew = spawnGunner(level, position.add(0.0D, 0.25D, 0.0D));
                roleTag = RANGED_TAG;
            }

            if (crew == null) {
                break;
            }

            setupPirate(crew, boat, fleetId, roleTag);

            if (addPirateToBoat(boat, crew)) {
                PirateBoatPassengerHelper.assignHomeBoat(crew, boat);
                filledSeats++;
            } else {
                break;
            }
        }

        return boat;
    }

    public static ModBoatEntity spawnSailboat(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            PirateShipSize shipSize,
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

        setupBoat(boat, fleetId);

        if (captainShip) {
            boat.addTag(PirateBoatPassengerHelper.CAPTAIN_SHIP_TAG);
        }

        level.addFreshEntity(boat);

        return boat;
    }

    private static ModBoatEntity createBoatForSize(ServerLevel level, PirateShipSize shipSize) {
        return switch (shipSize) {
            case SMALL -> ModEntities.MOD_BOAT.get().create(level);
            case MEDIUM -> ModEntities.MEDIUM_MOD_BOAT.get().create(level);
            case LARGE -> ModEntities.LARGE_MOD_BOAT.get().create(level);
        };
    }

    private static void setupBoat(ModBoatEntity boat, UUID fleetId) {
        boat.addTag(PirateBoatPassengerHelper.RAID_PIRATE_TAG);
        boat.addTag(RAID_BOAT_TAG);
        boat.addTag(PirateBoatPassengerHelper.FLEET_TAG_PREFIX + fleetId);
    }

    private static void setupPirate(Mob pirate, ModBoatEntity boat, UUID fleetId, String roleTag) {
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

    private static Vec3 findBestWaveBoatSpawnCenter(
            ServerLevel level,
            ServerPlayer player,
            BlockPos treasurePos,
            int currentWave
    ) {
        Vec3 treasureCenter = Vec3.atBottomCenterOf(treasurePos);

        Vec3 awayFromPlayer = treasureCenter.subtract(player.position());
        awayFromPlayer = new Vec3(awayFromPlayer.x, 0.0D, awayFromPlayer.z);

        if (awayFromPlayer.lengthSqr() < 0.001D) {
            awayFromPlayer = new Vec3(1.0D, 0.0D, 0.0D);
        }

        awayFromPlayer = awayFromPlayer.normalize();

        double baseDistance = 28.0D + currentWave * 5.0D;

        for (double distance : new double[]{
                baseDistance,
                baseDistance + 10.0D,
                baseDistance - 8.0D,
                baseDistance + 18.0D
        }) {
            Vec3 testCenter = treasureCenter.add(awayFromPlayer.scale(distance));

            Vec3 waterCenter = snapToSurfaceWater(level, testCenter, BOAT_WATER_SEARCH_RADIUS);

            if (waterCenter != null) {
                return waterCenter;
            }
        }

        Vec3 fallbackWater = snapToSurfaceWater(level, treasureCenter, 42);

        if (fallbackWater != null) {
            return fallbackWater;
        }

        return treasureCenter.add(awayFromPlayer.scale(baseDistance));
    }

    private static Vec3 snapToSurfaceWater(ServerLevel level, Vec3 position, int radius) {
        if (level == null || position == null) {
            return null;
        }

        int centerX = (int) Math.floor(position.x);
        int centerY = (int) Math.floor(position.y);
        int centerZ = (int) Math.floor(position.z);

        BlockPos exactSurface = findNearestWaterSurface(
                level,
                new BlockPos(centerX, centerY, centerZ),
                0,
                12
        );

        if (exactSurface != null) {
            return surfaceBlockToBoatPosition(exactSurface);
        }

        BlockPos nearbySurface = findNearestWaterSurface(
                level,
                new BlockPos(centerX, centerY, centerZ),
                radius,
                12
        );

        if (nearbySurface != null) {
            return surfaceBlockToBoatPosition(nearbySurface);
        }

        return null;
    }

    private static BlockPos findNearestWaterSurface(ServerLevel level, BlockPos center, int horizontalRange, int verticalRange) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int z = -horizontalRange; z <= horizontalRange; z++) {
                for (int y = verticalRange; y >= -verticalRange; y--) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);

                    if (!level.getFluidState(mutable).is(FluidTags.WATER)) {
                        continue;
                    }

                    if (!level.getBlockState(mutable.above()).isAir()) {
                        continue;
                    }

                    if (!level.getBlockState(mutable.above(2)).isAir()) {
                        continue;
                    }

                    double distance = mutable.distSqr(center);

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestPos = mutable.immutable();
                    }
                }
            }
        }

        return bestPos;
    }

    private static Vec3 surfaceBlockToBoatPosition(BlockPos waterSurfaceBlock) {
        return new Vec3(
                waterSurfaceBlock.getX() + 0.5D,
                waterSurfaceBlock.getY() + BOAT_SURFACE_Y_OFFSET,
                waterSurfaceBlock.getZ() + 0.5D
        );
    }

    private static Vec3 getForwardDirection(Vec3 center, ServerPlayer player) {
        Vec3 towardPlayer = player.position().subtract(center);
        towardPlayer = new Vec3(towardPlayer.x, 0.0D, towardPlayer.z);

        if (towardPlayer.lengthSqr() < 0.001D) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }

        return towardPlayer.normalize();
    }

    private static void faceBoatTowardForward(ModBoatEntity boat, Vec3 forward) {
        if (boat == null || forward == null) {
            return;
        }

        float yaw = directionToYaw(forward);

        boat.setYRot(yaw);
        boat.yRotO = yaw;
    }

    private static float directionToYaw(Vec3 direction) {
        return (float) (Math.atan2(direction.z, direction.x) * 180.0D / Math.PI) - 90.0F;
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

    private static Boat.Type getRandomBoatType(ServerLevel level) {
        Boat.Type[] values = Boat.Type.values();
        return values[level.random.nextInt(values.length)];
    }

    private static int getMaxChestCountForSize(PirateShipSize shipSize) {
        return switch (shipSize) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    private static int clampChestCount(PirateShipSize shipSize, int chestCount) {
        return Math.max(0, Math.min(chestCount, getMaxChestCountForSize(shipSize)));
    }

    private static int getSeatCountForSize(PirateShipSize shipSize, int chestCount) {
        chestCount = clampChestCount(shipSize, chestCount);

        int baseSeats = switch (shipSize) {
            case SMALL -> 2;
            case MEDIUM -> 3;
            case LARGE -> 4;
        };

        return Math.max(1, baseSeats - chestCount);
    }

    private static int getBannerCountForSize(PirateShipSize shipSize) {
        return switch (shipSize) {
            case SMALL -> 1;
            case MEDIUM -> 1;
            case LARGE -> 2;
        };
    }
}