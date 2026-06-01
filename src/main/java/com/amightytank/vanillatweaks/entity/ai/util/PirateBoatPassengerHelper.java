package com.amightytank.vanillatweaks.entity.ai.util;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class PirateBoatPassengerHelper {
    public static final String RAID_PIRATE_TAG = "PirateTreasureRaid";
    public static final String BOARDER_TAG = "PirateRaidBoarder";

    public static final String FLEET_TAG_PREFIX = "PirateFleet:";
    public static final String HOME_BOAT_TAG_PREFIX = "PirateHomeBoat:";
    public static final String RESERVED_BY_TAG_PREFIX = "PirateReservedBy:";
    public static final String CAPTAIN_RESCUE_TAG_PREFIX = "PirateCaptainRescue:";
    public static final String CAPTAIN_SHIP_TAG = "PirateCaptainShip";
    public static final String DISPLACED_DRIVER_TAG = "PirateDisplacedDriver";

    private static final double BOAT_SEARCH_RANGE = 96.0D;
    private static final double RESERVATION_CLEANUP_RANGE = 128.0D;
    private static final double QUEUED_BOARD_DISTANCE = 4.0D;

    private static final List<QueuedMount> QUEUED_MOUNTS = new ArrayList<>();

    private PirateBoatPassengerHelper() {
    }

    private record QueuedMount(UUID pirateId, UUID boatId, boolean priority) {
    }

    public static void tickQueuedMounts(ServerLevel level) {
        tickQueuedMounts((Level) level);
    }

    public static void tickQueuedMounts(Level level) {
        Iterator<QueuedMount> iterator = QUEUED_MOUNTS.iterator();

        while (iterator.hasNext()) {
            QueuedMount mount = iterator.next();

            Entity pirateEntity = findEntityByUuid(level, mount.pirateId(), null, 0.0D);
            Entity boatEntity = findEntityByUuid(level, mount.boatId(), null, 0.0D);

            if (!(pirateEntity instanceof Mob pirate) || !(boatEntity instanceof Boat boat)) {
                iterator.remove();
                continue;
            }

            if (!isValidBoat(boat) || !pirate.isAlive() || pirate.isRemoved()) {
                clearReservationForPirate(pirate);
                iterator.remove();
                continue;
            }

            if (pirate.isPassenger()) {
                clearReservationForPirate(pirate);
                iterator.remove();
                continue;
            }

            if (pirate.distanceToSqr(boat) > QUEUED_BOARD_DISTANCE * QUEUED_BOARD_DISTANCE) {
                continue;
            }

            if (attemptBoard(pirate, boat, mount.priority())) {
                iterator.remove();
            }
        }
    }

    public static void assignHomeBoat(Mob pirate, Boat boat) {
        clearHomeBoatTag(pirate);

        pirate.addTag(HOME_BOAT_TAG_PREFIX + boat.getUUID());

        copyFleetTag(pirate, boat);

        if (pirate instanceof PirateCaptainEntity) {
            makeCaptainShip(pirate, boat);
        }
    }

    public static void clearReservedBoat(Mob pirate) {
        clearHomeBoatTag(pirate);
        clearReservationForPirate(pirate);
    }

    public static void queueBoard(Mob pirate, Boat boat) {
        queueBoard(pirate, boat, false);
    }

    public static void queueBoard(Mob pirate, Boat boat, boolean priority) {
        if (pirate == null || boat == null || !isValidBoat(boat)) {
            return;
        }

        clearReservationForPirate(pirate);

        if (!priority && !hasAvailableReturnSeatFor(pirate, boat)) {
            return;
        }

        reserveSeat(pirate, boat);

        QUEUED_MOUNTS.removeIf(entry -> entry.pirateId().equals(pirate.getUUID()));
        QUEUED_MOUNTS.add(new QueuedMount(pirate.getUUID(), boat.getUUID(), priority));
    }

    public static boolean tryBoardQueuedNow(Mob pirate) {
        if (pirate == null || pirate.level().isClientSide) {
            return false;
        }

        Iterator<QueuedMount> iterator = QUEUED_MOUNTS.iterator();

        while (iterator.hasNext()) {
            QueuedMount mount = iterator.next();

            if (!mount.pirateId().equals(pirate.getUUID())) {
                continue;
            }

            Entity boatEntity = findEntityByUuid(pirate.level(), mount.boatId(), pirate.position(), BOAT_SEARCH_RANGE);

            if (!(boatEntity instanceof Boat boat)) {
                clearReservationForPirate(pirate);
                iterator.remove();
                return false;
            }

            if (pirate.distanceToSqr(boat) > QUEUED_BOARD_DISTANCE * QUEUED_BOARD_DISTANCE) {
                return false;
            }

            if (attemptBoard(pirate, boat, mount.priority())) {
                iterator.remove();
                return true;
            }

            return false;
        }

        return false;
    }

    public static boolean attemptBoard(Mob pirate, Boat boat, boolean priority) {
        if (pirate == null || boat == null || !isValidBoat(boat)) {
            return false;
        }

        if (pirate.isPassenger()) {
            return pirate.getVehicle() == boat;
        }

        if (priority && pirate instanceof PirateCaptainEntity && !hasAvailableReturnSeatFor(pirate, boat)) {
            return swapDriverForCaptain(pirate, boat);
        }

        if (!hasAvailableReturnSeatFor(pirate, boat)) {
            return false;
        }

        clearReservationForPirate(pirate);

        boolean mounted = pirate.startRiding(boat, true);

        if (mounted) {
            assignHomeBoat(pirate, boat);
            clearDisplacedDriver(pirate);

            if (pirate instanceof PirateCaptainEntity) {
                clearCaptainRescue(boat);
                makeCaptainShip(pirate, boat);
            }
        }

        return mounted;
    }

    public static boolean hasAvailableReturnSeat(Boat boat) {
        return hasAvailableReturnSeatFor(null, boat);
    }

    public static boolean hasAvailableReturnSeatFor(Mob pirate, Boat boat) {
        if (boat == null || !isValidBoat(boat)) {
            return false;
        }

        pruneStaleReservations(boat);

        UUID excludedPirate = pirate == null ? null : pirate.getUUID();

        int passengers = boat.getPassengers().size();
        int reserved = getReservedSeatCount(boat, excludedPirate);
        int maxPassengers = getBoatSeatLimit(boat);

        if (pirate != null && pirate.getVehicle() == boat) {
            return true;
        }

        return passengers + reserved < maxPassengers;
    }

    public static Boat getHomeBoat(Mob pirate) {
        if (pirate == null) {
            return null;
        }

        UUID boatUuid = getHomeBoatUuid(pirate);

        if (boatUuid == null) {
            return null;
        }

        Entity entity = findEntityByUuid(pirate.level(), boatUuid, pirate.position(), RESERVATION_CLEANUP_RANGE);

        if (entity instanceof Boat boat && isValidBoat(boat)) {
            return boat;
        }

        clearHomeBoatTag(pirate);
        return null;
    }

    public static boolean isCaptainNeedingRescue(Mob pirate) {
        if (!(pirate instanceof PirateCaptainEntity)) {
            return false;
        }

        if (!pirate.isAlive() || pirate.isRemoved()) {
            return false;
        }

        if (pirate.getVehicle() instanceof Boat currentBoat && isValidBoat(currentBoat)) {
            return false;
        }

        Boat homeBoat = getHomeBoat(pirate);

        return homeBoat == null || !isValidBoat(homeBoat);
    }

    public static Boat findBestCaptainRescueBoat(Mob captain) {
        return findBestCaptainRescueBoat(captain, BOAT_SEARCH_RANGE);
    }

    public static Boat findBestCaptainRescueBoat(Mob captain, double range) {
        if (!(captain instanceof PirateCaptainEntity)) {
            return null;
        }

        List<Boat> boats = getNearbyFleetBoats(captain, range);

        if (boats.isEmpty()) {
            return null;
        }

        /*
         * First choice:
         * closest non-loot boat with open room.
         */
        Boat openCombatBoat = boats.stream()
                .filter(boat -> !isLootBoat(boat))
                .filter(boat -> hasAvailableReturnSeatFor(captain, boat))
                .min(Comparator.comparingDouble(captain::distanceToSqr))
                .orElse(null);

        if (openCombatBoat != null) {
            return openCombatBoat;
        }

        /*
         * Second choice:
         * closest boat with open room, even if it is a chest/loot boat.
         */
        Boat openAnyBoat = boats.stream()
                .filter(boat -> hasAvailableReturnSeatFor(captain, boat))
                .min(Comparator.comparingDouble(captain::distanceToSqr))
                .orElse(null);

        if (openAnyBoat != null) {
            return openAnyBoat;
        }

        /*
         * No boat has room:
         * closest non-loot boat becomes the rescue boat and will swap driver.
         */
        Boat fullCombatBoat = boats.stream()
                .filter(boat -> !isLootBoat(boat))
                .min(Comparator.comparingDouble(captain::distanceToSqr))
                .orElse(null);

        if (fullCombatBoat != null) {
            return fullCombatBoat;
        }

        /*
         * Last resort:
         * any closest boat.
         */
        return boats.stream()
                .min(Comparator.comparingDouble(captain::distanceToSqr))
                .orElse(null);
    }

    public static Boat findBestReturnBoat(Mob pirate) {
        return findBestReturnBoat(pirate, BOAT_SEARCH_RANGE);
    }

    public static Boat findBestReturnBoat(Mob pirate, double range) {
        if (pirate == null) {
            return null;
        }

        Boat homeBoat = getHomeBoat(pirate);

        if (homeBoat != null && hasAvailableReturnSeatFor(pirate, homeBoat)) {
            return homeBoat;
        }

        List<Boat> boats = getNearbyFleetBoats(pirate, range);

        return boats.stream()
                .filter(boat -> hasAvailableReturnSeatFor(pirate, boat))
                .min(Comparator.comparingDouble(boat -> {
                    double score = pirate.distanceToSqr(boat);

                    if (isLootBoat(boat)) {
                        score += 400.0D;
                    }

                    if (isCaptainShip(boat)) {
                        score -= 50.0D;
                    }

                    return score;
                }))
                .orElse(null);
    }

    public static void requestCaptainRescue(Mob captain, Boat rescueBoat) {
        if (!(captain instanceof PirateCaptainEntity) || rescueBoat == null) {
            return;
        }

        clearCaptainRescue(rescueBoat);
        rescueBoat.addTag(CAPTAIN_RESCUE_TAG_PREFIX + captain.getUUID());

        copyFleetTag(captain, rescueBoat);
        assignHomeBoat(captain, rescueBoat);
        queueBoard(captain, rescueBoat, true);
    }

    public static Mob getCaptainRescueTarget(Boat boat) {
        if (boat == null || boat.level().isClientSide) {
            return null;
        }

        for (String tag : boat.getTags()) {
            if (!tag.startsWith(CAPTAIN_RESCUE_TAG_PREFIX)) {
                continue;
            }

            UUID captainUuid = parseUuid(tag.substring(CAPTAIN_RESCUE_TAG_PREFIX.length()));

            if (captainUuid == null) {
                continue;
            }

            Entity entity = findEntityByUuid(boat.level(), captainUuid, boat.position(), BOAT_SEARCH_RANGE);

            if (entity instanceof Mob captain && captain instanceof PirateCaptainEntity && captain.isAlive() && !captain.isPassenger()) {
                return captain;
            }
        }

        clearCaptainRescue(boat);
        return null;
    }

    public static boolean hasCaptainRescueTarget(Boat boat) {
        return getCaptainRescueTarget(boat) != null;
    }

    public static void clearCaptainRescue(Boat boat) {
        if (boat == null) {
            return;
        }

        removeTagsStartingWith(boat, CAPTAIN_RESCUE_TAG_PREFIX);
    }

    public static boolean swapDriverForCaptain(Mob captain, Boat boat) {
        if (!(captain instanceof PirateCaptainEntity) || boat == null || !isValidBoat(boat)) {
            return false;
        }

        if (captain.getVehicle() == boat && boat.getControllingPassenger() == captain) {
            return true;
        }

        /*
         * Never kick out players.
         */
        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Player) {
                return false;
            }
        }

        Entity driver = boat.getControllingPassenger();

        if (driver == null && !boat.getPassengers().isEmpty()) {
            driver = boat.getPassengers().get(0);
        }

        if (driver == null || driver == captain) {
            return attemptBoard(captain, boat, false);
        }

        if (!(driver instanceof Mob oldDriver)) {
            return false;
        }

        /*
         * Temporarily remove the current crew so the captain can be inserted first.
         * First passenger is normally the controlling passenger, so this makes the
         * captain become the driver instead of merely joining the back of the list.
         */
        List<Entity> oldPassengers = new ArrayList<>(boat.getPassengers());
        List<Entity> passengersToRestore = new ArrayList<>();

        for (Entity passenger : oldPassengers) {
            if (passenger == captain) {
                continue;
            }

            passenger.stopRiding();

            if (passenger == oldDriver) {
                markDisplacedDriver(oldDriver);
                clearReservedBoat(oldDriver);
                continue;
            }

            passengersToRestore.add(passenger);
        }

        clearReservationForPirate(captain);

        boolean captainMounted = captain.startRiding(boat, true);

        if (!captainMounted) {
            /*
             * Best-effort recovery: put the non-displaced passengers back.
             */
            for (Entity passenger : passengersToRestore) {
                if (hasRawRoom(boat)) {
                    passenger.startRiding(boat, true);
                }
            }

            if (hasRawRoom(boat)) {
                oldDriver.startRiding(boat, true);
            }

            return false;
        }

        assignHomeBoat(captain, boat);
        makeCaptainShip(captain, boat);
        clearCaptainRescue(boat);

        for (Entity passenger : passengersToRestore) {
            if (hasRawRoom(boat)) {
                passenger.startRiding(boat, true);

                if (passenger instanceof Mob mob) {
                    assignHomeBoat(mob, boat);
                }
            }
        }

        return boat.getControllingPassenger() == captain;
    }

    public static boolean isDisplacedDriver(Mob pirate) {
        return pirate != null && pirate.getTags().contains(DISPLACED_DRIVER_TAG);
    }

    public static void clearDisplacedDriver(Mob pirate) {
        if (pirate != null) {
            pirate.removeTag(DISPLACED_DRIVER_TAG);
        }
    }

    public static void markDisplacedDriver(Mob pirate) {
        if (pirate != null) {
            pirate.addTag(DISPLACED_DRIVER_TAG);
        }
    }

    public static boolean isCaptainShip(Boat boat) {
        return boat != null && boat.getTags().contains(CAPTAIN_SHIP_TAG);
    }

    public static boolean isLootBoat(Boat boat) {
        if (boat instanceof ChestBoat) {
            return true;
        }

        if (boat == null) {
            return false;
        }

        for (String tag : boat.getTags()) {
            String lower = tag.toLowerCase();

            if (lower.contains("loot") || lower.contains("chest")) {
                return true;
            }
        }

        return false;
    }

    private static void makeCaptainShip(Mob captain, Boat boat) {
        if (captain == null || boat == null) {
            return;
        }

        String captainFleet = getFleetId(captain);

        List<Boat> nearbyBoats = captain.level().getEntitiesOfClass(
                Boat.class,
                captain.getBoundingBox().inflate(RESERVATION_CLEANUP_RANGE),
                otherBoat -> otherBoat != boat && isValidBoat(otherBoat)
        );

        for (Boat otherBoat : nearbyBoats) {
            if (captainFleet == null || sameFleet(captain, otherBoat)) {
                otherBoat.removeTag(CAPTAIN_SHIP_TAG);
            }
        }

        boat.addTag(CAPTAIN_SHIP_TAG);
        copyFleetTag(captain, boat);
    }

    private static List<Boat> getNearbyFleetBoats(Mob pirate, double range) {
        return pirate.level().getEntitiesOfClass(
                Boat.class,
                pirate.getBoundingBox().inflate(range),
                boat -> isValidBoat(boat) && sameFleetOrRaid(pirate, boat)
        );
    }

    private static boolean sameFleetOrRaid(Entity pirate, Boat boat) {
        if (sameFleet(pirate, boat)) {
            return true;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (sameFleet(pirate, passenger)) {
                return true;
            }
        }

        /*
         * Fallback for older spawned fleets that only had the generic raid tag.
         */
        return pirate.getTags().contains(RAID_PIRATE_TAG) && boat.getTags().contains(RAID_PIRATE_TAG);
    }

    private static boolean sameFleet(Entity first, Entity second) {
        String firstFleet = getFleetId(first);
        String secondFleet = getFleetId(second);

        return firstFleet != null && firstFleet.equals(secondFleet);
    }

    private static String getFleetId(Entity entity) {
        if (entity == null) {
            return null;
        }

        for (String tag : entity.getTags()) {
            if (tag.startsWith(FLEET_TAG_PREFIX)) {
                return tag.substring(FLEET_TAG_PREFIX.length());
            }
        }

        return null;
    }

    private static void copyFleetTag(Entity from, Entity to) {
        if (from == null || to == null) {
            return;
        }

        for (String tag : from.getTags()) {
            if (tag.startsWith(FLEET_TAG_PREFIX)) {
                removeTagsStartingWith(to, FLEET_TAG_PREFIX);
                to.addTag(tag);
                return;
            }
        }
    }

    private static void reserveSeat(Mob pirate, Boat boat) {
        boat.addTag(RESERVED_BY_TAG_PREFIX + pirate.getUUID());
    }

    private static void clearReservationForPirate(Mob pirate) {
        if (pirate == null) {
            return;
        }

        UUID pirateUuid = pirate.getUUID();

        if (pirate.level() == null) {
            return;
        }

        List<Boat> boats = pirate.level().getEntitiesOfClass(
                Boat.class,
                pirate.getBoundingBox().inflate(RESERVATION_CLEANUP_RANGE),
                PirateBoatPassengerHelper::isValidBoat
        );

        for (Boat boat : boats) {
            boat.removeTag(RESERVED_BY_TAG_PREFIX + pirateUuid);
        }
    }

    private static int getReservedSeatCount(Boat boat, UUID excludedPirate) {
        int count = 0;

        for (String tag : boat.getTags()) {
            if (!tag.startsWith(RESERVED_BY_TAG_PREFIX)) {
                continue;
            }

            UUID reservedUuid = parseUuid(tag.substring(RESERVED_BY_TAG_PREFIX.length()));

            if (reservedUuid == null) {
                continue;
            }

            if (excludedPirate != null && excludedPirate.equals(reservedUuid)) {
                continue;
            }

            count++;
        }

        return count;
    }

    private static void pruneStaleReservations(Boat boat) {
        if (boat == null || boat.level().isClientSide) {
            return;
        }

        List<String> tagsToRemove = new ArrayList<>();

        for (String tag : boat.getTags()) {
            if (!tag.startsWith(RESERVED_BY_TAG_PREFIX)) {
                continue;
            }

            UUID reservedUuid = parseUuid(tag.substring(RESERVED_BY_TAG_PREFIX.length()));

            if (reservedUuid == null) {
                tagsToRemove.add(tag);
                continue;
            }

            Entity reservedEntity = findEntityByUuid(boat.level(), reservedUuid, boat.position(), RESERVATION_CLEANUP_RANGE);

            if (!(reservedEntity instanceof Mob mob) || !mob.isAlive() || mob.isRemoved() || mob.isPassenger()) {
                tagsToRemove.add(tag);
            }
        }

        for (String tag : tagsToRemove) {
            boat.removeTag(tag);
        }
    }

    private static boolean hasRawRoom(Boat boat) {
        return boat.getPassengers().size() < getBoatSeatLimit(boat);
    }

    private static int getBoatSeatLimit(Boat boat) {
        /*
         * This uses reflection so it works with your custom sailboats even if
         * getMaxPassengers is protected or overridden by small/medium/large boats.
         */
        Class<?> type = boat.getClass();

        while (type != null) {
            try {
                Method method = type.getDeclaredMethod("getMaxPassengers");
                method.setAccessible(true);

                Object result = method.invoke(boat);

                if (result instanceof Integer seatLimit) {
                    return Math.max(1, seatLimit);
                }
            } catch (NoSuchMethodException ignored) {
                type = type.getSuperclass();
                continue;
            } catch (Exception ignored) {
                break;
            }
        }

        /*
         * Fallback guesses. Reflection above should normally catch your custom
         * passenger limit.
         */
        String className = boat.getClass().getSimpleName().toLowerCase();

        if (className.contains("large")) {
            return boat instanceof ChestBoat ? 2 : 3;
        }

        if (className.contains("medium")) {
            return boat instanceof ChestBoat ? 1 : 2;
        }

        if (className.contains("sail")) {
            return 1;
        }

        return 2;
    }

    private static UUID getHomeBoatUuid(Mob pirate) {
        for (String tag : pirate.getTags()) {
            if (!tag.startsWith(HOME_BOAT_TAG_PREFIX)) {
                continue;
            }

            return parseUuid(tag.substring(HOME_BOAT_TAG_PREFIX.length()));
        }

        return null;
    }

    private static void clearHomeBoatTag(Mob pirate) {
        removeTagsStartingWith(pirate, HOME_BOAT_TAG_PREFIX);
    }

    private static void removeTagsStartingWith(Entity entity, String prefix) {
        if (entity == null) {
            return;
        }

        List<String> tagsToRemove = new ArrayList<>();

        for (String tag : entity.getTags()) {
            if (tag.startsWith(prefix)) {
                tagsToRemove.add(tag);
            }
        }

        for (String tag : tagsToRemove) {
            entity.removeTag(tag);
        }
    }

    private static UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Entity findEntityByUuid(Level level, UUID uuid, net.minecraft.world.phys.Vec3 center, double range) {
        if (level == null || uuid == null) {
            return null;
        }

        if (level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid);

            if (entity != null) {
                return entity;
            }
        }

        if (center == null || range <= 0.0D) {
            return null;
        }

        AABB box = new AABB(
                center.x - range,
                center.y - range,
                center.z - range,
                center.x + range,
                center.y + range,
                center.z + range
        );

        List<Entity> entities = level.getEntities((Entity) null, box, entity -> entity.getUUID().equals(uuid));

        return entities.isEmpty() ? null : entities.get(0);
    }

    private static boolean isValidBoat(Boat boat) {
        return boat != null && boat.isAlive() && !boat.isRemoved();
    }
}