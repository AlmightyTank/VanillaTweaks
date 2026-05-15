package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.custom.pirate.goal.CaptainParrotSwarmGoal;
import com.amightytank.vanillatweaks.entity.custom.pirate.goal.CaptainSummonKrakenGoal;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class PirateCaptainEntity extends AbstractPirateEntity {

    public PirateCaptainEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new CaptainSummonKrakenGoal(this));
        this.goalSelector.addGoal(2, new CaptainParrotSwarmGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
    }

    @Override
    public void applyRaidBuffs(int wave, boolean unused) {
        if (wave > 3) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(70.0D);
            this.setHealth(this.getMaxHealth());
        }

        if (wave > 5) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8.0D);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerArmPose.SPELLCASTING;
        }

        return IllagerArmPose.CROSSED;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }
}