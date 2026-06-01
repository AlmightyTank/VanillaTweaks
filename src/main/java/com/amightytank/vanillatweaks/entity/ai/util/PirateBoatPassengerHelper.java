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
    public static final String RESERVED_RETURN_BOAT_UUID_TAG = "PirateReservedReturnBoatUUID";

    private static final String RAID_PIRATE_TAG = "PirateTreasureRaid";
    private static final String BOARDER_TAG = "PirateRaidBoarder";

    private static final double RESERVATION_SEARCH_RANGE = 96.0D;

    private static final List<PendingMount> PENDING_MOUNTS = new ArrayList<>();

    private PirateBoatPassengerHelper() {
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

        return getOpenSeatCount(boat) > 0;
    }

    public static boolean tryBoard(Entity passenger, Boat boat) {
        if (!canBoard(passenger, boat)) {
            return false;
        }

        if (boat instanceof ModBoatEntity modBoat && passenger instanceof Mob mob) {
            boolean mounted = modBoat.addMobToSailboat(mob);

            if (mounted) {
                clearReservedBoat(passenger);
            }

            return mounted;
        }

        boolean mounted = passenger.startRiding(boat, true);

        if (mounted) {
            clearReservedBoat(passenger);
        }

        return mounted;
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

        if (!passenger.isAlive() || !boat.isAlive() || boat.isRemoved()) {
            return false;
        }

        if (passenger.isPassenger()) {
            return false;
        }

        /*
         * Count already queued mounts too.
         * This prevents multiple queued pirates from reserving the same final seat.
         */
        if (getQueueAvailableSeatCount(boat) <= 0) {
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

            if (!pendingMount.dimension.equals(level.dimension())) {
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
                tryBoard(passenger, boat);
            }
        }
    }

    public static int getPassengerLimit(Boat boat) {
        if (boat instanceof ModBoatEntity modBoat) {
            return modBoat.getSailboatPassengerLimit();
        }

        /*
         * Vanilla fallback.
         */
        return 2;
    }

    public static int getOpenSeatCount(Boat boat) {
        if (boat == null) {
            return 0;
        }

        return Math.max(0, getPassengerLimit(boat) - boat.getPassengers().size());
    }

    public static int getQueueAvailableSeatCount(Boat boat) {
        if (boat == null) {
            return 0;
        }

        return Math.max(0, getOpenSeatCount(boat) - getQueuedSeatCount(boat));
    }

    public static int getQueuedSeatCount(Boat boat) {
        if (boat == null) {
            return 0;
        }

        int count = 0;
        UUID boatUuid = boat.getUUID();
        ResourceKey<Level> dimension = boat.level().dimension();

        for (PendingMount pendingMount : PENDING_MOUNTS) {
            if (!pendingMount.dimension.equals(dimension)) {
                continue;
            }

            if (boatUuid.equals(pendingMount.boatUuid)) {
                count++;
            }
        }

        return count;
    }

    public static boolean isFull(Boat boat) {
        return getOpenSeatCount(boat) <= 0;
    }

    public static int getReservedSeatCount(Boat boat) {
        if (!(boat.level() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        UUID boatUuid = boat.getUUID();
        AABB searchBox = boat.getBoundingBox().inflate(RESERVATION_SEARCH_RANGE);

        int count = 0;

        for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, searchBox, PirateBoatPassengerHelper::isReturningBoarder)) {
            if (mob.isPassenger()) {
                continue;
            }

            if (!mob.getPersistentData().hasUUID(RESERVED_RETURN_BOAT_UUID_TAG)) {
                continue;
            }

            if (boatUuid.equals(mob.getPersistentData().getUUID(RESERVED_RETURN_BOAT_UUID_TAG))) {
                count++;
            }
        }

        return count;
    }

    public static int getAvailableReturnSeatCount(Boat boat) {
        return Math.max(0, getOpenSeatCount(boat) - getReservedSeatCount(boat));
    }

    public static boolean hasAvailableReturnSeat(Boat boat) {
        return getAvailableReturnSeatCount(boat) > 0;
    }

    public static boolean reserveReturnSeat(Entity passenger, Boat boat) {
        if (passenger == null || boat == null) {
            return false;
        }

        if (passenger.level().isClientSide) {
            return false;
        }

        if (passenger.level() != boat.level()) {
            return false;
        }

        if (!passenger.isAlive() || !boat.isAlive() || boat.isRemoved()) {
            return false;
        }

        if (passenger.isPassenger()) {
            return false;
        }

        if (!hasAvailableReturnSeat(boat)) {
            return false;
        }

        passenger.getPersistentData().putUUID(RESERVED_RETURN_BOAT_UUID_TAG, boat.getUUID());
        return true;
    }

    public static void clearReservedBoat(Entity passenger) {
        if (passenger != null) {
            passenger.getPersistentData().remove(RESERVED_RETURN_BOAT_UUID_TAG);
        }
    }

    public static boolean isBoatWaitingForCrew(Boat boat) {
        return boat != null
                && boat.isAlive()
                && !boat.isRemoved()
                && getOpenSeatCount(boat) > 0;
    }

    private static boolean isReturningBoarder(Mob mob) {
        if (mob == null || !mob.isAlive()) {
            return false;
        }

        return mob.getTags().contains(RAID_PIRATE_TAG)
                || mob.getTags().contains(BOARDER_TAG);
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