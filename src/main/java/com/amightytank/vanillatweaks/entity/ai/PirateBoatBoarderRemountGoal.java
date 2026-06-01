package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.ai.util.PirateRaidAiUtil;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class PirateBoatBoarderRemountGoal extends Goal {
    private static final String RAID_PIRATE_TAG = "PirateTreasureRaid";
    private static final String BOARDER_TAG = "PirateRaidBoarder";

    private static final String PIRATE_RAID_BOAT_TAG = "PirateRaidBoat";
    private static final String PIRATE_PATROL_BOAT_TAG = "PiratePatrolBoat";
    private static final String PIRATE_TREASURE_RAID_BOAT_TAG = "PirateTreasureRaidBoat";

    public static final String RETURN_BOAT_UUID_TAG = "PirateReturnBoatUUID";

    /*
     * If the target gets farther than this, boarders stop foot chasing and go back to the boat.
     */
    private static final double REMOUNT_WHEN_TARGET_FARTHER_THAN = 34.0D;

    /*
     * Even if the player is still close, force the boarder to go back if its boat is getting away.
     * This is what stops pirates from leaving their mates behind.
     */
    private static final double FORCE_REMOUNT_WHEN_BOAT_FARTHER_THAN = 14.0D;

    private static final double BOAT_SEARCH_RANGE = 56.0D;
    private static final double BOARD_BOAT_DISTANCE = 2.8D;

    /*
     * Do not mount the exact tick the pirate touches the boat.
     * This helps avoid passenger sync packets being sent too aggressively.
     */
    private static final int CLOSE_TICKS_BEFORE_MOUNT = 2;

    /*
     * If startRiding fails because the boat is full / invalid / syncing weirdly,
     * do not spam it every tick.
     */
    private static final int MOUNT_RETRY_COOLDOWN_TICKS = 10;

    private final Mob pirate;

    private Boat targetBoat;
    private int repathCooldown;
    private int closeBoatTicks;
    private int mountRetryCooldown;

    public PirateBoatBoarderRemountGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * MOVE = walk back to the boat.
         * LOOK = stare at the boat while returning.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.pirate.level().isClientSide) {
            return false;
        }

        if (!this.isRaidBoarder()) {
            return false;
        }

        if (this.pirate.isPassenger()) {
            return false;
        }

        this.targetBoat = this.findReturnBoat();

        if (!this.isValidBoat(this.targetBoat)) {
            this.targetBoat = null;
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        /*
         * Stay fighting only while:
         * - the target is still close
         * - AND the return boat has not started leaving.
         */
        if (this.shouldKeepFightingOnFoot(target)) {
            this.targetBoat = null;
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.level().isClientSide) {
            return false;
        }

        if (this.pirate.isPassenger()) {
            return false;
        }

        if (!this.isValidBoat(this.targetBoat)) {
            this.targetBoat = this.findReturnBoat();
            return this.isValidBoat(this.targetBoat);
        }

        LivingEntity target = this.pirate.getTarget();

        /*
         * If the player comes back close and the boat is still nearby,
         * stop returning and let foot charge take over.
         */
        return !this.shouldKeepFightingOnFoot(target);
    }

    @Override
    public void start() {
        this.repathCooldown = 0;
        this.closeBoatTicks = 0;
        this.mountRetryCooldown = 0;
    }

    @Override
    public void stop() {
        this.targetBoat = null;
        this.closeBoatTicks = 0;
        this.mountRetryCooldown = 0;
        this.pirate.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.pirate.level().isClientSide) {
            return;
        }

        if (!this.isValidBoat(this.targetBoat)) {
            this.targetBoat = this.findReturnBoat();

            if (!this.isValidBoat(this.targetBoat)) {
                return;
            }
        }

        PirateLookHelper.lookAtEntity(this.pirate, this.targetBoat);

        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }

        if (this.mountRetryCooldown > 0) {
            this.mountRetryCooldown--;
        }

        double distanceSqr = this.pirate.distanceToSqr(this.targetBoat);

        if (distanceSqr <= BOARD_BOAT_DISTANCE * BOARD_BOAT_DISTANCE) {
            this.pirate.getNavigation().stop();

            this.closeBoatTicks++;

            if (this.closeBoatTicks < CLOSE_TICKS_BEFORE_MOUNT) {
                return;
            }

            if (this.mountRetryCooldown > 0) {
                return;
            }

            this.mountRetryCooldown = MOUNT_RETRY_COOLDOWN_TICKS;

            if (PirateBoatPassengerHelper.tryBoard(this.pirate, this.targetBoat)) {
                //this.pirate.getPersistentData().remove(RETURN_BOAT_UUID_TAG);
            }

            return;
        }

        this.closeBoatTicks = 0;

        if (this.repathCooldown <= 0) {
            this.pirate.getNavigation().moveTo(
                    this.targetBoat.getX(),
                    this.targetBoat.getY(),
                    this.targetBoat.getZ(),
                    1.25D
            );

            this.repathCooldown = 8;
        }
    }

    private boolean shouldKeepFightingOnFoot(LivingEntity target) {
        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return false;
        }

        if (this.pirate.distanceToSqr(target) > REMOUNT_WHEN_TARGET_FARTHER_THAN * REMOUNT_WHEN_TARGET_FARTHER_THAN) {
            return false;
        }

        if (!this.isValidBoat(this.targetBoat)) {
            return false;
        }

        /*
         * If the boat is too far away, stop fighting and go back.
         */
        return this.pirate.distanceToSqr(this.targetBoat) <= FORCE_REMOUNT_WHEN_BOAT_FARTHER_THAN * FORCE_REMOUNT_WHEN_BOAT_FARTHER_THAN;
    }

    private Boat findReturnBoat() {
        Boat homeBoat = this.findHomeBoat();

        if (this.isValidBoat(homeBoat)
                && PirateBoatPassengerHelper.canBoardAssignedBoat(this.pirate, homeBoat)) {
            return homeBoat;
        }

        return null;
    }

    private Boat findHomeBoat() {
        if (!(this.pirate.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        UUID boatUuid = null;

        if (this.pirate.getPersistentData().hasUUID(PirateBoatPassengerHelper.HOME_BOAT_UUID_TAG)) {
            boatUuid = this.pirate.getPersistentData().getUUID(PirateBoatPassengerHelper.HOME_BOAT_UUID_TAG);
        } else if (this.pirate.getPersistentData().hasUUID(RETURN_BOAT_UUID_TAG)) {
            boatUuid = this.pirate.getPersistentData().getUUID(RETURN_BOAT_UUID_TAG);
        } else if (this.pirate.getPersistentData().hasUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG)) {
            boatUuid = this.pirate.getPersistentData().getUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG);
        }

        if (boatUuid == null) {
            return null;
        }

        Entity entity = serverLevel.getEntity(boatUuid);

        if (entity instanceof Boat boat && this.isValidBoat(boat)) {
            return boat;
        }

        return null;
    }

    private boolean isValidBoat(Boat boat) {
        return boat != null
                && boat.isAlive()
                && !boat.isRemoved()
                && boat.level() == this.pirate.level();
    }

    private boolean isRaidBoarder() {
        return this.pirate.getTags().contains(RAID_PIRATE_TAG)
                || this.pirate.getTags().contains(BOARDER_TAG);
    }
}