package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class CaptainSummonKrakenGoal extends Goal {
    private final PirateCaptainEntity captain;

    private int castTime;
    private int cooldown;

    public CaptainSummonKrakenGoal(PirateCaptainEntity captain) {
        this.captain = captain;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.captain.getTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        return this.captain.distanceToSqr(target) <= 24.0D * 24.0D;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.captain.getTarget();
        return this.castTime > 0 && target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.castTime = 40;
        this.captain.getNavigation().stop();
        this.captain.setAggressive(true);
    }

    @Override
    public void stop() {
        this.castTime = 0;
        this.cooldown = 180;
        this.captain.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.captain.getTarget();

        if (target == null) {
            return;
        }

        this.captain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.castTime--;

        if (this.castTime == 20 && !this.captain.level().isClientSide) {
            this.summonTentacles(target);
        }
    }

    private void summonTentacles(LivingEntity target) {
        Level level = this.captain.level();
        BlockPos center = target.blockPosition();

        spawnTentacle(level, center.offset(2, 0, 0));
        spawnTentacle(level, center.offset(-2, 0, 0));
        spawnTentacle(level, center.offset(0, 0, 2));
        spawnTentacle(level, center.offset(0, 0, -2));
        spawnTentacle(level, center.offset(2, 0, 2));
        spawnTentacle(level, center.offset(-2, 0, -2));
    }

    private void spawnTentacle(Level level, BlockPos pos) {
        KrakenTentacleEntity tentacle = ModEntities.KRAKEN_TENTACLE.get().create(level);

        if (tentacle == null) {
            return;
        }

        tentacle.moveTo(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                this.captain.getYRot(),
                0.0F
        );

        tentacle.setOwner(this.captain);
        level.addFreshEntity(tentacle);
    }
}