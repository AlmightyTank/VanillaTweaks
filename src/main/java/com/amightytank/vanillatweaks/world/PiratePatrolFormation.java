package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateDeckhandEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateGunnerEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateMarauderEntity;
import com.amightytank.vanillatweaks.util.PirateLootHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class PiratePatrolFormation {

    private enum SailboatSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    public static void spawn(ServerLevel level, BlockPos centerPos, ServerPlayer target, PiratePatrolSize size) {
        Boat.Type fleetWoodType = getRandomWoodType(level);
        UUID patrolId = UUID.randomUUID();

        switch (size) {
            case SMALL -> spawnSmallPatrol(level, centerPos, target, fleetWoodType, patrolId);
            case MEDIUM -> spawnMediumPatrol(level, centerPos, target, fleetWoodType, patrolId);
            case LARGE -> spawnLargePatrol(level, centerPos, target, fleetWoodType, patrolId);
        }
    }

    private static void spawnSmallPatrol(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            Boat.Type fleetWoodType,
            UUID patrolId
    ) {
        /*
         * Small patrol:
         * - 1 large captain loot ship
         * - 2 small combat sailboats
         */

        spawnCaptainShip(level, pos.offset(0, 0, 0), true, target, fleetWoodType, patrolId);

        spawnCombatShip(level, pos.offset(8, 0, 8), SailboatSize.SMALL, target, fleetWoodType, patrolId);
        spawnCombatShip(level, pos.offset(-8, 0, 8), SailboatSize.SMALL, target, fleetWoodType, patrolId);
    }

    private static void spawnMediumPatrol(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            Boat.Type fleetWoodType,
            UUID patrolId
    ) {
        /*
         * Medium patrol:
         * - 1 large captain ship
         * - 2 medium combat sailboats
         * - 1 small loot sailboat
         */

        spawnCaptainShip(level, pos.offset(0, 0, 0), false, target, fleetWoodType, patrolId);

        spawnCombatShip(level, pos.offset(9, 0, 9), SailboatSize.MEDIUM, target, fleetWoodType, patrolId);
        spawnCombatShip(level, pos.offset(-9, 0, 9), SailboatSize.MEDIUM, target, fleetWoodType, patrolId);

        spawnLootShip(level, pos.offset(0, 0, -10), SailboatSize.SMALL, target, fleetWoodType, patrolId);
    }

    private static void spawnLargePatrol(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            Boat.Type fleetWoodType,
            UUID patrolId
    ) {
        /*
         * Large patrol:
         * - 1 large captain ship
         * - 3 large combat sailboats
         * - 2 medium loot sailboats
         */

        spawnCaptainShip(level, pos.offset(0, 0, 0), false, target, fleetWoodType, patrolId);

        spawnCombatShip(level, pos.offset(10, 0, 10), SailboatSize.LARGE, target, fleetWoodType, patrolId);
        spawnCombatShip(level, pos.offset(-10, 0, 10), SailboatSize.LARGE, target, fleetWoodType, patrolId);
        spawnCombatShip(level, pos.offset(0, 0, 18), SailboatSize.LARGE, target, fleetWoodType, patrolId);

        spawnLootShip(level, pos.offset(9, 0, -10), SailboatSize.MEDIUM, target, fleetWoodType, patrolId);
        spawnLootShip(level, pos.offset(-9, 0, -10), SailboatSize.MEDIUM, target, fleetWoodType, patrolId);
    }

    private static void spawnCaptainShip(
            ServerLevel level,
            BlockPos pos,
            boolean lootShip,
            ServerPlayer target,
            Boat.Type fleetWoodType,
            UUID patrolId
    ) {
        SailboatSize size = SailboatSize.LARGE;

        /*
         * Keep loot ships at 1 chest.
         * Your ModBoatEntity subtracts chest count from passenger slots.
         * Large boat = 4 base passengers.
         * 1 chest leaves 3 seats: captain + 2 guards.
         */
        int chestCount = lootShip ? 1 : 0;

        ModBoatEntity boat = createBoat(level, pos, size, fleetWoodType, chestCount, target, patrolId);

        if (boat == null) {
            return;
        }

        level.addFreshEntity(boat);

        if (lootShip) {
            fillLootShip(level, boat);
        }

        Mob captain = createPirateCaptain(level, pos, target, patrolId);

        if (captain != null) {
            placeMobInBoat(level, captain, boat, pos);
        }

        int extraPirates = lootShip ? 2 : 3;

        for (int i = 0; i < extraPirates; i++) {
            Mob guard = createRandomPirateRaider(level, pos, target, patrolId);

            if (guard != null) {
                placeMobInBoat(level, guard, boat, pos);
            }
        }
    }

    private static void spawnCombatShip(
            ServerLevel level,
            BlockPos pos,
            SailboatSize size,
            ServerPlayer target,
            Boat.Type fleetWoodType,
            UUID patrolId
    ) {
        ModBoatEntity boat = createBoat(level, pos, size, fleetWoodType, 0, target, patrolId);

        if (boat == null) {
            return;
        }

        level.addFreshEntity(boat);

        int passengerSlots = getCombatPassengerSlots(size);

        for (int i = 0; i < passengerSlots; i++) {
            Mob pirate = createRandomPirateRaider(level, pos, target, patrolId);

            if (pirate != null) {
                placeMobInBoat(level, pirate, boat, pos);
            }
        }
    }

    private static void spawnLootShip(
            ServerLevel level,
            BlockPos pos,
            SailboatSize size,
            ServerPlayer target,
            Boat.Type fleetWoodType,
            UUID patrolId
    ) {
        /*
         * Always use 1 chest for pirate patrol loot boats.
         * More chests remove too many passenger slots.
         */
        int chestCount = 1;

        ModBoatEntity boat = createBoat(level, pos, size, fleetWoodType, chestCount, target, patrolId);

        if (boat == null) {
            return;
        }

        level.addFreshEntity(boat);
        fillLootShip(level, boat);

        int passengerSlots = getLootPassengerSlots(size);

        for (int i = 0; i < passengerSlots; i++) {
            Mob guard = createRandomPirateRaider(level, pos, target, patrolId);

            if (guard != null) {
                placeMobInBoat(level, guard, boat, pos);
            }
        }
    }

    private static int getCombatPassengerSlots(SailboatSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 4;
        };
    }

    private static int getLootPassengerSlots(SailboatSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    private static void fillLootShip(ServerLevel level, ModBoatEntity boat) {
        PirateLootHelper.fillLootShipChest(level, boat, boat);
    }

    private static void placeMobInBoat(ServerLevel level, Mob mob, ModBoatEntity boat, BlockPos pos) {
        if (mob == null || boat == null) {
            return;
        }

        mob.moveTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.0D,
                pos.getZ() + 0.5D,
                boat.getYRot(),
                0.0F
        );

        mob.setYHeadRot(boat.getYRot());
        mob.setPersistenceRequired();

        level.addFreshEntity(mob);

        mob.getPersistentData().putUUID("PirateRaidBoatUUID", boat.getUUID());

        boat.addMobToSailboat(mob);
    }

    private static ModBoatEntity createBoat(
            ServerLevel level,
            BlockPos pos,
            SailboatSize size,
            Boat.Type woodType,
            int chestCount,
            ServerPlayer target,
            UUID patrolId
    ) {
        ModBoatEntity boat = createBoatEntityForSize(level, size);

        if (boat == null) {
            return null;
        }

        boat.moveTo(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                getYawFacingTarget(pos, target),
                0.0F
        );

        boat.setDeltaMovement(Vec3.ZERO);
        boat.setModVariant(woodType);
        boat.setBoatSizeTier(getBoatSizeTier(size));
        boat.setChestCount(chestCount);
        boat.setBannerStack(Raid.getLeaderBannerInstance());

        boat.addTag("PirateTreasureRaidBoat");
        boat.addTag("PiratePatrolBoat");
        boat.addTag("PirateTreasureRaid_" + patrolId);

        return boat;
    }

    private static ModBoatEntity createBoatEntityForSize(ServerLevel level, SailboatSize size) {
        return switch (size) {
            case SMALL -> ModEntities.MOD_BOAT.get().create(level);
            case MEDIUM -> ModEntities.MEDIUM_MOD_BOAT.get().create(level);
            case LARGE -> ModEntities.LARGE_MOD_BOAT.get().create(level);
        };
    }

    private static int getBoatSizeTier(SailboatSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    private static float getYawFacingTarget(BlockPos pos, ServerPlayer target) {
        Vec3 boatPos = Vec3.atBottomCenterOf(pos);
        Vec3 targetPos = target.position();

        Vec3 direction = targetPos.subtract(boatPos);

        if (direction.lengthSqr() < 0.001D) {
            return 0.0F;
        }

        return (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
    }

    private static Boat.Type getRandomWoodType(ServerLevel level) {
        Boat.Type[] types = Boat.Type.values();
        return types[level.random.nextInt(types.length)];
    }

    private static Mob createRandomPirateRaider(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            UUID patrolId
    ) {
        int roll = level.random.nextInt(100);

        if (roll < 50) {
            return createPirateDeckhand(level, pos, target, patrolId);
        }

        if (roll < 80) {
            return createPirateGunner(level, pos, target, patrolId);
        }

        return createPirateMarauder(level, pos, target, patrolId);
    }

    private static void finalizePirateSpawn(ServerLevel level, Mob pirate, BlockPos pos) {
        if (pirate == null) {
            return;
        }

        pirate.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(pos),
                MobSpawnType.EVENT,
                null,
                null
        );
    }

    private static Mob createPirateCaptain(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            UUID patrolId
    ) {
        PirateCaptainEntity captain = ModEntities.PIRATE_CAPTAIN.get().create(level);

        if (captain == null) {
            return null;
        }

        finalizePirateSpawn(level, captain, pos);

        captain.setPersistenceRequired();
        captain.setTarget(target);

        captain.addTag("PirateTreasureRaid");
        captain.addTag("PiratePatrol");
        captain.addTag("PirateRaidRanged");
        captain.addTag("PirateTreasureRaid_" + patrolId);

        return captain;
    }

    private static Mob createPirateDeckhand(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            UUID patrolId
    ) {
        PirateDeckhandEntity pirate = ModEntities.PIRATE_DECKHAND.get().create(level);

        if (pirate == null) {
            return null;
        }

        finalizePirateSpawn(level, pirate, pos);

        pirate.setPersistenceRequired();
        pirate.setTarget(target);

        pirate.addTag("PirateTreasureRaid");
        pirate.addTag("PiratePatrol");
        pirate.addTag("PirateRaidBoarder");
        pirate.addTag("PirateTreasureRaid_" + patrolId);

        return pirate;
    }

    private static Mob createPirateGunner(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            UUID patrolId
    ) {
        PirateGunnerEntity pirate = ModEntities.PIRATE_GUNNER.get().create(level);

        if (pirate == null) {
            return null;
        }

        finalizePirateSpawn(level, pirate, pos);

        pirate.setPersistenceRequired();
        pirate.setTarget(target);

        pirate.addTag("PirateTreasureRaid");
        pirate.addTag("PiratePatrol");
        pirate.addTag("PirateRaidRanged");
        pirate.addTag("PirateTreasureRaid_" + patrolId);

        return pirate;
    }

    private static Mob createPirateMarauder(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            UUID patrolId
    ) {
        PirateMarauderEntity pirate = ModEntities.PIRATE_MARAUDER.get().create(level);

        if (pirate == null) {
            return null;
        }

        finalizePirateSpawn(level, pirate, pos);

        pirate.setPersistenceRequired();
        pirate.setTarget(target);

        pirate.addTag("PirateTreasureRaid");
        pirate.addTag("PiratePatrol");
        pirate.addTag("PirateRaidBoarder");
        pirate.addTag("PirateTreasureRaid_" + patrolId);

        return pirate;
    }
}