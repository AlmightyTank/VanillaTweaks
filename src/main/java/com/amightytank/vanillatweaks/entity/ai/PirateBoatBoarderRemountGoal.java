package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateBoatPassengerHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateBoatBoarderRemountGoal extends Goal {
    private static final double MAX_FOOT_CHASE_DISTANCE = 34.0D;
    private static final double TARGET_BOAT_SEARCH_RANGE = 96.0D;
    private static final double BOARD_DISTANCE = 4.0D;
    private static final int REPATH_COOLDOWN_TICKS = 10;

    private final Mob pirate;

    private Boat targetBoat;
    private int repathCooldown;

    public PirateBoatBoarderRemountGoal(Mob pirate) {
        this.pirate = pirate;

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.pirate.isAlive() || this.pirate.isPassenger()) {
            return false;
        }

        /*
         * Captain rescue ignores normal boarder rules.
         * If the captain lost his ship, he must get another fleet boat.
         */
        if (this.pirate instanceof PirateCaptainEntity && PirateBoatPassengerHelper.isCaptainNeedingRescue(this.pirate)) {
            this.targetBoat = PirateBoatPassengerHelper.findBestCaptainRescueBoat(this.pirate, TARGET_BOAT_SEARCH_RANGE);

            if (this.targetBoat != null) {
                PirateBoatPassengerHelper.requestCaptainRescue(this.pirate, this.targetBoat);
                return true;
            }

            return false;
        }

        /*
         * Normal remounting only applies to raid boarders or displaced drivers.
         */
        if (!isRaidBoarder() && !PirateBoatPassengerHelper.isDisplacedDriver(this.pirate)) {
            return false;
        }

        /*
         * If the target is still close, keep fighting on foot.
         * If the target ran away, reboard and let the fleet chase again.
         */
        LivingEntity target = this.pirate.getTarget();

        if (AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= MAX_FOOT_CHASE_DISTANCE * MAX_FOOT_CHASE_DISTANCE
                && !PirateBoatPassengerHelper.isDisplacedDriver(this.pirate)) {
            return false;
        }

        this.targetBoat = PirateBoatPassengerHelper.findBestReturnBoat(this.pirate, TARGET_BOAT_SEARCH_RANGE);

        if (this.targetBoat == null) {
            return false;
        }

        PirateBoatPassengerHelper.queueBoard(this.pirate, this.targetBoat, false);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.pirate.isAlive() || this.pirate.isPassenger()) {
            return false;
        }

        if (this.targetBoat == null || !this.targetBoat.isAlive() || this.targetBoat.isRemoved()) {
            return false;
        }

        if (this.pirate instanceof PirateCaptainEntity) {
            return true;
        }

        return PirateBoatPassengerHelper.hasAvailableReturnSeatFor(this.pirate, this.targetBoat);
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
        if (this.targetBoat == null || !this.targetBoat.isAlive() || this.targetBoat.isRemoved()) {
            findNewTargetBoat();
            return;
        }

        this.pirate.getLookControl().setLookAt(this.targetBoat, 30.0F, 30.0F);

        double distanceSqr = this.pirate.distanceToSqr(this.targetBoat);

        if (distanceSqr <= BOARD_DISTANCE * BOARD_DISTANCE) {
            boolean priorityCaptain = this.pirate instanceof PirateCaptainEntity;

            PirateBoatPassengerHelper.queueBoard(this.pirate, this.targetBoat, priorityCaptain);

            if (PirateBoatPassengerHelper.tryBoardQueuedNow(this.pirate)) {
                this.pirate.getNavigation().stop();
            }

            return;
        }

        this.repathCooldown--;

        if (this.repathCooldown <= 0) {
            this.repathCooldown = REPATH_COOLDOWN_TICKS;
            this.pirate.getNavigation().moveTo(this.targetBoat, 1.15D);
        }

        /*
         * Helps pirates swim toward the rescue boat instead of just spinning
         * if normal ground navigation cannot path through water.
         */
        if (this.pirate.isInWater()) {
            Vec3 direction = this.targetBoat.position().subtract(this.pirate.position());

            if (direction.lengthSqr() > 0.001D) {
                Vec3 push = direction.normalize().scale(0.045D);
                this.pirate.setDeltaMovement(this.pirate.getDeltaMovement().add(push));
            }
        }
    }

    private void findNewTargetBoat() {
        if (this.pirate instanceof PirateCaptainEntity) {
            this.targetBoat = PirateBoatPassengerHelper.findBestCaptainRescueBoat(this.pirate, TARGET_BOAT_SEARCH_RANGE);

            if (this.targetBoat != null) {
                PirateBoatPassengerHelper.requestCaptainRescue(this.pirate, this.targetBoat);
            }

            return;
        }

        this.targetBoat = PirateBoatPassengerHelper.findBestReturnBoat(this.pirate, TARGET_BOAT_SEARCH_RANGE);

        if (this.targetBoat != null) {
            PirateBoatPassengerHelper.queueBoard(this.pirate, this.targetBoat, false);
        }
    }

    private boolean isRaidBoarder() {
        return this.pirate.getTags().contains(PirateBoatPassengerHelper.RAID_PIRATE_TAG)
                && this.pirate.getTags().contains(PirateBoatPassengerHelper.BOARDER_TAG);
    }
}