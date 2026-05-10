package com.amightytank.vanillatweaks.entity.custom;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

public class ModChestBoatEntity extends ChestBoat {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE =
            SynchedEntityData.defineId(ModChestBoatEntity.class, EntityDataSerializers.INT);

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
    public Item getDropItem() {
        return ModItems.getChestBoatItem(this.getModVariant()).get();
    }

    @Override
    protected int getMaxPassengers() {
        return switch (this.getModVariant().getBoatSize()) {
            case SAILBOAT -> 1;
            case MEDIUM_SAILBOAT -> 1;
            case LARGE_SAILBOAT -> 2;
        };
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        int index = this.getPassengers().indexOf(passenger);
        Vec3 seat = getSeatOffset(index);

        double riderY = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();

        Vec3 rotatedSeat = seat.yRot(-this.getYRot() * Mth.DEG_TO_RAD);

        callback.accept(
                passenger,
                this.getX() + rotatedSeat.x,
                riderY + seat.y,
                this.getZ() + rotatedSeat.z
        );
    }

    private Vec3 getSeatOffset(int index) {
        return switch (this.getModVariant().getBoatSize()) {
            case SAILBOAT -> new Vec3(0.0D, 0.0D, -0.25D);

            case MEDIUM_SAILBOAT -> new Vec3(0.0D, 0.0D, -0.55D);

            case LARGE_SAILBOAT -> switch (index) {
                case 0 -> new Vec3(-0.45D, 0.0D, -0.65D);
                case 1 -> new Vec3(0.45D, 0.0D, -0.65D);
                default -> Vec3.ZERO;
            };
        };
    }

    public void setVariant(ModBoatEntity.Type variant) {
        this.entityData.set(DATA_ID_TYPE, variant.ordinal());
        this.refreshDimensions();
    }

    public ModBoatEntity.Type getModVariant() {
        return ModBoatEntity.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, ModBoatEntity.Type.SAILBOAT.ordinal());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Type", this.getModVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Type", 8)) {
            this.setVariant(ModBoatEntity.Type.byName(tag.getString("Type")));
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return switch (this.getModVariant().getBoatSize()) {
            case SAILBOAT -> EntityDimensions.scalable(1.375F, 0.5625F);
            case MEDIUM_SAILBOAT -> EntityDimensions.scalable(1.8F, 0.65F);
            case LARGE_SAILBOAT -> EntityDimensions.scalable(2.35F, 0.75F);
        };
    }
}