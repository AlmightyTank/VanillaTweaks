package com.amightytank.vanillatweaks.entity.ai.util;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class PirateBoatPassengerHelper {
    private static final List<PendingMount> PENDING_MOUNTS = new ArrayList<>();

    /*
     * This is the permanent home boat assignment.
     * Pirates should not switch boats during raids/patrols.
     */
    public static final String HOME_BOAT_UUID_TAG = "PirateHomeBoatUUID";

    private PirateBoatPassengerHelper() {
    }

    public static void assignHomeBoat(Mob pirate, Entity boatEntity) {
        if (pirate == null || boatEntity == null) {
            return;
        }

        pirate.getPersistentData().putUUID(HOME_BOAT_UUID_TAG, boatEntity.getUUID());
        pirate.getPersistentData().putUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG, boatEntity.getUUID());
    }

    public static boolean isAssignedToBoat(Entity passenger, Boat boat) {
        if (passenger == null || boat == null) {
            return false;
        }

        if (passenger.getPersistentData().hasUUID(HOME_BOAT_UUID_TAG)) {
            return passenger.getPersistentData().getUUID(HOME_BOAT_UUID_TAG).equals(boat.getUUID());
        }

        if (passenger.getPersistentData().hasUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG)) {
            return passenger.getPersistentData().getUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG).equals(boat.getUUID());
        }

        return false;
    }

    public static boolean canBoard(Entity passenger, Boat boat) {
        if (passenger == null || boat == null) {
            return false;
        }

        if (passenger.level().isClientSide) {
            return false;
        }

        if (!passenger.isAlive()) {
            return false;
        }

        if (!boat.isAlive() || boat.isRemoved()) {
            return false;
        }

        if (passenger.level() != boat.level()) {
            return false;
        }

        if (passenger.isPassenger()) {
            return false;
        }

        return boat.getPassengers().size() < getPassengerLimit(boat);
    }

    public static boolean canBoardAssignedBoat(Entity passenger, Boat boat) {
        return canBoard(passenger, boat) && isAssignedToBoat(passenger, boat);
    }

    public static boolean tryBoard(Entity passenger, Boat boat) {
        if (!canBoard(passenger, boat)) {
            return false;
        }

        if (boat instanceof ModBoatEntity modBoat && passenger instanceof Mob mob) {
            return modBoat.addMobToSailboat(mob);
        }

        return passenger.startRiding(boat, true);
    }

    public static boolean tryBoardAssignedBoat(Entity passenger, Boat boat) {
        if (!canBoardAssignedBoat(passenger, boat)) {
            return false;
        }

        return tryBoard(passenger, boat);
    }

    public static boolean queueBoard(Entity passenger, Entity boatEntity, int delayTicks) {
        if (passenger == null || boatEntity == null) {
            return false;
        }

        if (!(passenger.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (!(boatEntity instanceof Boat boat)) {
            return false;
        }

        if (passenger.level() != boat.level()) {
            return false;
        }

        PENDING_MOUNTS.add(new PendingMount(
                serverLevel.dimension(),
                passenger.getUUID(),
                boat.getUUID(),
                Math.max(1, delayTicks)
        ));

        return true;
    }

    public static void tickQueuedMounts(ServerLevel level) {
        Iterator<PendingMount> iterator = PENDING_MOUNTS.iterator();

        while (iterator.hasNext()) {
            PendingMount pendingMount = iterator.next();

            if (pendingMount.dimension != level.dimension()) {
                continue;
            }

            pendingMount.ticks--;

            if (pendingMount.ticks > 0) {
                continue;
            }

            iterator.remove();

            Entity passenger = level.getEntity(pendingMount.passengerUuid);
            Entity vehicle = level.getEntity(pendingMount.boatUuid);

            if (vehicle instanceof Boat boat) {
                tryBoardAssignedBoat(passenger, boat);
            }
        }
    }

    public static int getPassengerLimit(Boat boat) {
        if (boat instanceof ModBoatEntity modBoat) {
            return modBoat.getSailboatPassengerLimit();
        }

        return 2;
    }

    public static int getOpenSeatCount(Boat boat) {
        if (boat == null) {
            return 0;
        }

        return Math.max(0, getPassengerLimit(boat) - boat.getPassengers().size());
    }

    public static boolean isFull(Boat boat) {
        return getOpenSeatCount(boat) <= 0;
    }

    /*
     * Used by the pilot goal.
     * If this returns true, the boat should stop/wait instead of chasing.
     */
    public static boolean hasMissingAssignedCrewNearby(Boat boat, double searchRange) {
        if (!(boat.level() instanceof ServerLevel level)) {
            return false;
        }

        if (isFull(boat)) {
            return false;
        }

        AABB searchBox = boat.getBoundingBox().inflate(searchRange);

        List<Mob> missingCrew = level.getEntitiesOfClass(
                Mob.class,
                searchBox,
                mob -> mob.isAlive()
                        && !mob.isRemoved()
                        && isAssignedToBoat(mob, boat)
                        && mob.getVehicle() != boat
                        && !mob.isPassenger()
        );

        return !missingCrew.isEmpty();
    }

    private static final class PendingMount {
        private final ResourceKey<Level> dimension;
        private final UUID passengerUuid;
        private final UUID boatUuid;
        private int ticks;

        private PendingMount(ResourceKey<Level> dimension, UUID passengerUuid, UUID boatUuid, int ticks) {
            this.dimension = dimension;
            this.passengerUuid = passengerUuid;
            this.boatUuid = boatUuid;
            this.ticks = ticks;
        }
    }
}