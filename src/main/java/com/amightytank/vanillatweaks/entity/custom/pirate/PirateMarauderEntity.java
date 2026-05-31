package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.entity.ai.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class PirateMarauderEntity extends AbstractPirateEntity implements RangedAttackMob {
    private static final float TRIDENT_BRUTE_CHANCE = 0.50F;

    public PirateMarauderEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);

        // Client/summon safety default. Spawn finalization will randomize it.
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new PirateBoatPilotGoal(this));
        this.goalSelector.addGoal(4, new PirateBoatBoarderRemountGoal(this));
        this.goalSelector.addGoal(5, new PirateBoatBoarderDismountGoal(this));
        this.goalSelector.addGoal(6, new PirateBoarderChargeGoal(this));
        this.goalSelector.addGoal(7, new PirateMarauderThrowWhileChargingGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            this.setAggressive(this.isValidBruteTarget(target));

            if (this.getMainHandItem().isEmpty()) {
                this.equipRandomBruteWeapon(this.getRandom());
            }
        }
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);

        this.equipRandomBruteWeapon(random);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);

        this.equipRandomBruteWeapon(level.getRandom());

        return data;
    }

    private void equipRandomBruteWeapon(RandomSource random) {
        if (random.nextFloat() < TRIDENT_BRUTE_CHANCE) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        }

        this.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
    }

    public boolean isValidBruteTarget(LivingEntity target) {
        return target != null
                && target.isAlive()
                && !AbstractPirateEntity.isPirateAlly(target);
    }

    private boolean isHoldingAxe() {
        ItemStack heldItem = this.getMainHandItem();

        return heldItem.is(Items.WOODEN_AXE)
                || heldItem.is(Items.STONE_AXE)
                || heldItem.is(Items.IRON_AXE)
                || heldItem.is(Items.GOLDEN_AXE)
                || heldItem.is(Items.DIAMOND_AXE)
                || heldItem.is(Items.NETHERITE_AXE);
    }

    private PirateThrownWeaponEntity.WeaponType getThrownWeaponType() {
        return this.isHoldingAxe()
                ? PirateThrownWeaponEntity.WeaponType.AXE
                : PirateThrownWeaponEntity.WeaponType.TRIDENT;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!AbstractPirateEntity.canPirateAttack(target)) {
            return;
        }

        if (!this.hasLineOfSight(target)) {
            return;
        }

        this.throwWeaponAt(target);
    }

    public void throwWeaponAt(LivingEntity target) {
        if (this.level().isClientSide || !this.isValidBruteTarget(target)) {
            return;
        }

        PirateThrownWeaponEntity thrown = new PirateThrownWeaponEntity(
                ModEntities.PIRATE_THROWN_WEAPON.get(),
                this.level(),
                this
        );

        thrown.setPos(
                this.getX(),
                this.getEyeY() - 0.15D,
                this.getZ()
        );

        PirateThrownWeaponEntity.WeaponType weaponType = this.getThrownWeaponType();
        thrown.setWeaponType(weaponType);

        if (weaponType == PirateThrownWeaponEntity.WeaponType.AXE) {
            thrown.setBaseDamage(8.0D);
            thrown.setKnockback(1);
        } else {
            thrown.setBaseDamage(7.0D);
            thrown.setKnockback(1);
        }

        // Drowned-style aiming.
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        double dy = target.getY(0.3333333333333333D) - thrown.getY()
                + horizontalDistance * 0.20D;

        float velocity = 1.6F;
        float inaccuracy = 14.0F - this.level().getDifficulty().getId() * 4.0F;

        thrown.shoot(
                dx,
                dy,
                dz,
                velocity,
                inaccuracy
        );

        this.level().addFreshEntity(thrown);

        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.DROWNED_SHOOT,
                SoundSource.HOSTILE,
                1.0F,
                1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F)
        );
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

    @Override
    public double getBoatPilotStopRange() {
        return 12.0D;
    }

    @Override
    public double getBoatPilotStartRange() {
        return 18.0D;
    }
}