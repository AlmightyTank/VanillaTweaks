package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.ai.util.PirateLookHelper;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PirateCombatLookGoal extends Goal {
    private static final double LOOK_RANGE = 40.0D;

    private final Mob pirate;

    public PirateCombatLookGoal(Mob pirate) {
        this.pirate = pirate;

        /*
         * LOOK only.
         * This goal only controls where the pirate looks.
         */
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.pirate.getTarget();

        return AbstractPirateEntity.canPirateAttack(target)
                && this.pirate.distanceToSqr(target) <= LOOK_RANGE * LOOK_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = this.pirate.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        PirateLookHelper.lookAtEntity(this.pirate, target);
    }
}