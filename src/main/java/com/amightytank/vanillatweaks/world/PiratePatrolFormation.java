package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.world.pirate_raid.PirateShipSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PiratePatrolFormation {
    private static final double SHIP_SPACING = 8.0D;

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

            if (awayFromTarget.lengthSqr() > 0.001D) {
                awayFromTarget = awayFromTarget.normalize();
                center = target.position().add(awayFromTarget.scale(34.0D));
                center = new Vec3(center.x, spawnPos.getY(), center.z);
            }
        }

        return spawn(level, center, target, size);
    }

    public static List<Mob> spawn(ServerLevel level, Vec3 center, ServerPlayer target, PiratePatrolSize size) {
        if (level == null || center == null || size == null) {
            return List.of();
        }

        UUID fleetId = UUID.randomUUID();
        Boat.Type fleetWoodType = getRandomBoatType(level);

        Vec3 forward = getForwardDirection(center, target);
        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);

        return switch (size) {
            case SMALL -> spawnSmallPatrol(level, center, forward, right, fleetWoodType, fleetId);
            case MEDIUM -> spawnMediumPatrol(level, center, forward, right, fleetWoodType, fleetId);
            case LARGE -> spawnLargePatrol(level, center, forward, right, fleetWoodType, fleetId);
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
         * - 1 large captain chest sailboat
         * - 2 small combat sailboats
         */
        ModBoatEntity captainBoat = PirateShipSpawner.spawnCaptainShip(
                level,
                center,
                woodType,
                fleetId,
                true
        );

        ModBoatEntity leftCombat = PirateShipSpawner.spawnCombatShip(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.SMALL,
                fleetId
        );

        ModBoatEntity rightCombat = PirateShipSpawner.spawnCombatShip(
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

        /*
         * Medium patrol:
         * - 1 large captain sailboat
         * - 2 medium combat sailboats
         * - 1 small chest loot sailboat
         */
        ModBoatEntity captainBoat = PirateShipSpawner.spawnCaptainShip(
                level,
                center,
                woodType,
                fleetId,
                false
        );

        ModBoatEntity leftCombat = PirateShipSpawner.spawnCombatShip(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity rightCombat = PirateShipSpawner.spawnCombatShip(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).subtract(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity lootBoat = PirateShipSpawner.spawnLootShip(
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

        /*
         * Large patrol:
         * - 1 large captain sailboat
         * - 3 large combat sailboats
         * - 2 medium chest loot sailboats
         */
        ModBoatEntity captainBoat = PirateShipSpawner.spawnCaptainShip(
                level,
                center,
                woodType,
                fleetId,
                false
        );

        ModBoatEntity leftCombat = PirateShipSpawner.spawnCombatShip(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).add(right.scale(SHIP_SPACING * 1.25D)),
                woodType,
                PirateShipSpawner.ShipSize.LARGE,
                fleetId
        );

        ModBoatEntity middleCombat = PirateShipSpawner.spawnCombatShip(
                level,
                center.subtract(forward.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.LARGE,
                fleetId
        );

        ModBoatEntity rightCombat = PirateShipSpawner.spawnCombatShip(
                level,
                center.subtract(forward.scale(SHIP_SPACING)).subtract(right.scale(SHIP_SPACING * 1.25D)),
                woodType,
                PirateShipSpawner.ShipSize.LARGE,
                fleetId
        );

        ModBoatEntity leftLoot = PirateShipSpawner.spawnLootShip(
                level,
                center.subtract(forward.scale(SHIP_SPACING * 2.0D)).add(right.scale(SHIP_SPACING)),
                woodType,
                PirateShipSpawner.ShipSize.MEDIUM,
                fleetId
        );

        ModBoatEntity rightLoot = PirateShipSpawner.spawnLootShip(
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