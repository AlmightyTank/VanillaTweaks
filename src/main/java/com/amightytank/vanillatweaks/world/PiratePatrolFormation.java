package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.boat.ModChestBoatEntity;
import com.amightytank.vanillatweaks.util.PirateLootHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class PiratePatrolFormation {

    public static void spawn(ServerLevel level, BlockPos centerPos, ServerPlayer target, PiratePatrolSize size) {
        FleetWoodType fleetWoodType = FleetWoodType.getRandom(level.random);

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
            FleetWoodType fleetWoodType
    ) {
        // Small patrol:
        // 1 large captain chest sailboat
        // 2 small combat sailboats

        spawnCaptainShip(level, pos.offset(0, 0, 0), true, target, fleetWoodType);

        spawnCombatShip(level, pos.offset(6, 0, 6), ModBoatEntity.BoatSize.SAILBOAT, target, fleetWoodType);
        spawnCombatShip(level, pos.offset(-6, 0, 6), ModBoatEntity.BoatSize.SAILBOAT, target, fleetWoodType);
    }

    private static void spawnMediumPatrol(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            FleetWoodType fleetWoodType
    ) {
        // Medium patrol:
        // 1 large captain sailboat
        // 2 medium combat sailboats
        // 1 small chest loot sailboat

        spawnCaptainShip(level, pos.offset(0, 0, 0), false, target, fleetWoodType);

        spawnCombatShip(level, pos.offset(7, 0, 7), ModBoatEntity.BoatSize.MEDIUM_SAILBOAT, target, fleetWoodType);
        spawnCombatShip(level, pos.offset(-7, 0, 7), ModBoatEntity.BoatSize.MEDIUM_SAILBOAT, target, fleetWoodType);

        spawnLootShip(level, pos.offset(0, 0, -8), ModBoatEntity.BoatSize.SAILBOAT, target, fleetWoodType);
    }

    private static void spawnLargePatrol(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer target,
            FleetWoodType fleetWoodType
    ) {
        // Large patrol:
        // 1 large captain sailboat
        // 3 large combat sailboats
        // 2 medium chest loot sailboats

        spawnCaptainShip(level, pos.offset(0, 0, 0), false, target, fleetWoodType);

        spawnCombatShip(level, pos.offset(8, 0, 8), ModBoatEntity.BoatSize.LARGE_SAILBOAT, target, fleetWoodType);
        spawnCombatShip(level, pos.offset(-8, 0, 8), ModBoatEntity.BoatSize.LARGE_SAILBOAT, target, fleetWoodType);
        spawnCombatShip(level, pos.offset(0, 0, 14), ModBoatEntity.BoatSize.LARGE_SAILBOAT, target, fleetWoodType);

        spawnLootShip(level, pos.offset(7, 0, -8), ModBoatEntity.BoatSize.MEDIUM_SAILBOAT, target, fleetWoodType);
        spawnLootShip(level, pos.offset(-7, 0, -8), ModBoatEntity.BoatSize.MEDIUM_SAILBOAT, target, fleetWoodType);
    }

    private static int getPassengerSlots(ModBoatEntity.BoatSize size, boolean chestBoat) {
        return switch (size) {
            case SAILBOAT -> 1;
            case MEDIUM_SAILBOAT -> chestBoat ? 1 : 2;
            case LARGE_SAILBOAT -> chestBoat ? 2 : 3;
        };
    }

    private static void spawnCaptainShip(
            ServerLevel level,
            BlockPos pos,
            boolean chestBoat,
            ServerPlayer target,
            FleetWoodType fleetWoodType
    ) {
        ModBoatEntity.BoatSize size = ModBoatEntity.BoatSize.LARGE_SAILBOAT;

        Boat boat = chestBoat
                ? createChestBoat(level, pos, size, fleetWoodType)
                : createBoat(level, pos, size, fleetWoodType);

        if (boat == null) return;

        level.addFreshEntity(boat);

        int passengerSlots = getPassengerSlots(size, chestBoat);

        Mob captain = createPirateCaptain(level, pos);

        if (captain != null) {
            placeMobInBoat(level, captain, boat, pos);
        }

        while (boat.getPassengers().size() < passengerSlots) {
            Mob guard = createPirateRaider(level, pos);

            if (guard == null) return;

            placeMobInBoat(level, guard, boat, pos);
        }
    }
    private static void spawnCombatShip(
            ServerLevel level,
            BlockPos pos,
            ModBoatEntity.BoatSize size,
            ServerPlayer target,
            FleetWoodType fleetWoodType
    ) {
        Boat boat = createBoat(level, pos, size, fleetWoodType);

        if (boat == null) return;

        level.addFreshEntity(boat);

        int passengerSlots = getPassengerSlots(size, false);

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
            ModBoatEntity.BoatSize size,
            ServerPlayer target,
            FleetWoodType fleetWoodType
    ) {
        Boat boat = createChestBoat(level, pos, size, fleetWoodType);

        if (boat == null) return;

        level.addFreshEntity(boat);

        if (boat instanceof ModChestBoatEntity chestBoat) {
            PirateLootHelper.fillLootShipChest(level, chestBoat, chestBoat);
        }

        int passengerSlots = getPassengerSlots(size, true);

        for (int i = 0; i < passengerSlots; i++) {
            Mob guard = createPirateRaider(level, pos);

            if (guard != null) {
                placeMobInBoat(level, guard, boat, pos);
            }
        }
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

    private static Boat createBoat(
            ServerLevel level,
            BlockPos pos,
            ModBoatEntity.BoatSize size,
            FleetWoodType fleetWoodType
    ) {
        ModBoatEntity boat = ModEntities.MOD_BOAT.get().create(level);

        if (boat == null) return null;

        boat.moveTo(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                0.0F,
                0.0F
        );

        boat.setDeltaMovement(Vec3.ZERO);
        boat.setVariant(getBoatVariant(fleetWoodType, size));

        return boat;
    }

    private static Boat createChestBoat(
            ServerLevel level,
            BlockPos pos,
            ModBoatEntity.BoatSize size,
            FleetWoodType fleetWoodType
    ) {
        ModChestBoatEntity boat = ModEntities.MOD_CHEST_BOAT.get().create(level);

        if (boat == null) return null;

        boat.moveTo(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                0.0F,
                0.0F
        );

        boat.setDeltaMovement(Vec3.ZERO);
        boat.setVariant(getBoatVariant(fleetWoodType, size));

        return boat;
    }

    private static ModBoatEntity.Type getBoatVariant(
            FleetWoodType fleetWoodType,
            ModBoatEntity.BoatSize boatSize
    ) {
        for (ModBoatEntity.Type type : ModBoatEntity.Type.values()) {
            if (type.getWoodKind() == fleetWoodType.getWoodKind()
                    && type.getBoatSize() == boatSize) {
                return type;
            }
        }

        throw new IllegalStateException(
                "Missing boat variant for wood="
                        + fleetWoodType.name()
                        + ", size="
                        + boatSize.getSerializedName()
        );
    }

    private static Mob createPirateCaptain(ServerLevel level, BlockPos pos) {
        Mob captain = ModEntities.PIRATE_CAPTAIN.get().create(level);

        if (captain == null) return null;

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
            pirate = ModEntities.PIRATE_BRUTE.get().create(level);
        }

        if (pirate == null) return null;

        pirate.setPersistenceRequired();

        return pirate;
    }
}