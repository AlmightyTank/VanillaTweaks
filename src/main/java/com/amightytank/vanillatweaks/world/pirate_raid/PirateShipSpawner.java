package com.amightytank.vanillatweaks.world.pirate_raid;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PirateShipSpawner {
    private static final int MIN_SPAWN_RADIUS = 18;
    private static final int MAX_SPAWN_RADIUS = 34;

    public static List<Mob> spawnWave(
            ServerLevel level,
            ServerPlayer target,
            BlockPos treasurePos,
            int wave,
            UUID raidId,
            List<PirateShipSpawnEntry> wavePlan
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        if (wavePlan.isEmpty()) {
            target.displayClientMessage(
                    Component.literal("Pirate raid wave " + wave + " has no ships in its wave plan!"),
                    false
            );
            return spawnedPirates;
        }

        target.displayClientMessage(
                Component.literal("Pirate raid wave " + wave + " spawning " + wavePlan.size() + " ships."),
                false
        );

        for (int i = 0; i < wavePlan.size(); i++) {
            PirateShipSpawnEntry entry = wavePlan.get(i);

            BlockPos formationPos = getVFormationPosition(
                    level,
                    target.blockPosition(),
                    wavePlan.size(),
                    i
            );

            spawnedPirates.addAll(spawnShipEntryAtPosition(
                    level,
                    target,
                    formationPos,
                    raidId,
                    entry
            ));
        }

        target.displayClientMessage(
                Component.literal("Pirate raid wave " + wave + " spawned " + spawnedPirates.size() + " pirates."),
                false
        );

        return spawnedPirates;
    }

    private static List<Mob> spawnShipEntryAtPosition(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID raidId,
            PirateShipSpawnEntry entry
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        if (spawnPos == null) {
            target.displayClientMessage(
                    Component.literal("Could not find V formation surface water for " + entry.size() + " " + entry.role() + " ship."),
                    false
            );
            return spawnedPirates;
        }

        announceDebugShip(target, entry);

        Entity boat = spawnRaidBoat(level, spawnPos, entry);

        if (entry.role() == PirateShipRole.CAPTAIN_LOOT) {
            spawnedPirates.addAll(spawnCaptainLootShipPirates(level, target, spawnPos, raidId));
        } else if (entry.role() == PirateShipRole.CAPTAIN) {
            spawnedPirates.addAll(spawnCaptainShipPirates(level, target, spawnPos, raidId));
        } else if (entry.role() == PirateShipRole.LOOT) {
            spawnedPirates.addAll(spawnLootShipPirates(level, target, spawnPos, raidId, entry.size()));
        } else {
            spawnedPirates.addAll(spawnCombatShipPirates(level, target, spawnPos, raidId, entry.size()));
        }

        if (boat != null) {
            setupRaidBoat(boat, target, raidId);
            mountCrewToBoat(boat, spawnedPirates);
        }

        return spawnedPirates;
    }

    private static BlockPos getVFormationPosition(
            ServerLevel level,
            BlockPos targetPos,
            int shipCount,
            int shipIndex
    ) {
        double attackAngle = Math.toRadians(level.random.nextInt(360));

        Vec3 forwardToPlayer = new Vec3(
                Math.cos(attackAngle),
                0.0D,
                Math.sin(attackAngle)
        ).normalize();

        Vec3 right = new Vec3(
                -forwardToPlayer.z,
                0.0D,
                forwardToPlayer.x
        ).normalize();

        int row = getVRow(shipIndex);
        int side = getVSide(shipIndex);

        double baseDistance = 34.0D;
        double rowDepth = 9.0D;
        double sideSpacing = 9.0D;

        Vec3 targetCenter = Vec3.atBottomCenterOf(targetPos);

        Vec3 spawn = targetCenter
                .subtract(forwardToPlayer.scale(baseDistance + row * rowDepth))
                .add(right.scale(side * sideSpacing * Math.max(1, row)));

        BlockPos wantedPos = BlockPos.containing(spawn);

        BlockPos surface = findNearbySurfaceWater(level, wantedPos, 8);

        return surface != null ? surface : wantedPos;
    }

    private static int getVRow(int index) {
        return switch (index) {
            case 0 -> 0;
            case 1, 2 -> 1;
            default -> 2;
        };
    }

    private static int getVSide(int index) {
        return switch (index) {
            case 0 -> 0;
            case 1 -> -1;
            case 2 -> 1;
            case 3 -> -2;
            case 4 -> 2;
            default -> 0;
        };
    }

    private static BlockPos findNearbySurfaceWater(ServerLevel level, BlockPos center, int radius) {
        for (int r = 0; r <= radius; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (Math.abs(x) != r && Math.abs(z) != r) {
                        continue;
                    }

                    BlockPos surface = findWaterSurfaceAt(level, center.getX() + x, center.getZ() + z);

                    if (surface != null) {
                        return surface;
                    }
                }
            }
        }

        return null;
    }

    private static Entity spawnRaidBoat(ServerLevel level, BlockPos spawnPos, PirateShipSpawnEntry entry) {
        boolean lootBoat = entry.role() == PirateShipRole.LOOT
                || entry.role() == PirateShipRole.CAPTAIN_LOOT;

        PirateShipSize finalSize = entry.role() == PirateShipRole.CAPTAIN_LOOT
                ? PirateShipSize.LARGE
                : entry.size();

        int chestCount = lootBoat ? getChestCountForSize(finalSize) : 0;

        return spawnSailboat(level, spawnPos, finalSize, chestCount);
    }

    private static Entity spawnSailboat(
            ServerLevel level,
            BlockPos spawnPos,
            PirateShipSize size,
            int chestCount
    ) {
        ModBoatEntity boat = createBoatForSize(level, size);

        if (boat == null) {
            return null;
        }

        Boat.Type woodType = getRandomBoatType(level);

        boat.setModVariant(woodType);

        /*
         * Keep this as your size flag:
         * small = 1, medium = 2, large = 3.
         * The renderer decides that large boats draw the same banner twice.
         */
        boat.setBannerCount(getBannerCountForSize(size));
        boat.setChestCount(chestCount);
        boat.setBannerStack(Raid.getLeaderBannerInstance());

        boat.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F,
                0.0F
        );

        level.addFreshEntity(boat);

        return boat;
    }

    private static ModBoatEntity createBoatForSize(ServerLevel level, PirateShipSize size) {
        return switch (size) {
            case SMALL -> ModEntities.MOD_BOAT.get().create(level);
            case MEDIUM -> ModEntities.MEDIUM_MOD_BOAT.get().create(level);
            case LARGE -> ModEntities.LARGE_MOD_BOAT.get().create(level);
        };
    }

    private static int getBannerCountForSize(PirateShipSize size) {
        return switch (size) {
            case SMALL, MEDIUM -> 1;
            case LARGE -> 3;
        };
    }

    private static int getChestCountForSize(PirateShipSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    private static Boat.Type getRandomBoatType(ServerLevel level) {
        Boat.Type[] types = Boat.Type.values();
        return types[level.random.nextInt(types.length)];
    }

    private static void setupRaidBoat(Entity boat, ServerPlayer target, UUID raidId) {
        boat.addTag("PirateTreasureRaidBoat");
        boat.addTag("PirateTreasureRaid_" + raidId);

        Vec3 lookDirection = target.position().subtract(boat.position());

        if (lookDirection.lengthSqr() > 0.01D) {
            float yaw = (float) (Math.atan2(lookDirection.z, lookDirection.x) * (180.0D / Math.PI)) - 90.0F;
            boat.setYRot(yaw);
        }
    }

    private static void mountCrewToBoat(Entity boat, List<Mob> crew) {
        for (Mob pirate : crew) {
            if (pirate == null || !pirate.isAlive()) {
                continue;
            }

            pirate.getPersistentData().putUUID("PirateRaidBoatUUID", boat.getUUID());
            pirate.startRiding(boat, true);
        }
    }

    private static List<Mob> spawnCombatShipPirates(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID raidId,
            PirateShipSize size
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        spawnRandomPirates(
                spawnedPirates,
                level,
                target,
                spawnPos,
                raidId,
                getCombatCrewCount(size)
        );

        return spawnedPirates;
    }

    private static List<Mob> spawnLootShipPirates(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID raidId,
            PirateShipSize size
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        spawnRandomPirates(
                spawnedPirates,
                level,
                target,
                spawnPos,
                raidId,
                getLootCrewCount(size)
        );

        return spawnedPirates;
    }

    private static List<Mob> spawnCaptainLootShipPirates(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID raidId
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        addIfNotNull(
                spawnedPirates,
                spawnPirateCaptain(level, target, spawnPos, raidId)
        );

        spawnRandomPirates(
                spawnedPirates,
                level,
                target,
                spawnPos.offset(1, 0, 0),
                raidId,
                2
        );

        return spawnedPirates;
    }

    private static List<Mob> spawnCaptainShipPirates(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID raidId
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        addIfNotNull(
                spawnedPirates,
                spawnPirateCaptain(level, target, spawnPos, raidId)
        );

        spawnRandomPirates(
                spawnedPirates,
                level,
                target,
                spawnPos.offset(1, 0, 0),
                raidId,
                3
        );

        return spawnedPirates;
    }

    private static int getCombatCrewCount(PirateShipSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 4;
        };
    }

    private static int getLootCrewCount(PirateShipSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    private static void spawnRandomPirates(
            List<Mob> spawnedPirates,
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID raidId,
            int count
    ) {
        for (int i = 0; i < count; i++) {
            BlockPos offsetPos = spawnPos.offset(getCrewOffsetX(i), 0, getCrewOffsetZ(i));

            addIfNotNull(
                    spawnedPirates,
                    spawnRandomPirate(level, target, offsetPos, raidId)
            );
        }
    }

    private static Mob spawnRandomPirate(ServerLevel level, ServerPlayer target, BlockPos pos, UUID raidId) {
        int roll = level.random.nextInt(100);

        if (roll < 50) {
            return spawnPirateDeckhand(level, target, pos, raidId);
        }

        if (roll < 80) {
            return spawnPirateGunner(level, target, pos, raidId);
        }

        return spawnPirateBrute(level, target, pos, raidId);
    }

    private static int getCrewOffsetX(int index) {
        return switch (index) {
            case 1 -> 1;
            case 2 -> -1;
            default -> 0;
        };
    }

    private static int getCrewOffsetZ(int index) {
        return switch (index) {
            case 3 -> 1;
            case 4 -> -1;
            default -> 0;
        };
    }

    private static BlockPos findSurfaceWaterSpawnPos(
            ServerLevel level,
            BlockPos center,
            int angleOffset,
            int shipIndex
    ) {
        for (int attempt = 0; attempt < 12; attempt++) {
            int radius = MIN_SPAWN_RADIUS + level.random.nextInt(MAX_SPAWN_RADIUS - MIN_SPAWN_RADIUS + 1);

            double angle = Math.toRadians(
                    angleOffset
                            + shipIndex * 8
                            + level.random.nextInt(18)
                            - 9
                            + attempt * 11
            );

            int x = center.getX() + (int) (Math.cos(angle) * radius);
            int z = center.getZ() + (int) (Math.sin(angle) * radius);

            BlockPos surfacePos = findWaterSurfaceAt(level, x, z);

            if (surfacePos != null) {
                return surfacePos;
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
                return mutable.immutable().above();
            }
        }

        return null;
    }

    private static Mob spawnPirateCaptain(ServerLevel level, ServerPlayer target, BlockPos pos, UUID raidId) {
        Mob pirate = ModEntities.PIRATE_CAPTAIN.get().spawn(
                level,
                pos,
                MobSpawnType.EVENT
        );

        setupPirate(pirate, target, pos, raidId);

        if (pirate != null) {
            pirate.addTag("PirateRaidRanged");
        }

        return pirate;
    }

    private static Mob spawnPirateDeckhand(ServerLevel level, ServerPlayer target, BlockPos pos, UUID raidId) {
        Mob pirate = ModEntities.PIRATE_DECKHAND.get().spawn(
                level,
                pos,
                MobSpawnType.EVENT
        );

        setupPirate(pirate, target, pos, raidId);

        if (pirate != null) {
            pirate.addTag("PirateRaidBoarder");
        }

        return pirate;
    }

    private static Mob spawnPirateGunner(ServerLevel level, ServerPlayer target, BlockPos pos, UUID raidId) {
        Mob pirate = ModEntities.PIRATE_GUNNER.get().spawn(
                level,
                pos,
                MobSpawnType.EVENT
        );

        setupPirate(pirate, target, pos, raidId);

        if (pirate != null) {
            pirate.addTag("PirateRaidRanged");
        }

        return pirate;
    }

    private static Mob spawnPirateBrute(ServerLevel level, ServerPlayer target, BlockPos pos, UUID raidId) {
        Mob pirate = ModEntities.PIRATE_MARAUDER.get().spawn(
                level,
                pos,
                MobSpawnType.EVENT
        );

        setupPirate(pirate, target, pos, raidId);

        if (pirate != null) {
            pirate.addTag("PirateRaidBoarder");
        }

        return pirate;
    }

    private static void setupPirate(Mob pirate, ServerPlayer target, BlockPos pos, UUID raidId) {
        if (pirate == null) {
            return;
        }

        pirate.moveTo(Vec3.atBottomCenterOf(pos));
        pirate.setTarget(target);

        pirate.addTag("PirateTreasureRaid");
        pirate.addTag("PirateTreasureRaid_" + raidId);
    }

    private static void addIfNotNull(List<Mob> list, Mob mob) {
        if (mob != null) {
            list.add(mob);
        }
    }

    private static void announceDebugShip(ServerPlayer target, PirateShipSpawnEntry entry) {
        String shipName = switch (entry.role()) {
            case COMBAT -> entry.size().name().toLowerCase() + " combat sailboat";
            case LOOT -> entry.size().name().toLowerCase() + " loot sailboat";
            case CAPTAIN -> "captain ship";
            case CAPTAIN_LOOT -> "captain loot ship";
        };

        target.displayClientMessage(
                Component.literal("Spawning " + shipName),
                false
        );
    }
}