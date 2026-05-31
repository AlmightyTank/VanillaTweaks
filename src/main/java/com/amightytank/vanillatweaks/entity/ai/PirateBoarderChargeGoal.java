package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class PirateBoarderChargeGoal extends Goal {
    private static final double MAX_FOOT_CHASE_DISTANCE = 34.0D;
    private static final double TARGET_SEARCH_DISTANCE = 80.0D;
    private static final double MELEE_REACH_DISTANCE = 2.4D;

    private final Mob pirate;

    private LivingEntity target;
    private int attackCooldown;
    private int repathCooldown;

    public PirateBoarderChargeGoal(Mob pirate) {
        this.pirate = pirate;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        if (!this.pirate.getTags().contains(PirateRaidAiUtil.BOARDER_TAG)) {
            return false;
        }

        LivingEntity foundTarget = this.pirate.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(foundTarget)) {
            foundTarget = findNearestValidPlayer();
        }

        if (!AbstractPirateEntity.canPirateAttack(foundTarget)) {
            return false;
        }

        /*
         * Boarders do not chase safely inland players.
         */
        if (PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.pirate, foundTarget)) {
            this.pirate.setTarget(null);
            PirateRaidAiUtil.markNeedsRemount(this.pirate);
            return false;
        }

        this.target = foundTarget;
        this.pirate.setTarget(foundTarget);

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.pirate.isPassenger()) {
            return false;
        }

        if (!AbstractPirateEntity.canPirateAttack(this.target)) {
            return false;
        }

        if (PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.pirate, this.target)) {
            this.pirate.setTarget(null);
            PirateRaidAiUtil.markNeedsRemount(this.pirate);
            return false;
        }

        if (this.pirate.distanceToSqr(this.target) > MAX_FOOT_CHASE_DISTANCE * MAX_FOOT_CHASE_DISTANCE) {
            PirateRaidAiUtil.markNeedsRemount(this.pirate);
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        this.attackCooldown = 0;
        this.repathCooldown = 0;

        if (this.target != null) {
            this.pirate.setTarget(this.target);
        }
    }

    @Override
    public void stop() {
        this.pirate.getNavigation().stop();

        if (!this.pirate.isPassenger()) {
            PirateRaidAiUtil.markNeedsRemount(this.pirate);
        }

        this.target = null;
        this.attackCooldown = 0;
        this.repathCooldown = 0;
    }

    @Override
    public void tick() {
        if (!AbstractPirateEntity.canPirateAttack(this.target)) {
            return;
        }

        if (PirateRaidAiUtil.isTargetSafeOnLandForBoarder(this.pirate, this.target)) {
            this.pirate.setTarget(null);
            PirateRaidAiUtil.markNeedsRemount(this.pirate);
            this.pirate.getNavigation().stop();
            return;
        }

        this.pirate.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }

        if (this.repathCooldown <= 0) {
            this.repathCooldown = 10;
            this.pirate.getNavigation().moveTo(this.target, 1.25D);
        }

        double distanceSqr = this.pirate.distanceToSqr(this.target);

        if (distanceSqr <= MELEE_REACH_DISTANCE * MELEE_REACH_DISTANCE && this.attackCooldown <= 0) {
            this.attackCooldown = 20;
            this.pirate.swing(InteractionHand.MAIN_HAND);
            this.pirate.doHurtTarget(this.target);
        }
    }

    private LivingEntity findNearestValidPlayer() {
        List<Player> players = this.pirate.level().getEntitiesOfClass(
                Player.class,
                this.pirate.getBoundingBox().inflate(TARGET_SEARCH_DISTANCE),
                AbstractPirateEntity::canPirateAttack
        );

        return players.stream()
                .min(Comparator.comparingDouble(this.pirate::distanceToSqr))
                .orElse(null);
    }
}