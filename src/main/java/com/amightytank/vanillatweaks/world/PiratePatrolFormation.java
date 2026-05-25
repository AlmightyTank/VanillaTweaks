package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.util.PirateLootHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class PiratePatrolFormation {

    private enum SailboatSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    public static void spawn(ServerLevel level, BlockPos centerPos, ServerPlayer target, PiratePatrolSize size) {
        Boat.Type fleetWoodType = getRandomWoodType(level);

        switch (size) {
            case SMALL -> spawnSmallPatrol(level, centerPos, target, fleetWoodType);
            case MEDIUM -> spawnMediumPatrol(level, centerPos, target, fleetWoodType);
            case LARGE -> spawnLargePatrol(level, centerPos, target, fleetWoodType);
        }
    }

    private static void spawnSmallPatrol(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            Boat.Type fleetWoodType
    ) {
        // Small patrol:
        // 1 large captain sailboat with 2 chests
        // 2 small combat sailboats

        spawnCaptainShip(level, pos.offset(0, 0, 0), true, target, fleetWoodType);

        spawnCombatShip(level, pos.offset(6, 0, 6), SailboatSize.SMALL, target, fleetWoodType);
        spawnCombatShip(level, pos.offset(-6, 0, 6), SailboatSize.SMALL, target, fleetWoodType);
    }

    private static void spawnMediumPatrol(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            Boat.Type fleetWoodType
    ) {
        // Medium patrol:
        // 1 large captain sailboat
        // 2 medium combat sailboats
        // 1 small loot sailboat with 1 chest

        spawnCaptainShip(level, pos.offset(0, 0, 0), false, target, fleetWoodType);

        spawnCombatShip(level, pos.offset(7, 0, 7), SailboatSize.MEDIUM, target, fleetWoodType);
        spawnCombatShip(level, pos.offset(-7, 0, 7), SailboatSize.MEDIUM, target, fleetWoodType);

        spawnLootShip(level, pos.offset(0, 0, -8), SailboatSize.SMALL, target, fleetWoodType);
    }

    private static void spawnLargePatrol(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            Boat.Type fleetWoodType
    ) {
        // Large patrol:
        // 1 large captain sailboat
        // 3 large combat sailboats
        // 2 medium loot sailboats with 2 chests each

        spawnCaptainShip(level, pos.offset(0, 0, 0), false, target, fleetWoodType);

        spawnCombatShip(level, pos.offset(8, 0, 8), SailboatSize.LARGE, target, fleetWoodType);
        spawnCombatShip(level, pos.offset(-8, 0, 8), SailboatSize.LARGE, target, fleetWoodType);
        spawnCombatShip(level, pos.offset(0, 0, 14), SailboatSize.LARGE, target, fleetWoodType);

        spawnLootShip(level, pos.offset(7, 0, -8), SailboatSize.MEDIUM, target, fleetWoodType);
        spawnLootShip(level, pos.offset(-7, 0, -8), SailboatSize.MEDIUM, target, fleetWoodType);
    }

    private static int getPassengerSlots(SailboatSize size, int chestCount) {
        int baseSlots = switch (size) {
            case SMALL -> 2;
            case MEDIUM -> 3;
            case LARGE -> 4;
        };

        return Math.max(1, baseSlots - chestCount);
    }

    private static void spawnCaptainShip(
            ServerLevel level,
            BlockPos pos,
            boolean hasChests,
            ServerPlayer target,
            Boat.Type fleetWoodType
    ) {
        SailboatSize size = SailboatSize.LARGE;

        /*
         * Large captain chest ship gets 2 chests.
         * That leaves 2 passenger seats: captain + 1 guard.
         */
        int chestCount = hasChests ? 2 : 0;

        ModBoatEntity boat = createBoat(level, pos, size, fleetWoodType, chestCount);

        if (boat == null) {
            return;
        }

        level.addFreshEntity(boat);

        if (hasChests) {
            fillLootShip(level, boat);
        }

        int passengerSlots = getPassengerSlots(size, chestCount);

        Mob captain = createPirateCaptain(level, pos);

        if (captain != null) {
            placeMobInBoat(level, captain, boat, pos);
        }

        while (boat.getPassengers().size() < passengerSlots) {
            Mob guard = createPirateRaider(level, pos);

            if (guard == null) {
                return;
            }

            placeMobInBoat(level, guard, boat, pos);
        }
    }

    private static void spawnCombatShip(
            ServerLevel level,
            BlockPos pos,
            SailboatSize size,
            ServerPlayer target,
            Boat.Type fleetWoodType
    ) {
        ModBoatEntity boat = createBoat(level, pos, size, fleetWoodType, 0);

        if (boat == null) {
            return;
        }

        level.addFreshEntity(boat);

        int passengerSlots = getPassengerSlots(size, 0);

        for (int i = 0; i < passengerSlots; i++) {
            Mob pirate = createPirateRaider(level, pos);

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
            Boat.Type fleetWoodType
    ) {
        int chestCount = getChestCountForSize(size);

        ModBoatEntity boat = createBoat(level, pos, size, fleetWoodType, chestCount);

        if (boat == null) {
            return;
        }

        level.addFreshEntity(boat);
        fillLootShip(level, boat);

        int passengerSlots = getPassengerSlots(size, chestCount);

        for (int i = 0; i < passengerSlots; i++) {
            Mob guard = createPirateRaider(level, pos);

            if (guard != null) {
                placeMobInBoat(level, guard, boat, pos);
            }
        }
    }

    private static void fillLootShip(ServerLevel level, ModBoatEntity boat) {
        /*
         * This should compile if PirateLootHelper.fillLootShipChest accepts Container.
         * ModBoatEntity implements Container now.
         *
         * If your PirateLootHelper still specifically requires ModChestBoatEntity,
         * update that helper to accept Container or ModBoatEntity.
         */
        PirateLootHelper.fillLootShipChest(level, boat, boat);
    }

    private static void placeMobInBoat(ServerLevel level, Mob mob, Boat boat, BlockPos pos) {
        mob.moveTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.0D,
                pos.getZ() + 0.5D,
                boat.getYRot(),
                0.0F
        );

        mob.setPersistenceRequired();

        level.addFreshEntity(mob);
        mob.startRiding(boat, true);
    }

    private static ModBoatEntity createBoat(
            ServerLevel level,
            BlockPos pos,
            SailboatSize size,
            Boat.Type woodType,
            int chestCount
    ) {
        ModBoatEntity boat = createBoatEntityForSize(level, size);

        if (boat == null) {
            return null;
        }

        boat.moveTo(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                0.0F,
                0.0F
        );

        boat.setDeltaMovement(Vec3.ZERO);
        boat.setModVariant(woodType);
        boat.setBannerCount(getBannerCountForSize(size));
        boat.setChestCount(chestCount);

        return boat;
    }

    private static ModBoatEntity createBoatEntityForSize(ServerLevel level, SailboatSize size) {
        return switch (size) {
            case SMALL -> ModEntities.MOD_BOAT.get().create(level);
            case MEDIUM -> ModEntities.MEDIUM_MOD_BOAT.get().create(level);
            case LARGE -> ModEntities.LARGE_MOD_BOAT.get().create(level);
        };
    }

    private static int getBannerCountForSize(SailboatSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    private static int getChestCountForSize(SailboatSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    private static Boat.Type getRandomWoodType(ServerLevel level) {
        Boat.Type[] types = Boat.Type.values();
        return types[level.random.nextInt(types.length)];
    }

    private static Mob createPirateCaptain(ServerLevel level, BlockPos pos) {
        Mob captain = ModEntities.PIRATE_CAPTAIN.get().create(level);

        if (captain == null) {
            return null;
        }

        captain.setPersistenceRequired();

        return captain;
    }

    private static Mob createPirateRaider(ServerLevel level, BlockPos pos) {
        int roll = level.random.nextInt(100);

        Mob pirate;

        if (roll < 55) {
            pirate = ModEntities.PIRATE_DECKHAND.get().create(level);
        } else if (roll < 85) {
            pirate = ModEntities.PIRATE_GUNNER.get().create(level);
        } else {
            pirate = ModEntities.PIRATE_MARAUDER.get().create(level);
        }

        if (pirate == null) {
            return null;
        }

        pirate.setPersistenceRequired();

        return pirate;
    }
}