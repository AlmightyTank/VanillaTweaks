package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.boat.ModChestBoatEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class PirateBoatBoarderRaidGoal extends Goal {
    private static final String RAID_BOAT_UUID_TAG = "PirateRaidBoatUUID";

    private static final double BOARDER_DISMOUNT_DISTANCE = 11.0D;
    private static final double BOARDER_REMOUNT_DISTANCE = 30.0D;
    private static final double BOARDER_BOAT_REACH_DISTANCE = 4.0D;

    private static final double BOAT_SPEED = 0.045D;
    private static final double BOAT_MAX_SPEED = 0.38D;
    private static final float BOAT_TURN_SPEED = 8.0F;

    private final Mob pirate;

    public PirateBoatBoarderRaidGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * IMPORTANT:
         * Do not set MOVE or LOOK flags here.
         *
         * This goal steers the BOAT directly.
         * If we claim MOVE/LOOK, we block MeleeAttackGoal.
         */
    }

    @Override
    public boolean canUse() {
        if (!this.pirate.getTags().contains("PirateTreasureRaid")) {
            return false;
        }

        if (!this.isBoarder()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.pirate.isPassenger() && this.pirate.getVehicle() instanceof Boat) {
            return true;
        }

        return this.shouldTryToRemount(target);
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.pirate.getTags().contains("PirateTreasureRaid")) {
            return false;
        }

        if (!this.isBoarder()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        /*
         * Mounted boarders keep using this goal to steer the boat.
         */
        if (this.pirate.isPassenger() && this.pirate.getVehicle() instanceof Boat) {
            return true;
        }

        /*
         * Dismounted boarders only use this goal when they are too far
         * and need to return to their boat.
         */
        return this.shouldTryToRemount(target);
    }

    @Override
    public void tick() {
        LivingEntity target = this.pirate.getTarget();

        if (target == null || !target.isAlive()) {
            return;
        }

        if (this.pirate.isPassenger()) {
            Entity vehicle = this.pirate.getVehicle();

            if (!(vehicle instanceof Boat boat)) {
                return;
            }

            if (this.pirate.distanceTo(target) <= BOARDER_DISMOUNT_DISTANCE) {
                this.pirate.stopRiding();

                /*
                 * Push toward target once, then let MeleeAttackGoal take over.
                 */
                this.pirate.getNavigation().moveTo(target, 1.25D);
                return;
            }

            /*
             * Only the first passenger steers.
             * Other passengers can still attack/use abilities.
             */
            if (this.isBoatController(boat)) {
                steerBoatTowardTarget(boat, target);
            }

            return;
        }

        /*
         * Only happens when boarders are far from the player
         * and trying to return to the saved boat.
         */
        Boat boat = this.getSavedRaidBoat();

        if (boat == null || !boat.isAlive()) {
            return;
        }

        if (this.pirate.distanceTo(boat) <= BOARDER_BOAT_REACH_DISTANCE && this.hasOpenSeat(boat)) {
            this.pirate.startRiding(boat, true);
        } else {
            this.pirate.getNavigation().moveTo(boat, 1.25D);
        }
    }

    private void steerBoatTowardTarget(Boat boat, LivingEntity target) {
        moveBoatTowardTarget(boat, target.position());
    }

    private void moveBoatTowardTarget(Boat boat, Vec3 targetPos) {
        faceBoatToward(boat, targetPos);
        pushBoatForward(boat);
    }

    private void faceBoatToward(Boat boat, Vec3 targetPos) {
        Vec3 direction = targetPos.subtract(boat.position());

        if (direction.lengthSqr() < 0.001D) {
            return;
        }

        float wantedYaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float currentYaw = boat.getYRot();
        float newYaw = Mth.approachDegrees(currentYaw, wantedYaw, BOAT_TURN_SPEED);

        boat.setYRot(newYaw);
        boat.yRotO = newYaw;
    }

    private void rowBoatOars(Boat boat) {
        /*
         * Force vanilla boat rowing animation.
         */
        boat.setInput(true, true, true, false);
    }

    private void pushBoatForward(Boat boat) {
        rowBoatOars(boat);

        float yaw = boat.getYRot() * Mth.DEG_TO_RAD;

        Vec3 motion = boat.getDeltaMovement().add(
                Mth.sin(-yaw) * BOAT_SPEED,
                0.0D,
                Mth.cos(yaw) * BOAT_SPEED
        );

        double horizontalSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        if (horizontalSpeed > BOAT_MAX_SPEED) {
            double scale = BOAT_MAX_SPEED / horizontalSpeed;
            motion = new Vec3(motion.x * scale, motion.y, motion.z * scale);
        }

        boat.setDeltaMovement(motion);
        boat.hasImpulse = true;
    }

    private boolean isBoatController(Boat boat) {
        return !boat.getPassengers().isEmpty() && boat.getPassengers().get(0) == this.pirate;
    }

    private boolean shouldTryToRemount(LivingEntity target) {
        if (this.pirate.isPassenger()) {
            return false;
        }

        if (this.pirate.distanceTo(target) <= BOARDER_REMOUNT_DISTANCE) {
            return false;
        }

        Boat boat = this.getSavedRaidBoat();

        return boat != null && boat.isAlive() && this.hasOpenSeat(boat);
    }

    private Boat getSavedRaidBoat() {
        if (!(this.pirate.level() instanceof ServerLevel level)) {
            return null;
        }

        CompoundTag data = this.pirate.getPersistentData();

        if (!data.hasUUID(RAID_BOAT_UUID_TAG)) {
            return null;
        }

        UUID boatId = data.getUUID(RAID_BOAT_UUID_TAG);
        Entity entity = level.getEntity(boatId);

        if (entity instanceof Boat boat) {
            return boat;
        }

        return null;
    }

    private boolean hasOpenSeat(Boat boat) {
        int maxPassengers = 2;

        if (boat instanceof ModBoatEntity modBoat) {
            maxPassengers = ModBoatEntity.getSailboatMaxPassengers(
                    modBoat.getModVariant(),
                    false
            );
        } else if (boat instanceof ModChestBoatEntity modChestBoat) {
            maxPassengers = ModBoatEntity.getSailboatMaxPassengers(
                    modChestBoat.getModVariant(),
                    true
            );
        }

        return boat.getPassengers().size() < maxPassengers;
    }

    private boolean isBoarder() {
        return this.pirate.getTags().contains("PirateRaidBoarder");
    }
}