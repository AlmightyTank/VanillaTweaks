package com.amightytank.vanillatweaks.entity.custom.boat;

import com.amightytank.vanillatweaks.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class SailboatCollisionPartEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_PARENT_ID =
            SynchedEntityData.defineId(SailboatCollisionPartEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> DATA_PART_WIDTH =
            SynchedEntityData.defineId(SailboatCollisionPartEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> DATA_PART_HEIGHT =
            SynchedEntityData.defineId(SailboatCollisionPartEntity.class, EntityDataSerializers.FLOAT);

    @Nullable
    private ModBoatEntity parentBoat;

    public SailboatCollisionPartEntity(EntityType<? extends SailboatCollisionPartEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public SailboatCollisionPartEntity(Level level, ModBoatEntity parentBoat) {
        this(ModEntities.SAILBOAT_COLLISION_PART.get(), level);
        this.setParentBoat(parentBoat);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_PARENT_ID, -1);
        this.entityData.define(DATA_PART_WIDTH, 1.0F);
        this.entityData.define(DATA_PART_HEIGHT, 0.5F);
    }

    public void setParentBoat(ModBoatEntity parentBoat) {
        this.parentBoat = parentBoat;
        this.entityData.set(DATA_PARENT_ID, parentBoat.getId());
    }

    public boolean isParentBoat(ModBoatEntity boat) {
        return this.getParentBoat() == boat;
    }

    @Nullable
    public ModBoatEntity getParentBoat() {
        if (this.parentBoat != null && !this.parentBoat.isRemoved()) {
            return this.parentBoat;
        }

        int parentId = this.entityData.get(DATA_PARENT_ID);

        if (parentId < 0) {
            return null;
        }

        Entity entity = this.level().getEntity(parentId);

        if (entity instanceof ModBoatEntity boat && !boat.isRemoved()) {
            this.parentBoat = boat;
            return boat;
        }

        return null;
    }

    public void setPartSize(float width, float height) {
        boolean changed = false;

        if (Math.abs(this.entityData.get(DATA_PART_WIDTH) - width) > 0.001F) {
            this.entityData.set(DATA_PART_WIDTH, width);
            changed = true;
        }

        if (Math.abs(this.entityData.get(DATA_PART_HEIGHT) - height) > 0.001F) {
            this.entityData.set(DATA_PART_HEIGHT, height);
            changed = true;
        }

        if (changed) {
            this.refreshDimensions();
        }
    }

    private float getPartWidth() {
        return this.entityData.get(DATA_PART_WIDTH);
    }

    private float getPartHeight() {
        return this.entityData.get(DATA_PART_HEIGHT);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(this.getPartWidth(), this.getPartHeight());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (DATA_PART_WIDTH.equals(key) || DATA_PART_HEIGHT.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);

        if (!this.level().isClientSide) {
            ModBoatEntity parent = this.getParentBoat();

            if (parent == null || parent.isRemoved()) {
                this.discard();
            }
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isRemoved();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        ModBoatEntity parent = this.getParentBoat();

        if (entity == this || entity == parent) {
            return false;
        }

        if (parent != null && entity.isPassengerOfSameVehicle(parent)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}