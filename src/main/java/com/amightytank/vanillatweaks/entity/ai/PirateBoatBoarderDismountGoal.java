package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.EnumSet;

public class PirateBoatBoarderDismountGoal extends Goal {
    private static final String RAID_PIRATE_TAG = "PirateTreasureRaid";
    private static final String BOARDER_TAG = "PirateRaidBoarder";

    /*
     * Saved on the boarder when it jumps out.
     * The remount goal and pilot goal use this so the boat does not abandon its own crew.
     */
    public static final String RETURN_BOAT_UUID_TAG = "PirateReturnBoatUUID";

    /*
     * Boarders only jump out when the boat gets close enough.
     */
    private static final double DISMOUNT_DISTANCE = 10.0D;

    private final Mob pirate;

    public PirateBoatBoarderDismountGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * LOOK makes them stare at the target before jumping out.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.isRaidBoarder()) {
            return false;
        }

        if (!this.pirate.isPassenger()) {
            return false;
        }

        Entity vehicle = this.pirate.getVehicle();
        if (!(vehicle instanceof Boat)) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();
        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return false;
        }

        return this.pirate.distanceToSqr(target) <= DISMOUNT_DISTANCE * DISMOUNT_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        LivingEntity target = this.pirate.getTarget();

        Entity vehicle = this.pirate.getVehicle();
        if (vehicle instanceof Boat boat) {
            /*
             * Remember the exact boat this pirate jumped from.
             * This prevents boarders from chasing the wrong pirate boat later.
             */
            this.pirate.getPersistentData().putUUID(RETURN_BOAT_UUID_TAG, boat.getUUID());
        }

        if (target != null && target.isAlive()) {
            PirateLookHelper.lookAtEntity(this.pirate, target);
        }

        this.pirate.stopRiding();

        if (target != null && target.isAlive()) {
            this.pirate.getNavigation().moveTo(target, 1.25D);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.pirate.getTarget();

        if (target != null && target.isAlive()) {
            PirateLookHelper.lookAtEntity(this.pirate, target);
        }
    }

    private boolean isRaidBoarder() {
        return this.pirate.getTags().contains(RAID_PIRATE_TAG)
                || this.pirate.getTags().contains(BOARDER_TAG);
    }
}