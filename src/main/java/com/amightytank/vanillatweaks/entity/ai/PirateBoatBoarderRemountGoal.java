package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class PirateBoatBoarderRemountGoal extends Goal {
    private static final String RAID_PIRATE_TAG = "PirateTreasureRaid";
    private static final String BOARDER_TAG = "PirateRaidBoarder";

    private static final String PIRATE_RAID_BOAT_TAG = "PirateRaidBoat";
    private static final String PIRATE_PATROL_BOAT_TAG = "PiratePatrolBoat";
    private static final String PIRATE_TREASURE_RAID_BOAT_TAG = "PirateTreasureRaidBoat";

    /*
     * If the target gets farther than this, boarders stop foot chasing and go back to the boat.
     */
    private static final double REMOUNT_WHEN_TARGET_FARTHER_THAN = 34.0D;

    private static final double BOAT_SEARCH_RANGE = 48.0D;
    private static final double BOARD_BOAT_DISTANCE = 2.6D;

    private final Mob pirate;

    private Boat targetBoat;
    private int repathCooldown;

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
        if (!this.isRaidBoarder()) {
            return false;
        }

        if (this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        /*
         * Do not remount while the player is still close enough for foot combat.
         */
        if (AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= REMOUNT_WHEN_TARGET_FARTHER_THAN * REMOUNT_WHEN_TARGET_FARTHER_THAN) {
            return false;
        }

        this.targetBoat = this.findReturnBoat();
        return this.targetBoat != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        if (this.targetBoat == null || !this.targetBoat.isAlive()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        /*
         * If the player comes back close, stop returning and let foot charge take over.
         */
        if (AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= REMOUNT_WHEN_TARGET_FARTHER_THAN * REMOUNT_WHEN_TARGET_FARTHER_THAN) {
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        this.repathCooldown = 0;
    }

    @Override
    public void stop() {
        this.targetBoat = null;
        this.pirate.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetBoat == null || !this.targetBoat.isAlive()) {
            return;
        }

        PirateLookHelper.lookAtEntity(this.pirate, this.targetBoat);

        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }

        double distanceSqr = this.pirate.distanceToSqr(this.targetBoat);

        if (distanceSqr <= BOARD_BOAT_DISTANCE * BOARD_BOAT_DISTANCE) {
            this.pirate.getNavigation().stop();
            this.pirate.startRiding(this.targetBoat, true);
            return;
        }

        if (this.repathCooldown <= 0) {
            this.pirate.getNavigation().moveTo(
                    this.targetBoat.getX(),
                    this.targetBoat.getY(),
                    this.targetBoat.getZ(),
                    1.2D
            );

            this.repathCooldown = 10;
        }
    }

    private Boat findReturnBoat() {
        AABB searchBox = this.pirate.getBoundingBox().inflate(BOAT_SEARCH_RANGE);

        List<Boat> boats = this.pirate.level().getEntitiesOfClass(
                Boat.class,
                searchBox,
                boat -> boat.isAlive() && this.isPirateBoat(boat)
        );

        return boats.stream()
                .min(Comparator.comparingDouble(boat -> boat.distanceToSqr(this.pirate)))
                .orElse(null);
    }

    private boolean isPirateBoat(Boat boat) {
        if (boat.getTags().contains(PIRATE_RAID_BOAT_TAG)
                || boat.getTags().contains(PIRATE_PATROL_BOAT_TAG)
                || boat.getTags().contains(PIRATE_TREASURE_RAID_BOAT_TAG)) {
            return true;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof AbstractPirateEntity) {
                return true;
            }
        }

        return false;
    }

    private boolean isRaidBoarder() {
        return this.pirate.getTags().contains(RAID_PIRATE_TAG)
                || this.pirate.getTags().contains(BOARDER_TAG);
    }
}