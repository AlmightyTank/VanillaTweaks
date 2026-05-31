package com.amightytank.vanillatweaks.entity.ai.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class PirateRaidAiUtil {
    private PirateRaidAiUtil() {
    }

    /*
     * Entity tags used by the pirate patrol/raid system.
     */
    public static final String RAID_PIRATE_TAG = "PirateTreasureRaid";
    public static final String RAID_BOAT_TAG = "PirateRaidBoat";
    public static final String BOARDER_TAG = "PirateRaidBoarder";
    public static final String RANGED_TAG = "PirateRaidRanged";
    public static final String CAPTAIN_TAG = "PirateRaidCaptain";
    public static final String RAID_BOAT_UUID_TAG = "PirateRaidBoatUUID";
    public static final String TARGET_UUID_TAG = "PirateRaidTargetUUID";

    /*
     * Persistent data keys.
     */
    public static final String NEEDS_REMOUNT_TAG = "PirateNeedsRemount";

    /*
     * Land safety:
     * If the player is standing on land and this far from water,
     * boarders should give up and reboard.
     */
    public static final double SAFE_LAND_DISTANCE_FROM_WATER = 8.0D;

    /*
     * Ranged pirates can still pressure land players from offshore.
     */
    public static final double RANGED_LAND_PRESSURE_DISTANCE = 64.0D;

    /*
     * How far we scan around a player to decide if they are safely inland.
     */
    public static final int SAFE_LAND_WATER_SCAN_RADIUS = 10;

    /*
     * Basic tag helpers.
     */
    public static boolean isRaidPirate(Entity entity) {
        return entity != null && entity.getTags().contains(RAID_PIRATE_TAG);
    }

    public static boolean isRaidBoat(Entity entity) {
        return entity != null && entity.getTags().contains(RAID_BOAT_TAG);
    }

    public static boolean isBoarder(Entity entity) {
        return entity != null && entity.getTags().contains(BOARDER_TAG);
    }

    public static boolean isRanged(Entity entity) {
        return entity != null && entity.getTags().contains(RANGED_TAG);
    }

    public static boolean isCaptain(Entity entity) {
        return entity != null && entity.getTags().contains(CAPTAIN_TAG);
    }

    public static void markNeedsRemount(Mob pirate) {
        if (pirate != null) {
            pirate.getPersistentData().putBoolean(NEEDS_REMOUNT_TAG, true);
        }
    }

    public static void clearNeedsRemount(Mob pirate) {
        if (pirate != null) {
            pirate.getPersistentData().putBoolean(NEEDS_REMOUNT_TAG, false);
        }
    }

    public static boolean needsRemount(Mob pirate) {
        return pirate != null && pirate.getPersistentData().getBoolean(NEEDS_REMOUNT_TAG);
    }

    /*
     * This should ONLY be used by boarders/melee land-chase logic.
     * Do not use this as a global pirate attack rule or gunners/captains will stop attacking inland players.
     */
    public static boolean isTargetSafeOnLandForBoarder(Mob pirate, LivingEntity target) {
        if (pirate == null || target == null || !target.isAlive()) {
            return false;
        }

        /*
         * Still dangerous:
         * - target is in water
         * - target is riding a boat/entity
         */
        if (target.isInWaterOrBubble() || target.isPassenger()) {
            return false;
        }

        /*
         * Do not call them safe if they are falling/jumping/etc.
         */
        if (!target.onGround()) {
            return false;
        }

        double waterDistance = distanceToNearestWater(
                target.level(),
                target.blockPosition(),
                SAFE_LAND_WATER_SCAN_RADIUS
        );

        return waterDistance >= SAFE_LAND_DISTANCE_FROM_WATER;
    }

    /*
     * This is for gunners/captains/ranged pirates.
     * They are allowed to keep attacking land players if close enough and visible.
     */
    public static boolean canRangedPressureTarget(Mob pirate, LivingEntity target) {
        if (pirate == null || target == null || !target.isAlive()) {
            return false;
        }

        if (pirate.distanceToSqr(target) > RANGED_LAND_PRESSURE_DISTANCE * RANGED_LAND_PRESSURE_DISTANCE) {
            return false;
        }

        return pirate.hasLineOfSight(target);
    }

    /*
     * Finds nearest water around a position.
     * Returns Double.MAX_VALUE if no water was found in the radius.
     */
    public static double distanceToNearestWater(Level level, BlockPos center, int radius) {
        if (level == null || center == null) {
            return Double.MAX_VALUE;
        }

        double closest = Double.MAX_VALUE;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 2; y++) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);

                    if (isWater(level, mutable)) {
                        double distSqr = x * x + y * y + z * z;

                        if (distSqr < closest) {
                            closest = distSqr;
                        }
                    }
                }
            }
        }

        if (closest == Double.MAX_VALUE) {
            return Double.MAX_VALUE;
        }

        return Math.sqrt(closest);
    }

    public static boolean isNearWater(Level level, BlockPos center, int radius) {
        return distanceToNearestWater(level, center, radius) != Double.MAX_VALUE;
    }

    public static boolean isWater(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }

        return level.getFluidState(pos).is(FluidTags.WATER);
    }

    public static boolean isWaterAtOrBelow(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }

        return isWater(level, pos) || isWater(level, pos.below());
    }

    public static boolean isWaterAround(Level level, BlockPos center, int radius) {
        if (level == null || center == null) {
            return false;
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -1; y <= 1; y++) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);

                    if (isWater(level, mutable)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /*
     * Used by boat pilot AI.
     * Checks whether the direction the boat wants to move is still water.
     */
    public static boolean isWaterAhead(Level level, Vec3 position, Vec3 direction, double distance) {
        if (level == null || position == null || direction == null) {
            return false;
        }

        if (direction.lengthSqr() < 0.0001D) {
            return true;
        }

        Vec3 checkPos = position.add(direction.normalize().scale(distance));
        BlockPos blockPos = BlockPos.containing(checkPos);

        return isWaterAtOrBelow(level, blockPos);
    }

    /*
     * Direction helpers.
     */
    public static Vec3 horizontalDirection(Vec3 from, Vec3 to) {
        if (from == null || to == null) {
            return Vec3.ZERO;
        }

        Vec3 direction = new Vec3(to.x - from.x, 0.0D, to.z - from.z);

        if (direction.lengthSqr() < 0.0001D) {
            return Vec3.ZERO;
        }

        return direction.normalize();
    }

    public static Vec3 horizontalAwayFrom(Vec3 threat, Vec3 current) {
        return horizontalDirection(threat, current);
    }

    public static Vec3 leftOf(Vec3 direction) {
        if (direction == null || direction.lengthSqr() < 0.0001D) {
            return Vec3.ZERO;
        }

        Vec3 normalized = direction.normalize();
        return new Vec3(-normalized.z, 0.0D, normalized.x);
    }

    public static Vec3 rightOf(Vec3 direction) {
        if (direction == null || direction.lengthSqr() < 0.0001D) {
            return Vec3.ZERO;
        }

        Vec3 normalized = direction.normalize();
        return new Vec3(normalized.z, 0.0D, -normalized.x);
    }

    /*
     * Boat helpers.
     */
    public static boolean isPirateBoat(Boat boat) {
        if (boat == null || !boat.isAlive()) {
            return false;
        }

        if (boat.getTags().contains(RAID_BOAT_TAG)) {
            return true;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (isRaidPirate(passenger)) {
                return true;
            }
        }

        return false;
    }

    public static boolean boatHasBoarders(Boat boat) {
        if (boat == null) {
            return false;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (isBoarder(passenger)) {
                return true;
            }
        }

        return false;
    }

    public static boolean boatHasRangedPirates(Boat boat) {
        if (boat == null) {
            return false;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (isRanged(passenger) || isCaptain(passenger)) {
                return true;
            }
        }

        return false;
    }

    public static int countRaidPiratesOnBoat(Boat boat) {
        if (boat == null) {
            return 0;
        }

        int count = 0;

        for (Entity passenger : boat.getPassengers()) {
            if (isRaidPirate(passenger)) {
                count++;
            }
        }

        return count;
    }

    public static int countBoardersOnBoat(Boat boat) {
        if (boat == null) {
            return 0;
        }

        int count = 0;

        for (Entity passenger : boat.getPassengers()) {
            if (isBoarder(passenger)) {
                count++;
            }
        }

        return count;
    }

    public static int countRangedPiratesOnBoat(Boat boat) {
        if (boat == null) {
            return 0;
        }

        int count = 0;

        for (Entity passenger : boat.getPassengers()) {
            if (isRanged(passenger) || isCaptain(passenger)) {
                count++;
            }
        }

        return count;
    }

    /*
     * Used by reboard goal.
     * We let the pirate try to board if it is a raid boat.
     * Your custom boat class should decide the real max passenger count.
     */
    public static boolean canTryBoardBoat(Mob pirate, Boat boat) {
        if (pirate == null || boat == null || !boat.isAlive()) {
            return false;
        }

        if (pirate.isPassenger()) {
            return false;
        }

        if (!isPirateBoat(boat)) {
            return false;
        }

        /*
         * Vanilla boats usually cap at 2 passengers, but your custom sailboats
         * may override the passenger rules. Returning true here lets startRiding()
         * try, and the boat/entity rules decide if it succeeds.
         */
        return true;
    }

    /*
     * True when a target is in water, on water, or close enough to shore
     * that boarders are allowed to engage.
     */
    public static boolean isTargetBoardable(Mob pirate, LivingEntity target) {
        if (pirate == null || target == null || !target.isAlive()) {
            return false;
        }

        if (target.isInWaterOrBubble() || target.isPassenger()) {
            return true;
        }

        return !isTargetSafeOnLandForBoarder(pirate, target);
    }
}