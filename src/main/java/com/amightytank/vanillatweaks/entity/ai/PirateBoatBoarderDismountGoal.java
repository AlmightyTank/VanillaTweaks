package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateBoatBoarderDismountGoal extends Goal {
    /*
     * Once the boat gets this close, boarders jump out and chase.
     */
    private static final double DISMOUNT_DISTANCE = 12.0D;
    private static final double WATER_TARGET_DISMOUNT_DISTANCE = 14.0D;
    private static final double BOAT_TARGET_DISMOUNT_DISTANCE = 16.0D;

    private static final double TARGET_SEARCH_DISTANCE = 80.0D;

    private final Mob pirate;
    private LivingEntity target;

    public PirateBoatBoarderDismountGoal(Mob pirate) {
        this.pirate = pirate;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.isRaidBoarder()) {
            return false;
        }

        if (!(this.pirate.getVehicle() instanceof ModBoatEntity)) {
            return false;
        }

        LivingEntity target = this.getOrFindTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return false;
        }

        /*
         * Important:
         * Boarders should not jump out for a player who already escaped inland.
         */
        if (!PirateRaidAiUtil.isTargetBoardable(this.pirate, target)) {
            return false;
        }

        double dismountDistance = this.getDismountDistance(target);

        if (this.pirate.distanceTo(target) > dismountDistance) {
            return false;
        }

        this.target = target;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        if (!AbstractPirateEntity.canPirateAttack(this.target)) {
            return;
        }

        if (!PirateRaidAiUtil.isTargetBoardable(this.pirate, this.target)) {
            return;
        }

        Entity vehicle = this.pirate.getVehicle();

        if (vehicle instanceof ModBoatEntity boat) {
            CompoundTag pirateData = this.pirate.getPersistentData();
            pirateData.putUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG, boat.getUUID());

            CompoundTag boatData = boat.getPersistentData();
            boatData.putUUID(PirateRaidAiUtil.TARGET_UUID_TAG, this.target.getUUID());
        }

        this.pirate.stopRiding();

        Vec3 towardTarget = this.target.position().subtract(this.pirate.position());

        if (towardTarget.lengthSqr() > 0.001D) {
            Vec3 push = towardTarget.normalize().scale(0.45D);

            this.pirate.setDeltaMovement(
                    this.pirate.getDeltaMovement().add(push.x, 0.15D, push.z)
            );

            this.pirate.hasImpulse = true;
        }

        PirateRaidAiUtil.clearNeedsRemount(this.pirate);

        this.pirate.setTarget(this.target);
        this.pirate.getNavigation().moveTo(this.target, 1.35D);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    private double getDismountDistance(LivingEntity target) {
        if (target.isPassenger()) {
            return BOAT_TARGET_DISMOUNT_DISTANCE;
        }

        if (target.isInWaterOrBubble()) {
            return WATER_TARGET_DISMOUNT_DISTANCE;
        }

        return DISMOUNT_DISTANCE;
    }

    private LivingEntity getOrFindTarget() {
        LivingEntity target = this.pirate.getTarget();

        if (AbstractPirateEntity.canPirateAttack(target)) {
            return target;
        }

        Player player = this.pirate.level().getNearestPlayer(
                this.pirate.getX(),
                this.pirate.getY(),
                this.pirate.getZ(),
                TARGET_SEARCH_DISTANCE,
                entity -> entity instanceof Player foundPlayer
                        && foundPlayer.isAlive()
                        && !foundPlayer.isCreative()
                        && !foundPlayer.isSpectator()
        );

        if (player != null) {
            this.pirate.setTarget(player);
            return player;
        }

        return null;
    }

    private boolean isRaidBoarder() {
        return this.pirate.isAlive()
                && this.pirate.getTags().contains(PirateRaidAiUtil.RAID_PIRATE_TAG)
                && this.pirate.getTags().contains(PirateRaidAiUtil.BOARDER_TAG);
    }
}