package com.amightytank.vanillatweaks.world.pirate_raid;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
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
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.amightytank.vanillatweaks.world.PiratePatrolFormation.getRandomBoatType;

public final class PirateShipSpawner {
    public static final String RAID_BOAT_TAG = "PirateRaidBoat";
    public static final String CAPTAIN_TAG = "PirateRaidCaptain";
    public static final String RANGED_TAG = "PirateRaidRanged";
    public static final String BOAT_UUID_TAG_PREFIX = "PirateRaidBoatUUID:";

    private PirateShipSpawner() {
    }

    public static List<Mob> spawnWave(
            ServerLevel level,
            ServerPlayer player,
            BlockPos treasurePos,
            int currentWave,
            Object raidId,
            Object wavePlan
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        UUID fleetId = toFleetUuid(raidId);
        Boat.Type woodType = getRandomBoatType(level);

        Vec3 center = findBestWaveBoatSpawnCenter(level, player, treasurePos, currentWave);
        Vec3 forward = getForwardDirection(center, player);
        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);

        int captainShips = readIntFromPlan(
                wavePlan,
                currentWave >= 3 ? 1 : 0,
                "captainShips",
                "captainShipCount",
                "captainCount",
                "captains"
        );

        int combatShips = readIntFromPlan(
                wavePlan,
                Math.min(1 + currentWave, 3),
                "combatShips",
                "combatShipCount",
                "boatCount",
                "ships"
        );

        int lootShips = readIntFromPlan(
                wavePlan,
                0,
                "lootShips",
                "lootShipCount",
                "chestShips",
                "chestShipCount"
        );

        ShipSize combatSize = readShipSizeFromPlan(
                wavePlan,
                currentWave >= 3 ? ShipSize.LARGE : currentWave == 2 ? ShipSize.MEDIUM : ShipSize.SMALL,
                "shipSize",
                "combatShipSize",
                "boatSize"
        );

        ShipSize lootSize = readShipSizeFromPlan(
                wavePlan,
                ShipSize.MEDIUM,
                "lootShipSize",
                "chestShipSize"
        );

        int index = 0;

        for (int i = 0; i < captainShips; i++) {
            Vec3 pos = center.add(right.scale((index - 1.0D) * 8.0D));
            ModBoatEntity boat = spawnCaptainShip(level, pos, woodType, fleetId, false);
            collectBoatPirates(boat, spawnedPirates);
            index++;
        }

        for (int i = 0; i < combatShips; i++) {
            double sideOffset = ((i % 2 == 0) ? 1.0D : -1.0D) * (8.0D + (i / 2) * 7.0D);
            double backOffset = 8.0D + (i / 2) * 8.0D;

            Vec3 pos = center.subtract(forward.scale(backOffset)).add(right.scale(sideOffset));

            ModBoatEntity boat = spawnCombatShip(level, pos, woodType, combatSize, fleetId);
            collectBoatPirates(boat, spawnedPirates);
            index++;
        }

        for (int i = 0; i < lootShips; i++) {
            double sideOffset = ((i % 2 == 0) ? 1.0D : -1.0D) * 6.0D;
            double backOffset = 18.0D + (i / 2) * 7.0D;

            Vec3 pos = center.subtract(forward.scale(backOffset)).add(right.scale(sideOffset));

            ModBoatEntity boat = spawnLootShip(level, pos, woodType, lootSize, fleetId);
            collectBoatPirates(boat, spawnedPirates);
        }

