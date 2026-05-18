package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.custom.pirate.goal.PirateBruteLungeAttackGoal;
import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class PirateBruteEntity extends AbstractPirateEntity {
    private static final EntityDataAccessor<Integer> DATA_LUNGE_TICKS =
            SynchedEntityData.defineId(PirateBruteEntity.class, EntityDataSerializers.INT);

    private final WaterBoundPathNavigation waterNavigation;
    private final GroundPathNavigation groundNavigation;

    public PirateBruteEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;

        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LUNGE_TICKS, 0);
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {

    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        /*
         * AbstractPirateEntity already adds:
         * - FloatGoal
         * - WaterAvoidingRandomStrollGoal
         * - LookAtPlayerGoal
         * - RandomLookAroundGoal
         * - HurtByTargetGoal
         * - NearestAttackableTargetGoal<Player>
         *
         * So the brute only needs its special attack goal here.
         */
        this.goalSelector.addGoal(2, new PirateBruteLungeAttackGoal(this, 1.35D));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 36.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.45D);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();

            if (this.isInWaterOrBubble()) {
                if (this.navigation != this.waterNavigation) {
                    this.navigation = this.waterNavigation;
                }

                this.setSprinting(true);
                this.setMovementSpeedValue(0.36D);
            } else {
                if (this.navigation != this.groundNavigation) {
                    this.navigation = this.groundNavigation;
                }

                boolean chasing = target != null && target.isAlive();

                this.setSprinting(chasing);
                this.setMovementSpeedValue(chasing ? 0.34D : 0.27D);
            }
        }
    }

    private void setMovementSpeedValue(double value) {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speed != null) {
            speed.setBaseValue(value);
        }
    }

    public void setLungeTicks(int ticks) {
        this.entityData.set(DATA_LUNGE_TICKS, ticks);
    }

    public int getLungeTicks() {
        return this.entityData.get(DATA_LUNGE_TICKS);
    }

    public boolean isLunging() {
        return this.getLungeTicks() > 0;
    }

    public boolean isHoldingSpear() {
        return this.getMainHandItem().is(ModItems.PIRATE_SPEAR.get());
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);

        if (random.nextBoolean()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.PIRATE_SPEAR.get()));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnGroupData,
            @Nullable CompoundTag compoundTag
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, compoundTag);
        this.populateDefaultEquipmentSlots(this.random, difficulty);
        return data;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isAggressive() || this.isLunging()) {
            return AbstractIllager.IllagerArmPose.ATTACKING;
        }

        return AbstractIllager.IllagerArmPose.CROSSED;
    }
}