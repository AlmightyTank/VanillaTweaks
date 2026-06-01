package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.ai.PirateBoatBoarderRemountGoal;
import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.ai.util.PirateRaidAiUtil;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.util.PirateLootHelper;
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

public final class PiratePatrolFormation {
    private static final String PATROL_TAG = "PiratePatrol";
    private static final String PATROL_BOAT_TAG = "PiratePatrolBoat";

    private PiratePatrolFormation() {
    }

    public static List<Mob> spawnPatrol(ServerLevel level, ServerPlayer target) {
        UUID patrolId = UUID.randomUUID();
        List<PatrolShipEntry> plan = createRandomPatrolPlan(level);
        List<Mob> spawnedPirates = new ArrayList<>();

        if (plan.isEmpty()) {
            return spawnedPirates;
        }

        double attackAngle = Math.toRadians(level.random.nextInt(360));
        Boat.Type patrolWoodType = getRandomBoatType(level);

        for (int i = 0; i < plan.size(); i++) {
            PatrolShipEntry entry = plan.get(i);

            BlockPos formationPos = getVFormationPosition(
                    level,
                    target.blockPosition(),
                    plan.size(),
                    i,
                    attackAngle
            );

            if (formationPos == null) {
                continue;
            }

            spawnedPirates.addAll(spawnShipEntryAtPosition(
                    level,
                    target,
                    formationPos,
                    patrolId,
                    entry,
                    patrolWoodType
            ));
        }

        if (!spawnedPirates.isEmpty()) {
            target.displayClientMessage(
                    Component.literal("A pirate patrol has spotted you!"),
                    false
            );
        }

        return spawnedPirates;
    }

    private static List<PatrolShipEntry> createRandomPatrolPlan(ServerLevel level) {
        int roll = level.random.nextInt(100);
        List<PatrolShipEntry> plan = new ArrayList<>();

        if (roll < 35) {
            /*
             * Small patrol:
             * 1 large captain loot ship + 2 small combat boats.
             */
            plan.add(new PatrolShipEntry(PatrolShipSize.LARGE, PatrolShipRole.CAPTAIN_LOOT));
            plan.add(new PatrolShipEntry(PatrolShipSize.SMALL, PatrolShipRole.COMBAT));
            plan.add(new PatrolShipEntry(PatrolShipSize.SMALL, PatrolShipRole.COMBAT));
            return plan;
        }

        if (roll < 75) {
            /*
             * Medium patrol:
             * 1 large captain ship + 2 medium combat boats + 1 small loot boat.
             */
            plan.add(new PatrolShipEntry(PatrolShipSize.LARGE, PatrolShipRole.CAPTAIN));
            plan.add(new PatrolShipEntry(PatrolShipSize.MEDIUM, PatrolShipRole.COMBAT));
            plan.add(new PatrolShipEntry(PatrolShipSize.MEDIUM, PatrolShipRole.COMBAT));
            plan.add(new PatrolShipEntry(PatrolShipSize.SMALL, PatrolShipRole.LOOT));
            return plan;
        }

        /*
         * Large patrol:
         * 1 large captain ship + 3 large combat boats + 2 medium loot boats.
         */
        plan.add(new PatrolShipEntry(PatrolShipSize.LARGE, PatrolShipRole.CAPTAIN));
        plan.add(new PatrolShipEntry(PatrolShipSize.LARGE, PatrolShipRole.COMBAT));
        plan.add(new PatrolShipEntry(PatrolShipSize.LARGE, PatrolShipRole.COMBAT));
        plan.add(new PatrolShipEntry(PatrolShipSize.LARGE, PatrolShipRole.COMBAT));
        plan.add(new PatrolShipEntry(PatrolShipSize.MEDIUM, PatrolShipRole.LOOT));
        plan.add(new PatrolShipEntry(PatrolShipSize.MEDIUM, PatrolShipRole.LOOT));

        return plan;
    }