        return spawnedPirates;
    }

    private static void collectBoatPirates(Boat boat, List<Mob> spawnedPirates) {
        if (boat == null) {
            return;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Mob mob) {
                spawnedPirates.add(mob);
            }
        }
    }

    private static UUID toFleetUuid(Object raidId) {
        if (raidId instanceof UUID uuid) {
            return uuid;
        }

        if (raidId != null) {
            return UUID.nameUUIDFromBytes(String.valueOf(raidId).getBytes(StandardCharsets.UTF_8));
        }

        return UUID.randomUUID();
    }

    private static Vec3 findBestWaveBoatSpawnCenter(
            ServerLevel level,
            ServerPlayer player,
            BlockPos treasurePos,
            int currentWave
    ) {
        Vec3 treasureCenter = Vec3.atBottomCenterOf(treasurePos);

        Vec3 awayFromPlayer;

        if (player != null) {
            awayFromPlayer = treasureCenter.subtract(player.position());
            awayFromPlayer = new Vec3(awayFromPlayer.x, 0.0D, awayFromPlayer.z);
        } else {
            awayFromPlayer = new Vec3(1.0D, 0.0D, 0.0D);
        }

        if (awayFromPlayer.lengthSqr() < 0.001D) {
            awayFromPlayer = new Vec3(1.0D, 0.0D, 0.0D);
        }

        awayFromPlayer = awayFromPlayer.normalize();

        double baseDistance = 28.0D + currentWave * 5.0D;

        for (double distance : new double[]{baseDistance, baseDistance + 10.0D, baseDistance - 8.0D, baseDistance + 18.0D}) {
            Vec3 testCenter = treasureCenter.add(awayFromPlayer.scale(distance));

            BlockPos waterPos = findNearestWaterSurface(level, BlockPos.containing(testCenter), 14, 8);

            if (waterPos != null) {
                return Vec3.atBottomCenterOf(waterPos).add(0.0D, 0.12D, 0.0D);
            }
        }

        BlockPos fallbackWater = findNearestWaterSurface(level, treasurePos, 42, 10);

        if (fallbackWater != null) {
            return Vec3.atBottomCenterOf(fallbackWater).add(0.0D, 0.12D, 0.0D);
        }

        return treasureCenter.add(awayFromPlayer.scale(baseDistance));
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

                    BlockPos above = mutable.above();

                    if (!level.getBlockState(above).isAir() && !level.getFluidState(above).isEmpty()) {
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

    private static Vec3 getForwardDirection(Vec3 center, ServerPlayer player) {
        if (player == null) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }

        Vec3 towardPlayer = player.position().subtract(center);
        towardPlayer = new Vec3(towardPlayer.x, 0.0D, towardPlayer.z);

        if (towardPlayer.lengthSqr() < 0.001D) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }

        return towardPlayer.normalize();
    }

    private static int readIntFromPlan(Object wavePlan, int fallback, String... names) {
        if (wavePlan == null) {
            return fallback;
        }

        for (String name : names) {
            Integer value = readIntGetter(wavePlan, name);

            if (value != null) {
                return Math.max(0, value);
            }

            value = readIntField(wavePlan, name);

            if (value != null) {
                return Math.max(0, value);
            }
        }

        return fallback;
    }

    private static Integer readIntGetter(Object wavePlan, String name) {
        String capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);

        for (String methodName : new String[]{name, "get" + capitalized}) {
            try {
                Method method = wavePlan.getClass().getMethod(methodName);
                Object result = method.invoke(wavePlan);

                if (result instanceof Number number) {
                    return number.intValue();
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private static Integer readIntField(Object wavePlan, String name) {
        try {
            Field field = wavePlan.getClass().getDeclaredField(name);
            field.setAccessible(true);

            Object result = field.get(wavePlan);

            if (result instanceof Number number) {
                return number.intValue();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static ShipSize readShipSizeFromPlan(Object wavePlan, ShipSize fallback, String... names) {
        if (wavePlan == null) {
            return fallback;
        }

        for (String name : names) {
            Object value = readValueFromPlan(wavePlan, name);

            if (value instanceof ShipSize shipSize) {
                return shipSize;
            }

            if (value instanceof Enum<?> enumValue) {
                try {
                    return ShipSize.valueOf(enumValue.name());
                } catch (Exception ignored) {
                }
            }

            if (value instanceof String stringValue) {
                try {
                    return ShipSize.valueOf(stringValue.toUpperCase());
                } catch (Exception ignored) {
                }
            }
        }

        return fallback;
    }

    private static Object readValueFromPlan(Object wavePlan, String name) {
        String capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);

        for (String methodName : new String[]{name, "get" + capitalized}) {
            try {
                Method method = wavePlan.getClass().getMethod(methodName);
                return method.invoke(wavePlan);
            } catch (Exception ignored) {
            }
        }

        try {
            Field field = wavePlan.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(wavePlan);
        } catch (Exception ignored) {
        }

        return null;
    }

    public enum ShipSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    public static ModBoatEntity spawnCaptainShip(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            UUID fleetId,
            boolean hasChest
    ) {
        ModBoatEntity boat = spawnSailboat(level, position, woodType, ShipSize.LARGE, hasChest, true, fleetId);

        if (boat == null) {
            return null;
        }

        PirateCaptainEntity captain = spawnCaptain(level, position.add(0.0D, 0.25D, 0.0D));

        if (captain != null) {
            setupPirate(captain, boat, fleetId, CAPTAIN_TAG);
            addPirateToBoat(boat, captain);

            PirateBoatPassengerHelper.assignHomeBoat(captain, boat);
            boat.addTag(PirateBoatPassengerHelper.CAPTAIN_SHIP_TAG);
        }

        if (hasChest) {
            PirateLootHelper.fillPirateLootShip(level, boat);
        }

        return boat;
    }

    public static ModBoatEntity spawnCombatShip(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            ShipSize shipSize,
            UUID fleetId
    ) {
        ModBoatEntity boat = spawnSailboat(level, position, woodType, shipSize, false, false, fleetId);

        if (boat == null) {
            return null;
        }

        int seats = getSeatCountForSize(shipSize, false);

        /*
         * First passenger should be a melee pirate so this boat has a stable pilot.
         */
        Mob pilot = spawnMarauder(level, position.add(0.0D, 0.25D, 0.0D));

        if (pilot == null) {
            pilot = spawnDeckhand(level, position.add(0.0D, 0.25D, 0.0D));
        }

        if (pilot != null) {
            setupPirate(pilot, boat, fleetId, PirateBoatPassengerHelper.BOARDER_TAG);
            addPirateToBoat(boat, pilot);
            PirateBoatPassengerHelper.assignHomeBoat(pilot, boat);
        }

        for (int i = 1; i < seats; i++) {
            Mob crew;

            if (i % 2 == 0) {
                crew = spawnGunner(level, position.add(0.0D, 0.25D, 0.0D));
            } else {
                crew = spawnDeckhand(level, position.add(0.0D, 0.25D, 0.0D));
            }

            if (crew == null) {
                continue;
            }

            String roleTag = crew instanceof AbstractPirateEntity && crew.getClass().getSimpleName().toLowerCase().contains("gunner")
                    ? RANGED_TAG
                    : PirateBoatPassengerHelper.BOARDER_TAG;

            setupPirate(crew, boat, fleetId, roleTag);
            addPirateToBoat(boat, crew);
            PirateBoatPassengerHelper.assignHomeBoat(crew, boat);
        }

        return boat;
    }

    public static ModBoatEntity spawnLootShip(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            ShipSize shipSize,
            UUID fleetId
    ) {
        ModBoatEntity boat = spawnSailboat(level, position, woodType, shipSize, true, false, fleetId);

        if (boat == null) {
            return null;
        }

        boat.addTag("PirateLootShip");
        PirateLootHelper.fillPirateLootShip(level, boat);

        int seats = getSeatCountForSize(shipSize, true);

        /*
         * Loot ships still get a small crew, but they should not be preferred for captain rescue
         * unless every combat boat is unavailable.
         */
        for (int i = 0; i < seats; i++) {
            Mob crew = i == 0
                    ? spawnDeckhand(level, position.add(0.0D, 0.25D, 0.0D))
                    : spawnGunner(level, position.add(0.0D, 0.25D, 0.0D));

            if (crew == null) {
                continue;
            }

            String roleTag = i == 0 ? PirateBoatPassengerHelper.BOARDER_TAG : RANGED_TAG;

            setupPirate(crew, boat, fleetId, roleTag);
            addPirateToBoat(boat, crew);
            PirateBoatPassengerHelper.assignHomeBoat(crew, boat);
        }

        return boat;
    }

    public static ModBoatEntity spawnSailboat(
            ServerLevel level,
            Vec3 position,
            Boat.Type woodType,
            ShipSize shipSize,
            boolean hasChest,
            boolean captainShip,
            UUID fleetId
    ) {
        ModBoatEntity boat = createBoatForSize(level, shipSize);

        if (boat == null) {
            return null;
        }

        boat.moveTo(position.x, position.y, position.z, level.random.nextFloat() * 360.0F, 0.0F);

        boat.setModVariant(woodType);
        boat.setBannerStack(Raid.getLeaderBannerInstance());
        boat.setBannerCount(getBannerCountForSize(shipSize));

        if (hasChest) {
            boat.setChestCount(1);
        } else {
            boat.setChestCount(0);
        }

        setupBoat(boat, fleetId);

        if (captainShip) {
            boat.addTag(PirateBoatPassengerHelper.CAPTAIN_SHIP_TAG);
        }

        level.addFreshEntity(boat);

        return boat;
    }

    private static ModBoatEntity createBoatForSize(ServerLevel level, ShipSize shipSize) {
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

    private static void addPirateToBoat(ModBoatEntity boat, Mob pirate) {
        /*
         * Use your custom sailboat helper first so seat limits stay correct.
         * Fallback to vanilla riding if your boat accepts normal passengers.
         */
        if (!boat.addMobToSailboat(pirate)) {
            pirate.startRiding(boat, true);
        }
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

    private static int getBannerCountForSize(ShipSize shipSize) {
        return switch (shipSize) {
            case SMALL -> 1;
            case MEDIUM -> 1;
            case LARGE -> 2;
        };
    }

    private static int getSeatCountForSize(ShipSize shipSize, boolean hasChest) {
        return switch (shipSize) {
            case SMALL -> 1;
            case MEDIUM -> hasChest ? 1 : 2;
            case LARGE -> hasChest ? 2 : 3;
        };
    }
}