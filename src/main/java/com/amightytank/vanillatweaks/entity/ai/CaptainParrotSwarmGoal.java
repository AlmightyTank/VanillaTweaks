package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateParrotEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class CaptainParrotSwarmGoal extends Goal {
    private final PirateCaptainEntity captain;

    private int castTime;
    private int cooldown;

    public CaptainParrotSwarmGoal(PirateCaptainEntity captain) {
        this.captain = captain;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.captain.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return false;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        return this.captain.distanceToSqr(target) <= 32.0D * 32.0D;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.captain.getTarget();
        return this.castTime > 0 && AbstractPirateEntity.canPirateAttack(target);
    }

    @Override
    public void start() {
        this.castTime = 30;
        this.captain.getNavigation().stop();
        this.captain.setAggressive(true);
    }

    @Override
    public void stop() {
        this.castTime = 0;
        this.cooldown = 360;
        this.captain.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.captain.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            this.captain.setTarget(null);
            return;
        }

        this.captain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.castTime--;

        if (this.castTime == 15 && !this.captain.level().isClientSide) {
            this.summonParrots(target);
        }
    }

    private void summonParrots(LivingEntity target) {
        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        Level level = this.captain.level();

        for (int i = 0; i < 5; i++) {
            PirateParrotEntity parrot = ModEntities.PIRATE_PARROT.get().create(level);

            if (parrot == null) {
                continue;
            }

            double x = this.captain.getX() + this.captain.getRandom().nextDouble() * 4.0D - 2.0D;
            double y = this.captain.getY() + 1.5D + this.captain.getRandom().nextDouble();
            double z = this.captain.getZ() + this.captain.getRandom().nextDouble() * 4.0D - 2.0D;

            parrot.moveTo(x, y, z, this.captain.getRandom().nextFloat() * 360.0F, 0.0F);
            parrot.setOwner(this.captain);
            parrot.setTarget(target);
            parrot.setFromShoulder(false);

            level.addFreshEntity(parrot);
        }
    }
}