    private static List<Mob> spawnShipEntryAtPosition(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID patrolId,
            PatrolShipEntry entry,
            Boat.Type woodType
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        Entity boat = spawnPatrolBoat(level, spawnPos, entry, woodType);

        if (boat == null) {
            return spawnedPirates;
        }

        setupPatrolBoat(boat, target, patrolId);

        if (boat instanceof ModBoatEntity modBoat && isLootShip(entry)) {
            fillLootShip(level, modBoat);
        }

        switch (entry.role()) {
            case CAPTAIN_LOOT -> spawnedPirates.addAll(spawnCaptainLootShipPirates(level, target, spawnPos, patrolId));
            case CAPTAIN -> spawnedPirates.addAll(spawnCaptainShipPirates(level, target, spawnPos, patrolId));
            case LOOT -> spawnedPirates.addAll(spawnLootShipPirates(level, target, spawnPos, patrolId, entry.size()));
            case COMBAT -> spawnedPirates.addAll(spawnCombatShipPirates(level, target, spawnPos, patrolId, entry.size()));
        }

        mountCrewToBoat(boat, spawnedPirates);

        return spawnedPirates;
    }

    private static boolean isLootShip(PatrolShipEntry entry) {
        return entry.role() == PatrolShipRole.LOOT
                || entry.role() == PatrolShipRole.CAPTAIN_LOOT;
    }

    private static void fillLootShip(ServerLevel level, ModBoatEntity boat) {
        PirateLootHelper.fillPirateLootShip(level, boat);
    }

    private static BlockPos getVFormationPosition(
            ServerLevel level,
            BlockPos targetPos,
            int shipCount,
            int shipIndex,
            double attackAngle
    ) {
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

        double baseDistance = 48.0D;
        double rowDepth = 11.0D;
        double sideSpacing = 11.0D;

        Vec3 targetCenter = Vec3.atBottomCenterOf(targetPos);

        Vec3 spawn = targetCenter
                .subtract(forwardToPlayer.scale(baseDistance + row * rowDepth))
                .add(right.scale(side * sideSpacing * Math.max(1, row)));

        BlockPos wantedPos = BlockPos.containing(spawn);

        return findNearbySurfaceWater(level, wantedPos, 18);
    }

    private static int getVRow(int index) {
        return switch (index) {
            case 0 -> 0;
            case 1, 2 -> 1;
            case 3, 4 -> 2;
            default -> 3;
        };
    }

