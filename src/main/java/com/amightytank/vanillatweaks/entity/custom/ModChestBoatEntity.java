package com.amightytank.vanillatweaks.entity.custom;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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

        this.applySailboatMovement(oldYRot);

        // Keeps the custom rectangular hitbox updated as the chest boat turns
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected AABB makeBoundingBox() {
        ModBoatEntity.BoatSize size = this.getModVariant().getBoatSize();

        float width = size.getHitboxWidth();      // left / right
        float length = size.getHitboxLength();    // front / back
        float height = size.getHitboxHeight();

        float yaw = this.getYRot() * Mth.DEG_TO_RAD;

        double sin = Math.abs(Mth.sin(yaw));
        double cos = Math.abs(Mth.cos(yaw));

        double halfWidth = width / 2.0D;
        double halfLength = length / 2.0D;

        double halfX = halfWidth * cos + halfLength * sin;
        double halfZ = halfWidth * sin + halfLength * cos;

        return new AABB(
                this.getX() - halfX,
                this.getY(),
                this.getZ() - halfZ,
                this.getX() + halfX,
                this.getY() + height,
                this.getZ() + halfZ
        );
    }

    private void applySailboatMovement(float oldYRot) {
        if (!this.isVehicle()) {
            return;
        }

        ModBoatEntity.BoatSize size = this.getModVariant().getBoatSize();

        float vanillaTurn = Mth.wrapDegrees(this.getYRot() - oldYRot);
        this.setYRot(oldYRot + vanillaTurn * size.getTurnScale());

        if (this.sailInputForward) {
            int rowers = this.getRowingPassengerCount();
            int banners = this.getBannerCount();

            float rowerBonus = 1.0F + Math.max(0, rowers - 1) * 0.25F;
            float bannerBonus = 1.0F + banners * 0.18F;

            float acceleration = size.getBaseAcceleration() * rowerBonus * bannerBonus;
            float yaw = this.getYRot() * Mth.DEG_TO_RAD;

            this.setDeltaMovement(this.getDeltaMovement().add(
                    Mth.sin(-yaw) * acceleration,
                    0.0D,
                    Mth.cos(yaw) * acceleration
            ));
        }

        this.limitTopSpeed(size);
    }

    private void limitTopSpeed(ModBoatEntity.BoatSize size) {
        Vec3 motion = this.getDeltaMovement();

        double horizontalSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        if (horizontalSpeed <= 0.0D) {
            return;
        }

        int rowers = this.getRowingPassengerCount();
        int banners = this.getBannerCount();

        double rowerBonus = 1.0D + Math.max(0, rowers - 1) * 0.15D;
        double bannerBonus = 1.0D + banners * 0.12D;

        double maxSpeed = size.getBaseTopSpeed() * rowerBonus * bannerBonus;

        if (horizontalSpeed > maxSpeed) {
            double scale = maxSpeed / horizontalSpeed;

            this.setDeltaMovement(
                    motion.x * scale,
                    motion.y,
                    motion.z * scale
            );
        }
    }

    private int getRowingPassengerCount() {
        int count = 0;

        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                count++;
            }
        }

        return Math.max(1, count);
    }

    public int getBannerCount() {
        return this.entityData.get(DATA_BANNER_COUNT);
    }

    public void setBannerCount(int count) {
        ModBoatEntity.BoatSize size = this.getModVariant().getBoatSize();
        int clamped = Math.max(0, Math.min(count, size.getMaxBanners()));
        this.entityData.set(DATA_BANNER_COUNT, clamped);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.getModVariant().getBoatSize().getDimensions(true);
    }

    @Override
    protected int getMaxPassengers() {
        return this.getModVariant().getBoatSize().getMaxPassengers(true);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        int index = this.getPassengers().indexOf(passenger);
        Vec3 seat = this.getModVariant().getBoatSize().getSeatOffset(index, true);

        double riderY = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
        Vec3 rotatedSeat = seat.yRot(-this.getYRot() * Mth.DEG_TO_RAD);

        callback.accept(
                passenger,
                this.getX() + rotatedSeat.x,
                riderY + seat.y,
                this.getZ() + rotatedSeat.z
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
        this.setBoundingBox(this.makeBoundingBox());
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

        tag.putString("ModBoatType", this.getModVariant().getSerializedName());
        tag.putInt("BannerCount", this.getBannerCount());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("ModBoatType", 8)) {
            this.setVariant(ModBoatEntity.Type.byName(tag.getString("ModBoatType")));
        } else if (tag.contains("Type", 8)) {
            this.setVariant(ModBoatEntity.Type.byName(tag.getString("Type")));
        }

        if (tag.contains("BannerCount", 3)) {
            this.setBannerCount(tag.getInt("BannerCount"));
        }
    }
}