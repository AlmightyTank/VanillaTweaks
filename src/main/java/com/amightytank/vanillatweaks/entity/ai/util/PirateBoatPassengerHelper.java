package com.amightytank.vanillatweaks.entity.ai.util;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

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
    private static final double CAPTAIN_SHIP_CLEAR_RANGE = 128.0D;

    private PirateBoatPassengerHelper() {
    }

    /*
     * Kept so old calls do not break.
     * The simplified system does not use queued mounts anymore.
     */
    public static void tickQueuedMounts(ServerLevel level) {
    }

    public static void tickQueuedMounts(Level level) {
    }

    /*
     * Simplified:
     * No home boat ownership. Just copy the fleet tag to the boat.
     */
    public static void assignHomeBoat(Mob pirate, Boat boat) {
        if (pirate == null || boat == null) {
            return;
        }

        copyFleetTag(pirate, boat);

        if (pirate instanceof PirateCaptainEntity) {
            makeCaptainShip(pirate, boat);
        }
    }

    /*
     * Kept so old calls do not break.
     */
    public static void clearReservedBoat(Mob pirate) {
        clearDisplacedDriver(pirate);
    }

    /*
     * Simplified:
     * Queueing now just tries to board immediately.
     */
    public static void queueBoard(Mob pirate, Boat boat) {
        queueBoard(pirate, boat, false);
    }

    public static void queueBoard(Mob pirate, Boat boat, boolean priority) {
        attemptBoard(pirate, boat, priority);
    }

    /*
     * Kept so old calls do not break.
     */
    public static boolean tryBoardQueuedNow(Mob pirate) {
        return false;
    }

    public static boolean attemptBoard(Mob pirate, Boat boat, boolean priority) {
        if (pirate == null || boat == null || !isValidBoat(boat)) {
            return false;
        }

        if (pirate.isPassenger()) {
            return pirate.getVehicle() == boat;
        }

        if (!hasAvailableReturnSeatFor(pirate, boat)) {
            return false;
        }

        boolean mounted = pirate.startRiding(boat, true);

        if (mounted) {
            assignHomeBoat(pirate, boat);
            clearDisplacedDriver(pirate);
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

        if (pirate != null && pirate.getVehicle() == boat) {
            return true;
        }

        return boat.getPassengers().size() < getBoatSeatLimit(boat);
    }

    /*
     * Simplified:
     * Home boat tracking is removed.
     */
    public static Boat getHomeBoat(Mob pirate) {
        if (pirate != null && pirate.getVehicle() instanceof Boat boat && isValidBoat(boat)) {
            return boat;
        }

        return null;
    }

    public static boolean isCaptainNeedingRescue(Mob pirate) {
        return pirate instanceof PirateCaptainEntity
                && pirate.isAlive()
                && !pirate.isRemoved()
                && !(pirate.getVehicle() instanceof Boat);
    }

    public static Boat findBestCaptainRescueBoat(Mob captain) {
        return findBestCaptainRescueBoat(captain, BOAT_SEARCH_RANGE);
    }

    public static Boat findBestCaptainRescueBoat(Mob captain, double range) {
        return findBestReturnBoat(captain, range);
    }

    public static Boat findBestReturnBoat(Mob pirate) {
        return findBestReturnBoat(pirate, BOAT_SEARCH_RANGE);
    }

    public static Boat findBestReturnBoat(Mob pirate, double range) {
        if (pirate == null || pirate.level() == null) {
            return null;
        }

        List<Boat> boats = getNearbyFleetBoats(pirate, range);

        return boats.stream()
                .filter(boat -> hasAvailableReturnSeatFor(pirate, boat))
                .min(Comparator.comparingDouble(boat -> {
                    double score = pirate.distanceToSqr(boat);

                    /*
                     * Prefer combat boats over chest/loot boats.
                     */
                    if (isLootBoat(boat)) {
                        score += 400.0D;
                    }

                    /*
                     * Captain ship is a little more important.
                     */
                    if (isCaptainShip(boat)) {
                        score -= 50.0D;
                    }

                    return score;
                }))
                .orElse(null);
    }

    /*
     * Kept so old calls do not break.
     * The simplified system does not make boats drive backward for rescue.
     */
    public static void requestCaptainRescue(Mob captain, Boat rescueBoat) {
        if (captain != null && rescueBoat != null) {
            assignHomeBoat(captain, rescueBoat);
        }
    }

    public static Mob getCaptainRescueTarget(Boat boat) {
        return null;
    }

    public static boolean hasCaptainRescueTarget(Boat boat) {
        return false;
    }

    public static void clearCaptainRescue(Boat boat) {
    }

    /*
     * Simplified:
     * No driver swapping. If the boat has room, the captain boards.
     * If the boat is full, he does not.
     */
    public static boolean swapDriverForCaptain(Mob captain, Boat boat) {
        return attemptBoard(captain, boat, false);
    }

    public static boolean isDisplacedDriver(Mob pirate) {
        return false;
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
        if (boat == null) {
            return false;
        }

        if (boat instanceof ChestBoat) {
            return true;
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
        if (captain == null || boat == null || captain.level() == null) {
            return;
        }

        List<Boat> nearbyBoats = captain.level().getEntitiesOfClass(
                Boat.class,
                captain.getBoundingBox().inflate(CAPTAIN_SHIP_CLEAR_RANGE),
                otherBoat -> otherBoat != boat && isValidBoat(otherBoat)
        );

        for (Boat otherBoat : nearbyBoats) {
            if (sameFleet(captain, otherBoat)) {
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
        if (pirate == null || boat == null) {
            return false;
        }

        if (sameFleet(pirate, boat)) {
            return true;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (sameFleet(pirate, passenger)) {
                return true;
            }
        }

        /*
         * Fallback for older fleets that only use the generic raid tag.
         */
        return pirate.getTags().contains(RAID_PIRATE_TAG)
                && boat.getTags().contains(RAID_PIRATE_TAG);
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

    private static void removeTagsStartingWith(Entity entity, String prefix) {
        if (entity == null) {
            return;
        }

        List<String> tagsToRemove = entity.getTags()
                .stream()
                .filter(tag -> tag.startsWith(prefix))
                .toList();

        for (String tag : tagsToRemove) {
            entity.removeTag(tag);
        }
    }

    private static int getBoatSeatLimit(Boat boat) {
        /*
         * Uses reflection so your custom sailboats can still override
         * getMaxPassengers.
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
         * Fallback guesses.
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

    public static Mob findBestPickupPirate(Boat boat, double range) {
        if (boat == null || boat.level() == null || !isValidBoat(boat)) {
            return null;
        }

        if (!hasAvailableReturnSeat(boat)) {
            return null;
        }

        return boat.level().getEntitiesOfClass(
                        Mob.class,
                        boat.getBoundingBox().inflate(range),
                        pirate -> canBoatPickUpPirate(boat, pirate)
                )
                .stream()
                .min(Comparator.comparingDouble(boat::distanceToSqr))
                .orElse(null);
    }

    private static boolean canBoatPickUpPirate(Boat boat, Mob pirate) {
        if (pirate == null || boat == null) {
            return false;
        }

        if (!pirate.isAlive() || pirate.isRemoved()) {
            return false;
        }

        if (pirate.isPassenger()) {
            return false;
        }

        if (!pirate.getTags().contains(RAID_PIRATE_TAG)) {
            return false;
        }

        if (!sameFleetOrRaid(pirate, boat)) {
            return false;
        }

        return hasAvailableReturnSeatFor(pirate, boat);
    }

    private static boolean isValidBoat(Boat boat) {
        return boat != null && boat.isAlive() && !boat.isRemoved();
    }
}