    private static int getVSide(int index) {
        return switch (index) {
            case 0 -> 0;
            case 1 -> -1;
            case 2 -> 1;
            case 3 -> -2;
            case 4 -> 2;
            case 5 -> 0;
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

    private static Entity spawnPatrolBoat(
            ServerLevel level,
            BlockPos spawnPos,
            PatrolShipEntry entry,
            Boat.Type woodType
    ) {
        boolean lootBoat = isLootShip(entry);
        int chestCount = lootBoat ? 1 : 0;

        ModBoatEntity boat = createBoatForSize(level, entry.size());

        if (boat == null) {
            return null;
        }

        boat.setModVariant(woodType);
        boat.setBoatSizeTier(getBoatSizeTier(entry.size()));
        boat.setChestCount(chestCount);
        boat.setBannerStack(Raid.getLeaderBannerInstance());

        if (boat.isLargeSailboat()) {
            boat.setSecondBannerStack(Raid.getLeaderBannerInstance());
        }

        boat.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F,
                0.0F
        );

        /*
         * Add the boat before any crew mounts it.
         * Crew boarding is queued in mountCrewToBoat(...).
         */
        level.addFreshEntity(boat);

        return boat;
    }

    private static ModBoatEntity createBoatForSize(ServerLevel level, PatrolShipSize size) {
        return switch (size) {
            case SMALL -> ModEntities.MOD_BOAT.get().create(level);
            case MEDIUM -> ModEntities.MEDIUM_MOD_BOAT.get().create(level);
            case LARGE -> ModEntities.LARGE_MOD_BOAT.get().create(level);
        };
    }

    private static int getBoatSizeTier(PatrolShipSize size) {
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

    private static void setupPatrolBoat(Entity boat, ServerPlayer target, UUID patrolId) {
        /*
         * Important:
         * This MUST use PirateRaidAiUtil.RAID_BOAT_TAG.
         * The pilot/remount goals look for this tag.
         */
        boat.addTag(PirateRaidAiUtil.RAID_BOAT_TAG);
        boat.addTag(PATROL_BOAT_TAG);
        boat.addTag(getPatrolGroupTag(patrolId));
        boat.getPersistentData().putUUID(PirateRaidAiUtil.TARGET_UUID_TAG, target.getUUID());

        Vec3 lookDirection = target.position().subtract(boat.position());

        if (lookDirection.lengthSqr() > 0.01D) {
            float yaw = (float) (Math.atan2(lookDirection.z, lookDirection.x) * (180.0D / Math.PI)) - 90.0F;
            boat.setYRot(yaw);
            boat.setYHeadRot(yaw);
        }
    }

    private static void mountCrewToBoat(Entity boatEntity, List<Mob> crew) {
        if (!(boatEntity instanceof Boat boat)) {
            return;
        }

        /*
         * Count open seats once, then decrement as mounts are queued.
         * This respects ModBoatEntity's chest-based passenger limit.
         */
        int openSeats = PirateBoatPassengerHelper.getOpenSeatCount(boat);

        for (Mob pirate : crew) {
            if (openSeats <= 0) {
                break;
            }

            if (pirate == null || !pirate.isAlive()) {
                continue;
            }

            /*
             * Used by boat/pilot/raid AI.
             */
            pirate.getPersistentData().putUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG, boat.getUUID());

            /*
             * Used by remount AI.
             * The pirate will prefer this boat, but can choose another fleet boat
             * if this one has no unreserved open seat later.
             */
            pirate.getPersistentData().putUUID(PirateBoatBoarderRemountGoal.RETURN_BOAT_UUID_TAG, boat.getUUID());

            /*
             * Queue by 1 tick so the client knows the boat exists before receiving passengers.
             */
            if (PirateBoatPassengerHelper.queueBoard(pirate, boat, 1)) {
                openSeats--;
            }
        }
    }

    private static List<Mob> spawnCombatShipPirates(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID patrolId,
            PatrolShipSize size
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        spawnRandomPirates(
                spawnedPirates,
                level,
                target,
                spawnPos,
                patrolId,
                getCombatCrewCount(size)
        );

        return spawnedPirates;
    }

    private static List<Mob> spawnLootShipPirates(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID patrolId,
            PatrolShipSize size
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        spawnRandomPirates(
                spawnedPirates,
                level,
                target,
                spawnPos,
                patrolId,
                getLootCrewCount(size)
        );

        return spawnedPirates;
    }

    private static List<Mob> spawnCaptainLootShipPirates(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID patrolId
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        addIfNotNull(
                spawnedPirates,
                spawnPirateCaptain(level, target, spawnPos, patrolId)
        );

        spawnRandomPirates(
                spawnedPirates,
                level,
                target,
                spawnPos.offset(1, 0, 0),
                patrolId,
                2
        );

        return spawnedPirates;
    }

    private static List<Mob> spawnCaptainShipPirates(
            ServerLevel level,
            ServerPlayer target,
            BlockPos spawnPos,
            UUID patrolId
    ) {
        List<Mob> spawnedPirates = new ArrayList<>();

        addIfNotNull(
                spawnedPirates,
                spawnPirateCaptain(level, target, spawnPos, patrolId)
        );

        spawnRandomPirates(
                spawnedPirates,
                level,
                target,
                spawnPos.offset(1, 0, 0),
                patrolId,
                3
        );

        return spawnedPirates;
    }

    private static int getCombatCrewCount(PatrolShipSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 4;
        };
    }

