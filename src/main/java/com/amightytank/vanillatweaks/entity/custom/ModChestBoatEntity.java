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
import net.minecraft.world.entity.player.Player;
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

    private boolean sailInputLeft;
    private boolean sailInputRight;
    private boolean sailInputForward;
    private boolean sailInputBack;

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
    }

    private void applySailboatMovement(float oldYRot) {
        if (!this.isVehicle()) {
            return;
        }

        ModBoatEntity.BoatSize size = this.getModVariant().getBoatSize();

        // Scale vanilla turning.
        // Vanilla already turned the boat during super.tick(), so we reduce/boost that turn here.
        float vanillaTurn = Mth.wrapDegrees(this.getYRot() - oldYRot);

        float turnScale = switch (size) {
            case SAILBOAT -> 1.35F;        // fast turn
            case MEDIUM_SAILBOAT -> 0.75F; // slower
            case LARGE_SAILBOAT -> 0.35F;  // heavy turn
        };

        this.setYRot(oldYRot + vanillaTurn * turnScale);

        // Forward speed tuning.
        if (this.sailInputForward) {
            int rowers = getRowingPassengerCount();
            int banners = getBannerCount();

            float acceleration = getBaseAcceleration(size);
            float rowerBonus = 1.0F + Math.max(0, rowers - 1) * 0.25F;
            float bannerBonus = 1.0F + banners * 0.18F;

            acceleration *= rowerBonus;
            acceleration *= bannerBonus;

            float yaw = this.getYRot() * Mth.DEG_TO_RAD;

            this.setDeltaMovement(this.getDeltaMovement().add(
                    Mth.sin(-yaw) * acceleration,
                    0.0D,
                    Mth.cos(yaw) * acceleration
            ));
        }

        this.limitTopSpeed(size);
    }

    private float getBaseAcceleration(ModBoatEntity.BoatSize size) {
        return switch (size) {
            case SAILBOAT -> 0.038F;
            case MEDIUM_SAILBOAT -> 0.027F;
            case LARGE_SAILBOAT -> 0.018F;
        };
    }

    private double getBaseTopSpeed(ModBoatEntity.BoatSize size) {
        return switch (size) {
            case SAILBOAT -> 0.30D;
            case MEDIUM_SAILBOAT -> 0.38D;
            case LARGE_SAILBOAT -> 0.47D;
        };
    }

    private void limitTopSpeed(ModBoatEntity.BoatSize size) {
        Vec3 motion = this.getDeltaMovement();

        double horizontalSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        if (horizontalSpeed <= 0.0D) {
            return;
        }

        int rowers = getRowingPassengerCount();
        int banners = getBannerCount();

        double rowerBonus = 1.0D + Math.max(0, rowers - 1) * 0.15D;
        double bannerBonus = 1.0D + banners * 0.12D;

        double maxSpeed = getBaseTopSpeed(size) * rowerBonus * bannerBonus;

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

    private int getBannerCount() {
        return switch (this.getModVariant().getBoatSize()) {
            case SAILBOAT -> 1;
            case MEDIUM_SAILBOAT -> 1;
            case LARGE_SAILBOAT -> 2; // temporary test value
        };
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return switch (this.getModVariant().getBoatSize()) {
            case SAILBOAT -> EntityDimensions.scalable(1.375F, 0.5625F);
            case MEDIUM_SAILBOAT -> EntityDimensions.scalable(1.8F, 0.65F);
            case LARGE_SAILBOAT -> EntityDimensions.scalable(2.35F, 0.75F);
        };
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
            case SAILBOAT -> new Vec3(0.0D, 0.0D, 0.0D);

            case MEDIUM_SAILBOAT -> switch (index) {
                case 0 -> new Vec3(0.0D, 0.0D, 0.45D);
                case 1 -> new Vec3(0.0D, 0.0D, -1.15D);
                default -> Vec3.ZERO;
            };

            case LARGE_SAILBOAT -> switch (index) {
                case 0 -> new Vec3(0.0D, 0.25D, 1.15D);
                case 1 -> new Vec3(0.0D, 0.25D, -0.15D);
                case 2 -> new Vec3(0.0D, 0.25D, -1.85D);
                default -> Vec3.ZERO;
            };
        };
    }

    @Override
    public Item getDropItem() {
        return ModItems.getChestBoatItem(this.getModVariant()).get();
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
}