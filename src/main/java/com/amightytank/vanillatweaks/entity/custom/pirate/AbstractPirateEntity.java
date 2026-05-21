package com.amightytank.vanillatweaks.entity.custom.pirate;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPirateEntity extends AbstractIllager {

    protected AbstractPirateEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Pirates can still fight back, but NOT against other pirates.
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // Normal pirate target.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static boolean isPirateAlly(@Nullable Entity entity) {
        return entity instanceof AbstractPirateEntity
                || entity instanceof PirateParrotEntity
                || entity instanceof KrakenTentacleEntity;
    }

    public static boolean canPirateAttack(@Nullable LivingEntity target) {
        return target != null && target.isAlive() && !isPirateAlly(target);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (isPirateAlly(target)) {
            return false;
        }

        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (isPirateAlly(target)) {
            super.setTarget(null);
            return;
        }

        super.setTarget(target);
    }


    @Override
    public boolean isAlliedTo(Entity entity) {
        if (isPirateAlly(entity)) {
            return true;
        }

        return super.isAlliedTo(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        Entity directEntity = source.getDirectEntity();

        // Blocks melee damage, arrows, dynamite arrows, thrown spears, etc.
        // as long as the owner/shooter is a pirate.
        if (isPirateAlly(attacker) || isPirateAlly(directEntity)) {
            return false;
        }

        return super.hurt(source, amount);
    }
}