package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class PirateBoarderChargeGoal extends Goal {
    private static final String RAID_PIRATE_TAG = "PirateTreasureRaid";
    private static final String BOARDER_TAG = "PirateRaidBoarder";

    /*
     * While the player is inside this range, boarders stay on foot and fight.
     * If the player runs farther than this, this goal stops and the remount goal can take over.
     */
    private static final double MAX_FOOT_CHASE_DISTANCE = 34.0D;

    private static final double TARGET_SEARCH_DISTANCE = 80.0D;
    private static final double MELEE_REACH_DISTANCE = 2.4D;

    /*
     * Same default attack timing used by MeleeAttackGoal.
     */
    private static final int ATTACK_INTERVAL_TICKS = 20;

    private final Mob pirate;

    private int attackCooldown;
    private int repathCooldown;

    public PirateBoarderChargeGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * MOVE = run/chase.
         * LOOK = manually face the target instead of using LookAtPlayerGoal.
         */
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        if (!this.isRaidBoarder()) {
            return false;
        }

        LivingEntity target = this.getOrFindTarget();
        if (!canAttack(target)) {
            return false;
        }

        return this.pirate.distanceToSqr(target) <= MAX_FOOT_CHASE_DISTANCE * MAX_FOOT_CHASE_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        LivingEntity target = this.pirate.getTarget();
        if (!canAttack(target)) {
            return false;
        }

        /*
         * Stop this foot chase when the target gets too far away.
         * PirateBoatBoarderRemountGoal should then take over.
         */
        return this.pirate.distanceToSqr(target) <= MAX_FOOT_CHASE_DISTANCE * MAX_FOOT_CHASE_DISTANCE;
    }

    @Override
    public void start() {
        this.attackCooldown = 0;
        this.repathCooldown = 0;

        /*
         * This is the important MeleeAttackGoal behavior.
         * Illager-style models need aggressive=true to use the attacking arm pose.
         */
        this.pirate.setAggressive(true);
    }

    @Override
    public void stop() {
        this.pirate.getNavigation().stop();

        /*
         * Same cleanup idea as MeleeAttackGoal.
         * When the boarder stops charging, go back to normal/neutral pose.
         */
        this.pirate.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.pirate.getTarget();
        if (!canAttack(target)) {
            return;
        }

        /*
         * Keep aggressive true while ticking in case another goal/model state tries to reset it.
         */
        this.pirate.setAggressive(true);

        PirateLookHelper.lookAtEntity(this.pirate, target);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }

        double distanceSqr = this.pirate.distanceToSqr(target);

        if (distanceSqr <= MELEE_REACH_DISTANCE * MELEE_REACH_DISTANCE) {
            this.pirate.getNavigation().stop();
            this.performMeleeAttack(target);
            return;
        }

        if (this.repathCooldown <= 0) {
            this.pirate.getNavigation().moveTo(target, 1.25D);
            this.repathCooldown = 10;
        }
    }

    private void performMeleeAttack(LivingEntity target) {
        if (this.attackCooldown > 0) {
            return;
        }

        /*
         * Same visible attack call used by MeleeAttackGoal.
         * The second argument forces the animation packet to be sent.
         */
        this.pirate.swing(InteractionHand.MAIN_HAND, true);
        this.pirate.doHurtTarget(target);

        this.attackCooldown = ATTACK_INTERVAL_TICKS;
    }

    private LivingEntity getOrFindTarget() {
        LivingEntity target = this.pirate.getTarget();

        if (canAttack(target)) {
            return target;
        }

        Player nearestPlayer = this.pirate.level().getNearestPlayer(this.pirate, TARGET_SEARCH_DISTANCE);
        if (canAttack(nearestPlayer)) {
            this.pirate.setTarget(nearestPlayer);
            return nearestPlayer;
        }

        return null;
    }

    private boolean isRaidBoarder() {
        return this.pirate.getTags().contains(RAID_PIRATE_TAG)
                || this.pirate.getTags().contains(BOARDER_TAG);
    }

    private static boolean canAttack(LivingEntity target) {
        return AbstractPirateEntity.canPirateAttack(target);
    }
}