    private static int getLootCrewCount(PatrolShipSize size) {
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
            UUID patrolId,
            int count
    ) {
        for (int i = 0; i < count; i++) {
            BlockPos offsetPos = spawnPos.offset(getCrewOffsetX(i), 0, getCrewOffsetZ(i));

            addIfNotNull(
                    spawnedPirates,
                    spawnRandomPirate(level, target, offsetPos, patrolId)
            );
        }
    }

    private static Mob spawnRandomPirate(ServerLevel level, ServerPlayer target, BlockPos pos, UUID patrolId) {
        int roll = level.random.nextInt(100);

        if (roll < 50) {
            return spawnPirateDeckhand(level, target, pos, patrolId);
        }

        if (roll < 80) {
            return spawnPirateGunner(level, target, pos, patrolId);
        }

        /*
         * This is the marauder.
         * It is a boarder hybrid, so it gets BOARDER_TAG later.
         */
        return spawnPirateMarauder(level, target, pos, patrolId);
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

    private static Mob spawnPirateCaptain(ServerLevel level, ServerPlayer target, BlockPos pos, UUID patrolId) {
        Mob pirate = ModEntities.PIRATE_CAPTAIN.get().spawn(
                level,
                pos,
                MobSpawnType.EVENT
        );

        Mob result = setupPirate(pirate, target, pos, patrolId, PirateRaidAiUtil.RANGED_TAG);

        if (result != null) {
            result.addTag(PirateRaidAiUtil.CAPTAIN_TAG);
        }

        return result;
    }

    private static Mob spawnPirateDeckhand(ServerLevel level, ServerPlayer target, BlockPos pos, UUID patrolId) {
        Mob pirate = ModEntities.PIRATE_DECKHAND.get().spawn(
                level,
                pos,
                MobSpawnType.EVENT
        );

        return setupPirate(pirate, target, pos, patrolId, PirateRaidAiUtil.BOARDER_TAG);
    }

    private static Mob spawnPirateGunner(ServerLevel level, ServerPlayer target, BlockPos pos, UUID patrolId) {
        Mob pirate = ModEntities.PIRATE_GUNNER.get().spawn(
                level,
                pos,
                MobSpawnType.EVENT
        );

        return setupPirate(pirate, target, pos, patrolId, PirateRaidAiUtil.RANGED_TAG);
    }

    private static Mob spawnPirateMarauder(ServerLevel level, ServerPlayer target, BlockPos pos, UUID patrolId) {
        Mob pirate = ModEntities.PIRATE_MARAUDER.get().spawn(
                level,
                pos,
                MobSpawnType.EVENT
        );

        /*
         * Marauder is a boarder hybrid.
         * Do NOT give it RANGED_TAG.
         */
        return setupPirate(pirate, target, pos, patrolId, PirateRaidAiUtil.BOARDER_TAG);
    }

    private static Mob setupPirate(
            Mob pirate,
            ServerPlayer target,
            BlockPos pos,
            UUID patrolId,
            String roleTag
    ) {
        if (pirate == null) {
            return null;
        }

        pirate.moveTo(Vec3.atBottomCenterOf(pos));
        pirate.setTarget(target);
        pirate.setPersistenceRequired();

        pirate.addTag(PirateRaidAiUtil.RAID_PIRATE_TAG);
        pirate.addTag(PATROL_TAG);
        pirate.addTag(getPatrolGroupTag(patrolId));
        pirate.addTag(roleTag);

        return pirate;
    }

    private static String getPatrolGroupTag(UUID patrolId) {
        return PirateRaidAiUtil.RAID_PIRATE_TAG + "_" + patrolId;
    }

    private static void addIfNotNull(List<Mob> list, Mob mob) {
        if (mob != null) {
            list.add(mob);
        }
    }

    private enum PatrolShipRole {
        CAPTAIN,
        CAPTAIN_LOOT,
        COMBAT,
        LOOT
    }

    private enum PatrolShipSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    private record PatrolShipEntry(PatrolShipSize size, PatrolShipRole role) {
    }
}