package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class PirateBoatBoarderRemountGoal extends Goal {
    /*
     * If the player gets farther than this, boarders regroup and go back to the boat.
     */
    private static final double REGROUP_START_DISTANCE = 34.0D;

    /*
     * If the player comes back close while they are regrouping,
     * cancel and fight on foot again, unless the player is still safely inland.
     */
    private static final double REGROUP_CANCEL_DISTANCE = 24.0D;

    private static final double BOAT_REACH_DISTANCE = 4.0D;
    private static final double TARGET_SEARCH_DISTANCE = 160.0D;
    private static final double BOAT_SEARCH_DISTANCE = 56.0D;

    private final Mob pirate;

    private ModBoatEntity targetBoat;
    private int retargetCooldown;
    private int repathCooldown;

    public PirateBoatBoarderRemountGoal(Mob pirate) {
        this.pirate = pirate;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.pirate.getVehicle() != null || this.pirate.isPassenger()) {
            return false;
        }

        if (!this.isRaidBoarder()) {
            return false;
        }

        LivingEntity target = this.getOrFindTarget();

        boolean needsRemount = PirateRaidAiUtil.needsRemount(this.pirate);
        boolean targetSafeOnLand = AbstractPirateEntity.canPirateAttack(target)
                && PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.pirate, target);

        boolean targetTooFar = AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceTo(target) > REGROUP_START_DISTANCE;

        /*
         * Start remounting when:
         * - another goal marked this pirate as needing remount
         * - target escaped safely inland
         * - target got too far away
         */
        if (!needsRemount && !targetSafeOnLand && !targetTooFar) {
            return false;
        }

        this.targetBoat = this.getSavedOrNearestRaidBoat();

        return this.targetBoat != null
                && this.targetBoat.isAlive()
                && this.hasOpenSeat(this.targetBoat);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.getVehicle() != null || this.pirate.isPassenger()) {
            return false;
        }

        if (!this.isRaidBoarder()) {
            return false;
        }

        if (this.targetBoat == null || !this.targetBoat.isAlive() || !this.hasOpenSeat(this.targetBoat)) {
            return false;
        }

        LivingEntity target = this.getOrFindTarget();

        if (AbstractPirateEntity.canPirateAttack(target)) {
            boolean targetSafeOnLand = PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.pirate, target);
            boolean targetCameBackClose = this.pirate.distanceTo(target) <= REGROUP_CANCEL_DISTANCE;

            /*
             * If the player comes back to the shore/water, stop remounting and fight again.
             * If they are still safely inland, keep remounting.
             */
            if (targetCameBackClose && !targetSafeOnLand && PirateRaidAiUtil.isTargetBoardable(this.pirate, target)) {
                PirateRaidAiUtil.clearNeedsRemount(this.pirate);
                return false;
            }
        }

        return true;
    }

    @Override
    public void start() {
        this.retargetCooldown = 0;
        this.repathCooldown = 0;
    }

    @Override
    public void stop() {
        this.pirate.getNavigation().stop();

        if (this.pirate.isPassenger()) {
            PirateRaidAiUtil.clearNeedsRemount(this.pirate);
        }

        this.targetBoat = null;
        this.repathCooldown = 0;
    }

    @Override
    public void tick() {
        if (this.targetBoat == null || !this.targetBoat.isAlive()) {
            this.targetBoat = this.getSavedOrNearestRaidBoat();

            if (this.targetBoat == null) {
                return;
            }
        }

        this.pirate.getLookControl().setLookAt(this.targetBoat, 30.0F, 30.0F);

        if (this.pirate.distanceTo(this.targetBoat) <= BOAT_REACH_DISTANCE && this.hasOpenSeat(this.targetBoat)) {
            this.mountBoat(this.targetBoat);

            LivingEntity target = this.getOrFindTarget();
            if (AbstractPirateEntity.canPirateAttack(target)) {
                this.pirate.setTarget(target);
            }

            PirateRaidAiUtil.clearNeedsRemount(this.pirate);
            return;
        }

        if (this.repathCooldown-- <= 0) {
            this.repathCooldown = 10;
            this.pirate.getNavigation().moveTo(this.targetBoat, 1.35D);
        }

        /*
         * Extra swim push so boarders do not get stuck bobbing in water.
         */
        if (this.pirate.isInWater()) {
            Vec3 toBoat = this.targetBoat.position().subtract(this.pirate.position());

            if (toBoat.lengthSqr() > 0.001D) {
                Vec3 push = toBoat.normalize().scale(0.08D);

                this.pirate.setDeltaMovement(
                        this.pirate.getDeltaMovement().add(push.x, 0.02D, push.z)
                );

                this.pirate.hasImpulse = true;
            }
        }
    }

    private void mountBoat(ModBoatEntity boat) {
        boat.addMobToSailboat(this.pirate);
    }

    private LivingEntity getOrFindTarget() {
        LivingEntity target = this.pirate.getTarget();

        if (target != null && target.isAlive()) {
            return target;
        }

        ModBoatEntity boat = this.getSavedRaidBoat();

        if (boat != null && boat.level() instanceof ServerLevel serverLevel) {
            CompoundTag boatData = boat.getPersistentData();

            if (boatData.hasUUID(PirateRaidAiUtil.TARGET_UUID_TAG)) {
                UUID targetId = boatData.getUUID(PirateRaidAiUtil.TARGET_UUID_TAG);
                Entity entity = serverLevel.getEntity(targetId);

                if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
                    this.pirate.setTarget(livingEntity);
                    return livingEntity;
                }
            }
        }

        if (this.retargetCooldown > 0) {
            this.retargetCooldown--;
            return null;
        }

        this.retargetCooldown = 20;

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

            ModBoatEntity savedBoat = this.getSavedOrNearestRaidBoat();
            if (savedBoat != null) {
                savedBoat.getPersistentData().putUUID(PirateRaidAiUtil.TARGET_UUID_TAG, player.getUUID());
            }

            return player;
        }

        return null;
    }

    private ModBoatEntity getSavedOrNearestRaidBoat() {
        ModBoatEntity savedBoat = this.getSavedRaidBoat();

        if (savedBoat != null && savedBoat.isAlive() && this.hasOpenSeat(savedBoat)) {
            return savedBoat;
        }

        return this.findNearestRaidBoat();
    }

    private ModBoatEntity getSavedRaidBoat() {
        if (!(this.pirate.level() instanceof ServerLevel level)) {
            return null;
        }

        CompoundTag data = this.pirate.getPersistentData();

        if (!data.hasUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG)) {
            return null;
        }

        UUID boatId = data.getUUID(PirateRaidAiUtil.RAID_BOAT_UUID_TAG);
        Entity entity = level.getEntity(boatId);

        if (entity instanceof ModBoatEntity boat) {
            return boat;
        }

        return null;
    }

    private ModBoatEntity findNearestRaidBoat() {
        List<ModBoatEntity> boats = this.pirate.level().getEntitiesOfClass(
                ModBoatEntity.class,
                this.pirate.getBoundingBox().inflate(BOAT_SEARCH_DISTANCE),
                boat -> boat.isAlive()
                        && PirateRaidAiUtil.isRaidBoat(boat)
                        && this.hasOpenSeat(boat)
        );

        return boats.stream()
                .min(Comparator.comparingDouble(this.pirate::distanceToSqr))
                .orElse(null);
    }

    private boolean hasOpenSeat(ModBoatEntity boat) {
        return boat != null && boat.hasOpenSailboatSeat();
    }

    private boolean isRaidBoarder() {
        return this.pirate.isAlive()
                && this.pirate.getTags().contains(PirateRaidAiUtil.RAID_PIRATE_TAG)
                && this.pirate.getTags().contains(PirateRaidAiUtil.BOARDER_TAG);
    }
}