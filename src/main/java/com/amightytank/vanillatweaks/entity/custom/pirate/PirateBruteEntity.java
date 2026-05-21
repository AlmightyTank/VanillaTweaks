package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ai.PirateBruteAttackGoal;
import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PirateBruteEntity extends AbstractPirateEntity {
    public static final int ATTACK_NONE = 0;

    public static final int SPEAR_WINDUP = 1;
    public static final int SPEAR_LUNGE = 2;
    public static final int SPEAR_RECOVER = 3;

    public static final int AXE_WINDUP = 4;
    public static final int AXE_CHOP = 5;
    public static final int AXE_RECOVER = 6;

    private static final EntityDataAccessor<Integer> DATA_WEAPON_TYPE =
            SynchedEntityData.defineId(PirateBruteEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ATTACK_STATE =
            SynchedEntityData.defineId(PirateBruteEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ATTACK_TICK =
            SynchedEntityData.defineId(PirateBruteEntity.class, EntityDataSerializers.INT);

    public PirateBruteEntity(EntityType<? extends PirateBruteEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(2, new PirateBruteAttackGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WEAPON_TYPE, BruteWeaponType.SPEAR.getId());
        this.entityData.define(DATA_ATTACK_STATE, ATTACK_NONE);
        this.entityData.define(DATA_ATTACK_TICK, 0);
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {

    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);

        this.chooseRandomWeapon(level.getRandom());
        this.equipBruteWeapon();

        return data;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    private void chooseRandomWeapon(RandomSource random) {
        // 70% spear, 30% iron axe
        if (random.nextFloat() < 0.70F) {
            this.setBruteWeaponType(BruteWeaponType.SPEAR);
        } else {
            this.setBruteWeaponType(BruteWeaponType.AXE);
        }
    }

    public void equipBruteWeapon() {
        if (this.isSpearBrute()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.PIRATE_SPEAR.get()));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        }

        this.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
    }

    public BruteWeaponType getBruteWeaponType() {
        return BruteWeaponType.byId(this.entityData.get(DATA_WEAPON_TYPE));
    }

    public void setBruteWeaponType(BruteWeaponType type) {
        this.entityData.set(DATA_WEAPON_TYPE, type.getId());
    }

    public boolean isSpearBrute() {
        return this.getBruteWeaponType() == BruteWeaponType.SPEAR;
    }

    public boolean isAxeBrute() {
        return this.getBruteWeaponType() == BruteWeaponType.AXE;
    }

    public int getAttackState() {
        return this.entityData.get(DATA_ATTACK_STATE);
    }

    public void setAttackState(int state) {
        this.entityData.set(DATA_ATTACK_STATE, state);
    }

    public int getAttackTick() {
        return this.entityData.get(DATA_ATTACK_TICK);
    }

    public void setAttackTick(int tick) {
        this.entityData.set(DATA_ATTACK_TICK, tick);
    }

    public boolean isBruteAttacking() {
        return this.getAttackState() != ATTACK_NONE;
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        return AbstractIllager.IllagerArmPose.ATTACKING;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.96F;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.035F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("WeaponType", this.getBruteWeaponType().getId());
        tag.putInt("AttackState", this.getAttackState());
        tag.putInt("AttackTick", this.getAttackTick());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.setBruteWeaponType(BruteWeaponType.byId(tag.getInt("WeaponType")));
        this.setAttackState(tag.getInt("AttackState"));
        this.setAttackTick(tag.getInt("AttackTick"));

        this.equipBruteWeapon();
    }

    public enum BruteWeaponType {
        SPEAR(0),
        AXE(1);

        private final int id;

        BruteWeaponType(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static BruteWeaponType byId(int id) {
            for (BruteWeaponType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }

            return SPEAR;
        }
    }
}