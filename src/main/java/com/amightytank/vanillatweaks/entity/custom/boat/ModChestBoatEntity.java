package com.amightytank.vanillatweaks.entity.custom.boat;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ModChestBoatEntity extends ChestBoat {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE =
            SynchedEntityData.defineId(ModChestBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_BANNER_COUNT =
            SynchedEntityData.defineId(ModChestBoatEntity.class, EntityDataSerializers.INT);

    private boolean sailInputLeft;
    private boolean sailInputRight;
    private boolean sailInputForward;
    private boolean sailInputBack;

    public ModChestBoatEntity(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public ModChestBoatEntity(Level level, double x, double y, double z) {
        this(ModEntities.MOD_CHEST_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public void setInput(boolean left, boolean right, boolean forward, boolean back) {
        super.setInput(left, right, forward, back);

        this.sailInputLeft = left;
        this.sailInputRight = right;
        this.sailInputForward = forward;
        this.sailInputBack = back;
    }

    @Override
    public void tick() {
        float oldYRot = this.getYRot();

        super.tick();

        ModBoatEntity.applySailboatMovement(
                this,
                this.getModVariant().getBoatSize(),
                this.sailInputForward,
                this.getBannerCount(),
                oldYRot
        );
    }

    public int getBannerCount() {
        return this.entityData.get(DATA_BANNER_COUNT);
    }

    public void setBannerCount(int count) {
        this.entityData.set(DATA_BANNER_COUNT, ModBoatEntity.clampBannerCount(this.getModVariant(), count));
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return ModBoatEntity.getSailboatDimensions(this.getModVariant(), true);
    }

    @Override
    protected int getMaxPassengers() {
        return ModBoatEntity.getSailboatMaxPassengers(this.getModVariant(), true);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction callback) {
        ModBoatEntity.positionSailboatRider(
                this,
                passenger,
                callback,
                this.getModVariant(),
                true
        );
    }

    @Override
    public Item getDropItem() {
        return ModItems.getChestBoatItem(this.getModVariant()).get();
    }

    public void setVariant(ModBoatEntity.Type variant) {
        this.entityData.set(DATA_ID_TYPE, variant.ordinal());
        this.setBannerCount(this.getBannerCount());
        this.refreshDimensions();
    }

    public ModBoatEntity.Type getModVariant() {
        return ModBoatEntity.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, ModBoatEntity.Type.OAK_SAILBOAT.ordinal());
        this.entityData.define(DATA_BANNER_COUNT, 0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ModBoatEntity.saveSailboatData(tag, this.getModVariant(), this.getBannerCount());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.setVariant(ModBoatEntity.loadSailboatVariant(tag));

        if (tag.contains("BannerCount", 3)) {
            this.setBannerCount(tag.getInt("BannerCount"));
        }
    }
}