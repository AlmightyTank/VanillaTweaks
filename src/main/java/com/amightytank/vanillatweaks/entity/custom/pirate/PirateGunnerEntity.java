package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.custom.pirate.goal.PirateGunnerAttackGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class PirateGunnerEntity extends AbstractPirateEntity implements RangedAttackMob {
    private static final float ARROW_SPEED = 1.7F;
    private static final float ARROW_INACCURACY = 7.0F;

    private static final EntityDataAccessor<Boolean> DATA_CHARGING_CROSSBOW =
            SynchedEntityData.defineId(PirateGunnerEntity.class, EntityDataSerializers.BOOLEAN);

    public PirateGunnerEntity(EntityType<? extends AbstractPirateEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CHARGING_CROSSBOW, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(2, new PirateGunnerAttackGoal(
                this,
                1.0D,
                35,
                18.0F
        ));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            boolean hasTarget = this.getTarget() != null && this.getTarget().isAlive();

            this.setAggressive(hasTarget);

            if (!hasTarget) {
                this.setChargingCrossbow(false);
            }
        }
    }

    @Override
    public void applyRaidBuffs(int wave, boolean unusedFalse) {

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 36.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.10D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnData,
            @Nullable net.minecraft.nbt.CompoundTag tag
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        return data;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    public void setChargingCrossbow(boolean chargingCrossbow) {
        this.entityData.set(DATA_CHARGING_CROSSBOW, chargingCrossbow);
    }

    public boolean isChargingCrossbow() {
        return this.entityData.get(DATA_CHARGING_CROSSBOW);
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
        }

        if (this.isAggressive() && this.getMainHandItem().is(Items.CROSSBOW)) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
        }

        return AbstractIllager.IllagerArmPose.NEUTRAL;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        Projectile projectile = this.createPirateProjectile();

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        double dy = target.getY(0.3333333333333333D) - projectile.getY()
                + horizontalDistance * 0.20D;

        projectile.shoot(
                dx,
                dy,
                dz,
                ARROW_SPEED,
                ARROW_INACCURACY
        );

        this.level().addFreshEntity(projectile);

        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.CROSSBOW_SHOOT,
                SoundSource.HOSTILE,
                1.0F,
                1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }

    private Projectile createPirateProjectile() {
        PirateArrowType arrowType = this.getRandomArrowType();

        if (arrowType == PirateArrowType.DYNAMITE) {
            return new PirateDynamiteArrowEntity(this.level(), this);
        }

        Arrow arrow = new Arrow(this.level(), this);
        arrow.setBaseDamage(4.0D);
        arrow.setShotFromCrossbow(true);

        if (arrowType == PirateArrowType.POISON) {
            arrow.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
        }

        if (arrowType == PirateArrowType.SLOWNESS) {
            arrow.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
        }

        return arrow;
    }

    private PirateArrowType getRandomArrowType() {
        int roll = this.getRandom().nextInt(100);

        if (roll < 60) {
            return PirateArrowType.NORMAL;
        }

        if (roll < 80) {
            return PirateArrowType.POISON;
        }

        if (roll < 94) {
            return PirateArrowType.SLOWNESS;
        }

        return PirateArrowType.DYNAMITE;
    }

    private enum PirateArrowType {
        NORMAL,
        POISON,
        SLOWNESS,
        DYNAMITE
    }
}