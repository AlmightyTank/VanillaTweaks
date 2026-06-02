package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ai.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.damagesource.DamageSource;

import javax.annotation.Nullable;

public class PirateDeckhandEntity extends AbstractPirateEntity {
    private static final float MAINHAND_DROP_CHANCE = 0.75F;

    public PirateDeckhandEntity(EntityType<? extends AbstractPirateEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.05D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(2, new PirateBoatPilotGoal(this)); // MOVE
        this.goalSelector.addGoal(3, new PirateCombatLookGoal(this)); // LOOK
        this.goalSelector.addGoal(4, new PirateBoatBoarderDismountGoal(this)); // ACTION
        this.goalSelector.addGoal(5, new PirateBoatBoarderRemountGoal(this)); // MOVE
        this.goalSelector.addGoal(6, new PirateBoarderChargeGoal(this)); // MOVE
        this.goalSelector.addGoal(7, new PirateMeleeAttackActionGoal(this)); // ACTION
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {

    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);

        ItemStack weapon;
        float roll = random.nextFloat();

        if (roll < 0.45F) {
            weapon = new ItemStack(Items.IRON_SWORD);
        } else if (roll < 0.70F) {
            weapon = new ItemStack(Items.STONE_SWORD);
        } else if (roll < 0.90F) {
            weapon = new ItemStack(Items.IRON_AXE);
        } else {
            weapon = new ItemStack(Items.WOODEN_SWORD);
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        this.setDropChance(EquipmentSlot.MAINHAND, MAINHAND_DROP_CHANCE);
    }

    @Override
    protected void populateDefaultEquipmentEnchantments(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentEnchantments(random, difficulty);
        this.setDropChance(EquipmentSlot.MAINHAND, MAINHAND_DROP_CHANCE);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);

        this.populateDefaultEquipmentSlots(this.random, difficulty);
        this.populateDefaultEquipmentEnchantments(this.random, difficulty);
        this.setDropChance(EquipmentSlot.MAINHAND, MAINHAND_DROP_CHANCE);

        return data;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(target);

        if (hurt && target instanceof net.minecraft.world.entity.LivingEntity livingTarget) {
            if (!this.level().isClientSide && this.random.nextFloat() < 0.10F) {
                livingTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0), this);
            }
        }

        return hurt;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }
}