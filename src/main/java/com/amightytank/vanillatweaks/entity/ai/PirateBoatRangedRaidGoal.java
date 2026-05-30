package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class PirateBoatRangedRaidGoal extends Goal {
    private static final double RAID_TARGET_SEARCH_DISTANCE = 96.0D;
    private static final int TARGET_REFRESH_INTERVAL = 20;

    private static final double RANGED_MIN_DISTANCE = 20.0D;
    private static final double RANGED_MAX_DISTANCE = 36.0D;

    private static final double BOAT_SPEED = 0.045D;
    private static final double BOAT_MAX_SPEED = 0.38D;
    private static final float BOAT_TURN_SPEED = 8.0F;

    private final Mob pirate;
    private int targetRefreshTicks;

    public PirateBoatRangedRaidGoal(Mob pirate) {
        this.pirate = pirate;
    }

    @Override
    public boolean canUse() {
        if (!this.pirate.getTags().contains("PirateTreasureRaid")) {
            return false;
        }

        if (!this.isRanged()) {
            return false;
        }

        LivingEntity target = this.getOrFindTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        return this.pirate.isPassenger() && this.pirate.getVehicle() instanceof Boat;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.pirate.getTags().contains("PirateTreasureRaid")) {
            return false;
        }

        if (!this.isRanged()) {
            return false;
        }

        LivingEntity target = this.getOrFindTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        return this.pirate.isPassenger() && this.pirate.getVehicle() instanceof Boat;
    }

    @Override
    public void tick() {
        LivingEntity target = this.getOrFindTarget();

        if (target == null || !target.isAlive()) {
            return;
        }

        Entity vehicle = this.pirate.getVehicle();

        if (!(vehicle instanceof Boat boat)) {
            return;
        }

        if (this.isBoatController(boat)) {
            steerBoat(boat, target);
        }
    }

    private LivingEntity getOrFindTarget() {
        LivingEntity target = this.pirate.getTarget();

        if (target != null && target.isAlive()) {
            return target;
        }

        if (this.targetRefreshTicks > 0) {
            this.targetRefreshTicks--;
            return null;
        }

        this.targetRefreshTicks = TARGET_REFRESH_INTERVAL;

        Player player = this.pirate.level().getNearestPlayer(
                this.pirate.getX(),
                this.pirate.getY(),
                this.pirate.getZ(),
                RAID_TARGET_SEARCH_DISTANCE,
                entity -> entity instanceof Player foundPlayer
                        && foundPlayer.isAlive()
                        && !foundPlayer.isSpectator()
                        && !foundPlayer.isCreative()
        );

        if (player != null) {
            this.pirate.setTarget(player);
            return player;
        }

        return null;
    }

    private void steerBoat(Boat boat, LivingEntity target) {
        Vec3 boatPos = boat.position();
        Vec3 targetPos = target.position();

        double distance = boatPos.distanceTo(targetPos);

        if (distance < RANGED_MIN_DISTANCE) {
            moveBoatAwayFromTarget(boat, targetPos);
        } else if (distance > RANGED_MAX_DISTANCE) {
            moveBoatTowardTarget(boat, targetPos);
        } else {
            slowBoat(boat);
            faceBoatToward(boat, targetPos);
        }
    }

    private void moveBoatTowardTarget(Boat boat, Vec3 targetPos) {
        faceBoatToward(boat, targetPos);
        pushBoatForward(boat);
    }

    private void moveBoatAwayFromTarget(Boat boat, Vec3 targetPos) {
        Vec3 awayDirection = boat.position().subtract(targetPos);

        if (awayDirection.lengthSqr() < 0.001D) {
            awayDirection = new Vec3(boat.getLookAngle().x, 0.0D, boat.getLookAngle().z);
        }

        Vec3 awayTarget = awayDirection.normalize()
                .scale(8.0D)
                .add(boat.position());

        faceBoatToward(boat, awayTarget);
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
        if (boat instanceof ModBoatEntity modBoat) {
            modBoat.setPirateRaidRowing(true);
            return;
        }

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

    private void slowBoat(Boat boat) {
        if (boat instanceof ModBoatEntity modBoat) {
            modBoat.setPirateRaidRowing(false);
        } else {
            boat.setInput(false, false, false, false);
        }

        Vec3 motion = boat.getDeltaMovement();

        boat.setDeltaMovement(
                motion.x * 0.85D,
                motion.y,
                motion.z * 0.85D
        );
    }

    private boolean isBoatController(Boat boat) {
        return !boat.getPassengers().isEmpty() && boat.getPassengers().get(0) == this.pirate;
    }

    private boolean isRanged() {
        return this.pirate.getTags().contains("PirateRaidRanged");
    }
}