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

    private static final int CAST_TIME = 30;
    private static final int EXTRA_SHARED_COOLDOWN_TICKS = 20 * 5;

    private int castTime;
    private int cooldown;

    private static final double PARROT_SWARM_RANGE = 48.0D;

    public CaptainParrotSwarmGoal(PirateCaptainEntity captain) {
        this.captain = captain;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.captain.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return false;
        }

        if (this.captain.isCaptainSpellOnCooldown()) {
            return false;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        return this.captain.distanceToSqr(target) <= PARROT_SWARM_RANGE * PARROT_SWARM_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.captain.getTarget();
        return this.castTime > 0 && AbstractPirateEntity.canPirateAttack(target);
    }

    @Override
    public void start() {
        this.castTime = CAST_TIME;

        if (!this.captain.level().isClientSide) {
            this.captain.setCaptainSpellCooldown(CAST_TIME + EXTRA_SHARED_COOLDOWN_TICKS);
        }

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