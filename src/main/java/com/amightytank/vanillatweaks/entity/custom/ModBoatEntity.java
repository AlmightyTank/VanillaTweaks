package com.amightytank.vanillatweaks.entity.custom;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.IntFunction;

public class ModBoatEntity extends Boat {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_BANNER_COUNT =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private boolean sailInputLeft;
    private boolean sailInputRight;
    private boolean sailInputForward;
    private boolean sailInputBack;

    public ModBoatEntity(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public ModBoatEntity(Level level, double x, double y, double z) {
        this(ModEntities.MOD_BOAT.get(), level);
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

        // Keeps the custom rectangular hitbox updated as the boat turns
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected AABB makeBoundingBox() {
        BoatSize size = this.getModVariant().getBoatSize();

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

        BoatSize size = this.getModVariant().getBoatSize();

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

    private void limitTopSpeed(BoatSize size) {
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
        BoatSize size = this.getModVariant().getBoatSize();
        int clamped = Math.max(0, Math.min(count, size.getMaxBanners()));
        this.entityData.set(DATA_BANNER_COUNT, clamped);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.getModVariant().getBoatSize().getDimensions(false);
    }

    @Override
    protected int getMaxPassengers() {
        return this.getModVariant().getBoatSize().getMaxPassengers(false);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        int index = this.getPassengers().indexOf(passenger);
        Vec3 seat = this.getModVariant().getBoatSize().getSeatOffset(index, false);

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
        return ModItems.getBoatItem(this.getModVariant()).get();
    }

    public void setVariant(Type variant) {
        this.entityData.set(DATA_ID_TYPE, variant.ordinal());
        this.setBannerCount(this.getBannerCount());
        this.refreshDimensions();
        this.setBoundingBox(this.makeBoundingBox());
    }

    public Type getModVariant() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, Type.OAK_SAILBOAT.ordinal());
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
            this.setVariant(Type.byName(tag.getString("ModBoatType")));
        } else if (tag.contains("Type", 8)) {
            this.setVariant(Type.byName(tag.getString("Type")));
        }

        if (tag.contains("BannerCount", 3)) {
            this.setBannerCount(tag.getInt("BannerCount"));
        }
    }

    public enum BoatSize implements StringRepresentable {
        SAILBOAT("sailboat", 1.375F, 1.60F, 0.35F, 1, 1, 1.35F, 0.020F, 0.36D, 0),
        MEDIUM_SAILBOAT("medium_sailboat", 1.55F, 2.85F, 0.40F, 2, 1, 0.75F, 0.014F, 0.42D, 0),
        LARGE_SAILBOAT("large_sailboat", 1.85F, 4.35F, 0.45F, 3, 2, 0.35F, 0.009F, 0.48D, 2);

        private final String name;
        private final float hitboxWidth;
        private final float hitboxLength;
        private final float hitboxHeight;
        private final int maxPassengers;
        private final int maxChestPassengers;
        private final float turnScale;
        private final float baseAcceleration;
        private final double baseTopSpeed;
        private final int maxBanners;

