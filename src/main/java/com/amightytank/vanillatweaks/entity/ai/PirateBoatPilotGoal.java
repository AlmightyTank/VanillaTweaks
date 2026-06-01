package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.ai.util.PirateRaidAiUtil;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class PirateBoatPilotGoal extends Goal {
    /*
     * This must match PirateBoatPassengerHelper.
     * Returning boarders use this so boats do not all wait for / chase the same pirate.
     */
    private static final String RESERVED_RETURN_BOAT_UUID_TAG = "PirateReservedReturnBoatUUID";

    private static final double BOARDING_STOP_RANGE = 9.0D;
    private static final double RANGED_STOP_RANGE = 20.0D;
    private static final double SAFE_LAND_HOLD_RANGE = 28.0D;

    /*
     * A boat with open seats will hold position if loose boarders from the same raid/patrol
     * are nearby and could still fill those seats.
     */
    private static final double CREW_WAIT_SEARCH_RANGE = 32.0D;

    private static final double TARGET_SEARCH_DISTANCE = 80.0D;

    private static final double WATER_CHECK_DISTANCE = 3.0D;
    private static final double FORWARD_WATER_CHECK_DISTANCE = 2.25D;

    private static final float STEER_DEAD_ZONE_DEGREES = 4.0F;
    private static final float FORWARD_MAX_TURN_DEGREES = 145.0F;

    private static final double MAX_BOAT_SPEED = 0.34D;
    private static final double BRAKE_SPEED_SQR = 0.0009D;

    private final AbstractPirateEntity pirate;

    private LivingEntity target;
    private int retargetCooldown;

    public PirateBoatPilotGoal(AbstractPirateEntity pirate) {
        this.pirate = pirate;

        /*
         * Do NOT claim LOOK.
         * Gunners/captains/marauders need LOOK for aiming and casting.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.pirate.level().isClientSide) {
            return false;
        }

        if (!(this.pirate.getVehicle() instanceof ModBoatEntity boat)) {
            return false;
        }

        if (!this.isPilotForBoat(boat)) {
            return false;
        }

        this.target = this.findTarget(boat);
        return this.target != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.level().isClientSide) {
            return false;
        }

        if (!(this.pirate.getVehicle() instanceof ModBoatEntity boat)) {
            return false;
        }

        if (!this.isPilotForBoat(boat)) {
            return false;
        }

        if (!this.isValidTarget(this.target)) {
            this.target = this.findTarget(boat);
        }

        return this.target != null;
    }

    @Override
    public void start() {
        this.retargetCooldown = 0;
    }

    @Override
    public void stop() {
        if (this.pirate.getVehicle() instanceof ModBoatEntity boat) {
            boat.clearPirateRaidInput();
        }

        this.target = null;
    }

    @Override
    public void tick() {
        if (!(this.pirate.getVehicle() instanceof ModBoatEntity boat)) {
            return;
        }

        if (!this.isPilotForBoat(boat)) {
            boat.clearPirateRaidInput();
            return;
        }

        if (this.retargetCooldown > 0) {
            this.retargetCooldown--;
        }

        if (this.retargetCooldown <= 0 || !this.isValidTarget(this.target)) {
            this.target = this.findTarget(boat);
            this.retargetCooldown = 20;
        }

        if (this.target == null) {
            boat.clearPirateRaidInput();
            this.slowBoat(boat);
            return;
        }

        this.shareTargetWithBoatCrew(boat, this.target);

        /*
         * Important fleet behavior:
         *
         * If this boat has an open seat, and there is a loose boarder from the same raid/patrol
         * nearby that is not reserved for a different boat, this boat waits.
         *
         * This prevents one open boat from trying to collect every loose boarder while the other
         * boats sail away missing crew.
         */
        if (this.shouldWaitForReturningCrew(boat)) {
            boat.clearPirateRaidInput();
            this.slowBoat(boat);
            return;
        }

        this.driveBoatTowardTarget(boat, this.target);
        this.capBoatSpeed(boat);
    }

    private boolean isPilotForBoat(ModBoatEntity boat) {
        LivingEntity controllingPassenger = boat.getControllingPassenger();

        /*
         * If a player is in the control seat, pirates should not fight the player's controls.
         */
        if (controllingPassenger instanceof Player) {
            return false;
        }

        /*
         * Normal case: ModBoatEntity returns the front pirate / selected pirate as controller.
         */
        if (controllingPassenger == this.pirate) {
            return true;
        }

        /*
         * If another mob is already considered controller, do not let this pirate also drive.
         */
        if (controllingPassenger instanceof Mob) {
            return false;
        }

        /*
         * Fallback if seat syncing temporarily fails.
         */
        return this.getBestPirateDriver(boat) == this.pirate;
    }

    private Mob getBestPirateDriver(ModBoatEntity boat) {
        Mob bestDriver = null;
        int bestPriority = -1;

        for (Entity passenger : boat.getPassengers()) {
            if (!(passenger instanceof Mob mob)) {
                continue;
            }

            if (!this.isPirateCrew(mob)) {
                continue;
            }

            int priority = this.getDriverPriority(mob);

            if (priority > bestPriority) {
                bestPriority = priority;
                bestDriver = mob;
            }
        }

        return bestDriver;
    }

    private int getDriverPriority(Mob mob) {
        if (mob.getTags().contains(PirateRaidAiUtil.CAPTAIN_TAG)) {
            return 4;
        }

        if (mob.getTags().contains(PirateRaidAiUtil.RANGED_TAG)) {
            return 3;
        }

        if (mob.getTags().contains(PirateRaidAiUtil.BOARDER_TAG)) {
            return 2;
        }

        if (mob.getTags().contains(PirateRaidAiUtil.RAID_PIRATE_TAG)) {
            return 1;
        }

        return 0;
    }

    private boolean shouldWaitForReturningCrew(ModBoatEntity boat) {
        if (PirateBoatPassengerHelper.getOpenSeatCount(boat) <= 0) {
            return false;
        }

        return this.findNearbyReturningBoarderForBoat(boat) != null;
    }

    private Mob findNearbyReturningBoarderForBoat(ModBoatEntity boat) {
        AABB searchBox = boat.getBoundingBox().inflate(CREW_WAIT_SEARCH_RANGE);

        List<Mob> nearbyBoarders = boat.level().getEntitiesOfClass(
                Mob.class,
                searchBox,
                mob -> this.canBoatWaitForBoarder(boat, mob)
        );

        Mob closest = null;
        double closestDistanceSqr = Double.MAX_VALUE;

        for (Mob boarder : nearbyBoarders) {
            double distanceSqr = boarder.distanceToSqr(boat);

            if (distanceSqr < closestDistanceSqr) {
                closestDistanceSqr = distanceSqr;
                closest = boarder;
            }
        }

        return closest;
    }

    private boolean canBoatWaitForBoarder(ModBoatEntity boat, Mob boarder) {
        if (boarder == null || !boarder.isAlive()) {
            return false;
        }

        if (boarder.isPassenger()) {
            return false;
        }

        if (!this.isReturningBoarder(boarder)) {
            return false;
        }

        if (!this.sharesRaidGroupTag(boat, boarder)) {
            return false;
        }

        /*
         * If the boarder already reserved another boat, this boat should not wait for it.
         * That is what stops one boat from acting like it owns all loose pirates.
         */
        if (boarder.getPersistentData().hasUUID(RESERVED_RETURN_BOAT_UUID_TAG)) {
            UUID reservedBoatUuid = boarder.getPersistentData().getUUID(RESERVED_RETURN_BOAT_UUID_TAG);
            return boat.getUUID().equals(reservedBoatUuid);
        }

        /*
         * No reservation yet. Any open boat in the same fleet can wait.
         * The remount goal will reserve an actual seat when the boarder chooses a boat.
         */
        return true;
    }

    private boolean isReturningBoarder(Mob mob) {
        return mob.getTags().contains(PirateRaidAiUtil.BOARDER_TAG)
                || mob.getTags().contains("PirateRaidBoarder");
    }

    private boolean sharesRaidGroupTag(Entity boat, Entity mob) {
        String groupPrefix = PirateRaidAiUtil.RAID_PIRATE_TAG + "_";
        boolean boatHasGroupTag = false;

        for (String tag : boat.getTags()) {
            if (!tag.startsWith(groupPrefix)) {
                continue;
            }

            boatHasGroupTag = true;

            if (mob.getTags().contains(tag)) {
                return true;
            }
        }

        /*
         * Older/spawned test boats may not have a group tag.
         * If no group tag exists, allow fallback behavior.
         */
        return !boatHasGroupTag;
    }

    private void driveBoatTowardTarget(ModBoatEntity boat, LivingEntity target) {
        Vec3 toTarget = target.position().subtract(boat.position());
        Vec3 horizontalToTarget = new Vec3(toTarget.x, 0.0D, toTarget.z);

        if (horizontalToTarget.lengthSqr() < 0.001D) {
            boat.clearPirateRaidInput();
            return;
        }

        double distance = horizontalToTarget.length();
        double stopRange = this.getBoatStopRange(boat, target);

        float wantedYaw = this.getYawToward(horizontalToTarget);
        float yawDifference = Mth.wrapDegrees(wantedYaw - boat.getYRot());

        boolean left = yawDifference < -STEER_DEAD_ZONE_DEGREES;
        boolean right = yawDifference > STEER_DEAD_ZONE_DEGREES;

        boolean forward = distance > stopRange
                && Math.abs(yawDifference) <= FORWARD_MAX_TURN_DEGREES
                && this.isWaterAhead(boat);

        boolean back = false;

        /*
         * If the boat gets too close, lightly reverse instead of ramming shore/target.
         */
        if (distance < Math.max(4.0D, stopRange * 0.65D)
                && this.isWaterBehind(boat)
                && this.getHorizontalSpeedSqr(boat) > BRAKE_SPEED_SQR) {
            forward = false;
            back = true;
        }

        boat.setPirateRaidInput(left, right, forward, back);
    }

    private double getBoatStopRange(ModBoatEntity boat, LivingEntity target) {
        if (this.boatHasBoarders(boat) && this.isTargetSafeOnLandForBoarder(target)) {
            return SAFE_LAND_HOLD_RANGE;
        }

        if (this.boatHasBoarders(boat)) {
            return BOARDING_STOP_RANGE;
        }

        return RANGED_STOP_RANGE;
    }

    private boolean isTargetSafeOnLandForBoarder(LivingEntity target) {
        if (target == null) {
            return false;
        }

        if (target.isInWaterOrBubble()) {
            return false;
        }

        return target.onGround();
    }

    private boolean boatHasBoarders(ModBoatEntity boat) {
        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Mob mob && mob.getTags().contains(PirateRaidAiUtil.BOARDER_TAG)) {
                return true;
            }
        }

        return false;
    }

    private boolean findTargetFromPirateTarget() {
        LivingEntity currentTarget = this.pirate.getTarget();

        if (this.isValidTarget(currentTarget)) {
            this.target = currentTarget;
            return true;
        }

        return false;
    }

    private LivingEntity findTarget(ModBoatEntity boat) {
        if (this.findTargetFromPirateTarget()) {
            return this.target;
        }

        LivingEntity savedTarget = this.findSavedTarget(boat);

        if (this.isValidTarget(savedTarget)) {
            this.pirate.setTarget(savedTarget);
            return savedTarget;
        }

        LivingEntity nearestPlayer = this.findNearestPlayerTarget(boat);

        if (nearestPlayer != null) {
            this.pirate.setTarget(nearestPlayer);
        }

        return nearestPlayer;
    }

    private LivingEntity findSavedTarget(ModBoatEntity boat) {
        if (!(boat.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        if (!boat.getPersistentData().hasUUID(PirateRaidAiUtil.TARGET_UUID_TAG)) {
            return null;
        }

        UUID targetUuid = boat.getPersistentData().getUUID(PirateRaidAiUtil.TARGET_UUID_TAG);
        Entity entity = serverLevel.getEntity(targetUuid);

        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        return null;
    }

    private LivingEntity findNearestPlayerTarget(ModBoatEntity boat) {
        AABB searchBox = boat.getBoundingBox().inflate(TARGET_SEARCH_DISTANCE);

        List<Player> players = boat.level().getEntitiesOfClass(
                Player.class,
                searchBox,
                player -> player.isAlive()
                        && !player.isCreative()
                        && !player.isSpectator()
        );

        Player closest = null;
        double closestDistanceSqr = Double.MAX_VALUE;

        for (Player player : players) {
            double distanceSqr = player.distanceToSqr(boat);

            if (distanceSqr < closestDistanceSqr) {
                closestDistanceSqr = distanceSqr;
                closest = player;
            }
        }

        return closest;
    }

    private boolean isValidTarget(LivingEntity possibleTarget) {
        return AbstractPirateEntity.canPirateAttack(possibleTarget);
    }

    private void shareTargetWithBoatCrew(ModBoatEntity boat, LivingEntity target) {
        if (!this.isValidTarget(target)) {
            return;
        }

        for (Entity passenger : boat.getPassengers()) {
            if (passenger instanceof Mob mob && this.isPirateCrew(mob)) {
                mob.setTarget(target);
            }
        }
    }

    private boolean isPirateCrew(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return false;
        }

        return mob instanceof AbstractPirateEntity
                || mob.getTags().contains(PirateRaidAiUtil.RAID_PIRATE_TAG);
    }

    private float getYawToward(Vec3 direction) {
        return (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
    }

    private boolean isWaterAhead(ModBoatEntity boat) {
        return this.isWaterInDirection(boat, boat.getYRot(), FORWARD_WATER_CHECK_DISTANCE)
                || this.isWaterInDirection(boat, boat.getYRot() - 25.0F, WATER_CHECK_DISTANCE)
                || this.isWaterInDirection(boat, boat.getYRot() + 25.0F, WATER_CHECK_DISTANCE);
    }

    private boolean isWaterBehind(ModBoatEntity boat) {
        return this.isWaterInDirection(boat, boat.getYRot() + 180.0F, FORWARD_WATER_CHECK_DISTANCE);
    }

    private boolean isWaterInDirection(ModBoatEntity boat, float yawDegrees, double distance) {
        float yawRadians = yawDegrees * Mth.DEG_TO_RAD;

        double x = boat.getX() + Mth.sin(-yawRadians) * distance;
        double z = boat.getZ() + Mth.cos(yawRadians) * distance;

        BlockPos pos = BlockPos.containing(x, boat.getY() - 0.15D, z);
        BlockPos below = pos.below();

        return boat.level().getFluidState(pos).is(FluidTags.WATER)
                || boat.level().getFluidState(below).is(FluidTags.WATER);
    }

    private void slowBoat(ModBoatEntity boat) {
        Vec3 movement = boat.getDeltaMovement();

        boat.setDeltaMovement(
                movement.x * 0.72D,
                movement.y,
                movement.z * 0.72D
        );
    }

    private void capBoatSpeed(ModBoatEntity boat) {
        Vec3 movement = boat.getDeltaMovement();

        double horizontalSpeedSqr = movement.x * movement.x + movement.z * movement.z;
        double maxSpeedSqr = MAX_BOAT_SPEED * MAX_BOAT_SPEED;

        if (horizontalSpeedSqr <= maxSpeedSqr) {
            return;
        }

        double horizontalSpeed = Math.sqrt(horizontalSpeedSqr);
        double scale = MAX_BOAT_SPEED / horizontalSpeed;

        boat.setDeltaMovement(
                movement.x * scale,
                movement.y,
                movement.z * scale
        );
    }

    private double getHorizontalSpeedSqr(ModBoatEntity boat) {
        Vec3 movement = boat.getDeltaMovement();
        return movement.x * movement.x + movement.z * movement.z;
    }
}