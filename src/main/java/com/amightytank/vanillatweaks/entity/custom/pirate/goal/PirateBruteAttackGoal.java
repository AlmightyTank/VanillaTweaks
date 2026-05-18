package com.amightytank.vanillatweaks.entity.custom.pirate.goal;

import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;

public class PirateBruteAttackGoal extends MeleeAttackGoal {
    private final PathfinderMob mob;

    public PirateBruteAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        ItemStack mainHand = this.mob.getMainHandItem();

        if (mainHand.is(ModItems.PIRATE_SPEAR.get())) {
            double spearReach = 3.5D;
            return spearReach * spearReach + target.getBbWidth();
        }

        double axeReach = 2.2D;
        return axeReach * axeReach + target.getBbWidth();
    }
}