        BoatSize(String name, float hitboxWidth, float hitboxLength, float hitboxHeight,
                 int maxPassengers, int maxChestPassengers,
                 float turnScale, float baseAcceleration, double baseTopSpeed, int maxBanners) {
            this.name = name;
            this.hitboxWidth = hitboxWidth;
            this.hitboxLength = hitboxLength;
            this.hitboxHeight = hitboxHeight;
            this.maxPassengers = maxPassengers;
            this.maxChestPassengers = maxChestPassengers;
            this.turnScale = turnScale;
            this.baseAcceleration = baseAcceleration;
            this.baseTopSpeed = baseTopSpeed;
            this.maxBanners = maxBanners;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public float getHitboxWidth() {
            return this.hitboxWidth;
        }

        public float getHitboxLength() {
            return this.hitboxLength;
        }

        public float getHitboxHeight() {
            return this.hitboxHeight;
        }

        public EntityDimensions getDimensions(boolean chestBoat) {
            return EntityDimensions.scalable(this.hitboxWidth, this.hitboxHeight);
        }

        public int getMaxPassengers(boolean chestBoat) {
            return chestBoat ? this.maxChestPassengers : this.maxPassengers;
        }

        public float getTurnScale() {
            return this.turnScale;
        }

        public float getBaseAcceleration() {
            return this.baseAcceleration;
        }

        public double getBaseTopSpeed() {
            return this.baseTopSpeed;
        }

        public int getMaxBanners() {
            return this.maxBanners;
        }

        public Vec3 getSeatOffset(int index, boolean chestBoat) {
            if (chestBoat) {
                return switch (this) {
                    case SAILBOAT -> new Vec3(0.0D, 0.0D, -0.25D);
                    case MEDIUM_SAILBOAT -> new Vec3(0.0D, 0.0D, -0.55D);
                    case LARGE_SAILBOAT -> switch (index) {
                        case 0 -> new Vec3(-0.45D, 0.0D, -0.65D);
                        case 1 -> new Vec3(0.45D, 0.0D, -0.65D);
                        default -> Vec3.ZERO;
                    };
                };
            }

            return switch (this) {
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
    }

    public enum WoodKind implements StringRepresentable {
        OAK("oak", Blocks.OAK_PLANKS),
        SPRUCE("spruce", Blocks.SPRUCE_PLANKS),
        BIRCH("birch", Blocks.BIRCH_PLANKS),
        JUNGLE("jungle", Blocks.JUNGLE_PLANKS),
        ACACIA("acacia", Blocks.ACACIA_PLANKS),
        DARK_OAK("dark_oak", Blocks.DARK_OAK_PLANKS),
        MANGROVE("mangrove", Blocks.MANGROVE_PLANKS),
        CHERRY("cherry", Blocks.CHERRY_PLANKS),
        BAMBOO("bamboo", Blocks.BAMBOO_PLANKS);

        private final String name;
        private final Block planks;

        WoodKind(String name, Block planks) {
            this.name = name;
            this.planks = planks;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return this.planks;
        }
    }

    public enum Type implements StringRepresentable {
        OAK_SAILBOAT(WoodKind.OAK, BoatSize.SAILBOAT, "oak_sailboat"),
        OAK_MEDIUM_SAILBOAT(WoodKind.OAK, BoatSize.MEDIUM_SAILBOAT, "oak_medium_sailboat"),
        OAK_LARGE_SAILBOAT(WoodKind.OAK, BoatSize.LARGE_SAILBOAT, "oak_large_sailboat"),
        SPRUCE_SAILBOAT(WoodKind.SPRUCE, BoatSize.SAILBOAT, "spruce_sailboat"),
        SPRUCE_MEDIUM_SAILBOAT(WoodKind.SPRUCE, BoatSize.MEDIUM_SAILBOAT, "spruce_medium_sailboat"),
        SPRUCE_LARGE_SAILBOAT(WoodKind.SPRUCE, BoatSize.LARGE_SAILBOAT, "spruce_large_sailboat"),
        BIRCH_SAILBOAT(WoodKind.BIRCH, BoatSize.SAILBOAT, "birch_sailboat"),
        BIRCH_MEDIUM_SAILBOAT(WoodKind.BIRCH, BoatSize.MEDIUM_SAILBOAT, "birch_medium_sailboat"),
        BIRCH_LARGE_SAILBOAT(WoodKind.BIRCH, BoatSize.LARGE_SAILBOAT, "birch_large_sailboat"),
        JUNGLE_SAILBOAT(WoodKind.JUNGLE, BoatSize.SAILBOAT, "jungle_sailboat"),
        JUNGLE_MEDIUM_SAILBOAT(WoodKind.JUNGLE, BoatSize.MEDIUM_SAILBOAT, "jungle_medium_sailboat"),
        JUNGLE_LARGE_SAILBOAT(WoodKind.JUNGLE, BoatSize.LARGE_SAILBOAT, "jungle_large_sailboat"),
        ACACIA_SAILBOAT(WoodKind.ACACIA, BoatSize.SAILBOAT, "acacia_sailboat"),
        ACACIA_MEDIUM_SAILBOAT(WoodKind.ACACIA, BoatSize.MEDIUM_SAILBOAT, "acacia_medium_sailboat"),
        ACACIA_LARGE_SAILBOAT(WoodKind.ACACIA, BoatSize.LARGE_SAILBOAT, "acacia_large_sailboat"),
        DARK_OAK_SAILBOAT(WoodKind.DARK_OAK, BoatSize.SAILBOAT, "dark_oak_sailboat"),
        DARK_OAK_MEDIUM_SAILBOAT(WoodKind.DARK_OAK, BoatSize.MEDIUM_SAILBOAT, "dark_oak_medium_sailboat"),
        DARK_OAK_LARGE_SAILBOAT(WoodKind.DARK_OAK, BoatSize.LARGE_SAILBOAT, "dark_oak_large_sailboat"),
        MANGROVE_SAILBOAT(WoodKind.MANGROVE, BoatSize.SAILBOAT, "mangrove_sailboat"),
        MANGROVE_MEDIUM_SAILBOAT(WoodKind.MANGROVE, BoatSize.MEDIUM_SAILBOAT, "mangrove_medium_sailboat"),
        MANGROVE_LARGE_SAILBOAT(WoodKind.MANGROVE, BoatSize.LARGE_SAILBOAT, "mangrove_large_sailboat"),
        CHERRY_SAILBOAT(WoodKind.CHERRY, BoatSize.SAILBOAT, "cherry_sailboat"),
        CHERRY_MEDIUM_SAILBOAT(WoodKind.CHERRY, BoatSize.MEDIUM_SAILBOAT, "cherry_medium_sailboat"),
        CHERRY_LARGE_SAILBOAT(WoodKind.CHERRY, BoatSize.LARGE_SAILBOAT, "cherry_large_sailboat"),
        BAMBOO_SAILBOAT(WoodKind.BAMBOO, BoatSize.SAILBOAT, "bamboo_sailboat"),
        BAMBOO_MEDIUM_SAILBOAT(WoodKind.BAMBOO, BoatSize.MEDIUM_SAILBOAT, "bamboo_medium_sailboat"),
        BAMBOO_LARGE_SAILBOAT(WoodKind.BAMBOO, BoatSize.LARGE_SAILBOAT, "bamboo_large_sailboat");

        private final WoodKind woodKind;
        private final BoatSize boatSize;
        private final String name;

        public static final StringRepresentable.EnumCodec<Type> CODEC =
                StringRepresentable.fromEnum(Type::values);

        private static final IntFunction<Type> BY_ID =
                ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        Type(WoodKind woodKind, BoatSize boatSize, String name) {
            this.woodKind = woodKind;
            this.boatSize = boatSize;
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public WoodKind getWoodKind() {
            return this.woodKind;
        }

        public BoatSize getBoatSize() {
            return this.boatSize;
        }

        public Block getPlanks() {
            return this.woodKind.getPlanks();
        }

        public String getTextureName() {
            return this.name;
        }

        public String getItemName() {
            return this.name;
        }

        public String getChestItemName() {
            return this.name + "_chest_boat";
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Type byId(int id) {
            return BY_ID.apply(id);
        }

        public static Type byName(String name) {
            return CODEC.byName(name, OAK_SAILBOAT);
        }
    }
}