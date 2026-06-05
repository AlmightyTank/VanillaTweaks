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
    private static final double BOARD_DISTANCE = 20.0D;
    private static final int REPATH_COOLDOWN_TICKS = 10;

    private final Mob pirate;

    private Boat targetBoat;
    private int repathCooldown;

    public PirateBoatBoarderRemountGoal(Mob pirate) {
        this.pirate = pirate;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.pirate.isAlive() || this.pirate.isPassenger()) {
            return false;
        }

        /*
         * Captains are allowed to reboard too.
         * No special rescue logic. They just find an open fleet boat.
         */
        if (!(this.pirate instanceof PirateCaptainEntity) && !isRaidBoarder()) {
            return false;
        }

        /*
         * Boarders stay on foot while the target is close.
         */
        LivingEntity target = this.pirate.getTarget();

        if (!(this.pirate instanceof PirateCaptainEntity)
                && AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= MAX_FOOT_CHASE_DISTANCE * MAX_FOOT_CHASE_DISTANCE) {
            return false;
        }

        this.targetBoat = PirateBoatPassengerHelper.findBestReturnBoat(this.pirate, TARGET_BOAT_SEARCH_RANGE);
        return this.targetBoat != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.pirate.isAlive() || this.pirate.isPassenger()) {
            return false;
        }

        return this.targetBoat != null
                && this.targetBoat.isAlive()
                && !this.targetBoat.isRemoved()
                && PirateBoatPassengerHelper.hasAvailableReturnSeatFor(this.pirate, this.targetBoat);
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
            this.targetBoat = PirateBoatPassengerHelper.findBestReturnBoat(this.pirate, TARGET_BOAT_SEARCH_RANGE);
            return;
        }

        if (!PirateBoatPassengerHelper.hasAvailableReturnSeatFor(this.pirate, this.targetBoat)) {
            this.targetBoat = PirateBoatPassengerHelper.findBestReturnBoat(this.pirate, TARGET_BOAT_SEARCH_RANGE);
            return;
        }

        this.pirate.getLookControl().setLookAt(this.targetBoat, 30.0F, 30.0F);

        double distanceSqr = this.pirate.distanceToSqr(this.targetBoat);

        if (distanceSqr <= BOARD_DISTANCE * BOARD_DISTANCE) {
            if (PirateBoatPassengerHelper.attemptBoard(this.pirate, this.targetBoat, false)) {
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
         * Simple water push so they do not spin in the water forever.
         */
        if (this.pirate.isInWater()) {
            Vec3 direction = this.targetBoat.position().subtract(this.pirate.position());

            if (direction.lengthSqr() > 0.001D) {
                Vec3 push = direction.normalize().scale(0.045D);
                this.pirate.setDeltaMovement(this.pirate.getDeltaMovement().add(push));
            }
        }
    }

    private boolean isRaidBoarder() {
        return this.pirate.getTags().contains(PirateBoatPassengerHelper.RAID_PIRATE_TAG)
                && this.pirate.getTags().contains(PirateBoatPassengerHelper.BOARDER_TAG);
    }
}