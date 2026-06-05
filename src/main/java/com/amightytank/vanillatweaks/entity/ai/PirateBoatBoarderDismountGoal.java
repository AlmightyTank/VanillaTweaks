package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateBoatBoarderDismountGoal extends Goal {
    /*
     * This exact tag name must match what PirateBoatPassengerHelper / remount logic uses.
     */
    public static final String RETURN_BOAT_UUID_TAG = "PirateReturnBoatUUID";

    private static final double DISMOUNT_DISTANCE = 20.0D;
    private static final double DISMOUNT_CANCEL_DISTANCE = 24.0D;
    /*
     * Small delay before the boarder jumps out.
     */
    private static final int DISMOUNT_WINDUP_TICKS = 8;

    private final Mob pirate;

    private LivingEntity target;
    private Boat returnBoat;
    private int windupTicks;

    public PirateBoatBoarderDismountGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * Do NOT claim MOVE.
         *
         * PirateBoatPilotGoal owns MOVE while the pirate is riding.
         * This goal only handles looking/wind-up before the actual jump.
         */
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.isRaidBoarder()) {
            return false;
        }

        if (!this.pirate.isAlive() || !this.pirate.isPassenger()) {
            return false;
        }

        Entity vehicle = this.pirate.getVehicle();
        if (!(vehicle instanceof Boat boat)) {
            return false;
        }

        LivingEntity currentTarget = this.pirate.getTarget();
        if (!AbstractPirateEntity.canPirateAttack(currentTarget)) {
            return false;
        }

        if (this.pirate.distanceToSqr(currentTarget) > DISMOUNT_DISTANCE * DISMOUNT_DISTANCE) {
            return false;
        }

        this.target = currentTarget;
        this.returnBoat = boat;

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.isRaidBoarder()) {
            return false;
        }

        if (!this.pirate.isAlive() || !this.pirate.isPassenger()) {
            return false;
        }

        if (!(this.pirate.getVehicle() instanceof Boat)) {
            return false;
        }

        if (!AbstractPirateEntity.canPirateAttack(this.target)) {
            return false;
        }

        if (this.pirate.distanceToSqr(this.target) > DISMOUNT_CANCEL_DISTANCE * DISMOUNT_CANCEL_DISTANCE) {
            return false;
        }

        return this.windupTicks < DISMOUNT_WINDUP_TICKS;
    }

    @Override
    public void start() {
        this.windupTicks = 0;

        if (this.target != null && this.target.isAlive()) {
            PirateLookHelper.lookAtEntity(this.pirate, this.target);
        }
    }

    @Override
    public void tick() {
        if (this.target == null || !this.target.isAlive()) {
            return;
        }

        PirateLookHelper.lookAtEntity(this.pirate, this.target);

        this.windupTicks++;

        if (this.windupTicks >= DISMOUNT_WINDUP_TICKS) {
            this.jumpOut();
        }
    }

    @Override
    public void stop() {
        this.target = null;
        this.returnBoat = null;
        this.windupTicks = 0;
    }

    private void jumpOut() {
        if (!this.pirate.isPassenger()) {
            return;
        }

        Entity vehicle = this.pirate.getVehicle();

        if (vehicle instanceof Boat boat) {
            this.pirate.getPersistentData().putUUID(RETURN_BOAT_UUID_TAG, boat.getUUID());
        } else if (this.returnBoat != null) {
            this.pirate.getPersistentData().putUUID(RETURN_BOAT_UUID_TAG, this.returnBoat.getUUID());
        }

        this.pirate.stopRiding();

        if (this.target != null && this.target.isAlive()) {
            PirateLookHelper.lookAtEntity(this.pirate, this.target);

            Vec3 direction = this.target.position().subtract(this.pirate.position());

            if (direction.lengthSqr() > 0.0001D) {
                Vec3 normalized = direction.normalize();

                this.pirate.setDeltaMovement(
                        this.pirate.getDeltaMovement().add(
                                normalized.x * 0.18D,
                                0.18D,
                                normalized.z * 0.18D
                        )
                );
            }

            this.pirate.getNavigation().stop();
            this.pirate.getNavigation().moveTo(this.target, 1.25D);
        }
    }

    private boolean isRaidBoarder() {
        return this.pirate.getTags().contains(PirateBoatPassengerHelper.RAID_PIRATE_TAG)
                && this.pirate.getTags().contains(PirateBoatPassengerHelper.BOARDER_TAG);
    }
}