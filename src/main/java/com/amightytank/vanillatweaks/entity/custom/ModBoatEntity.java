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
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.function.IntFunction;

public class ModBoatEntity extends Boat {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

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
    public EntityDimensions getDimensions(Pose pose) {
        return switch (this.getModVariant().getBoatSize()) {
            case SAILBOAT -> EntityDimensions.scalable(1.375F, 0.5625F);
            case MEDIUM_SAILBOAT -> EntityDimensions.scalable(2.2F, 0.7F);
            case LARGE_SAILBOAT -> EntityDimensions.scalable(3.5F, 0.8F);
        };
    }

    @Override
    protected int getMaxPassengers() {
        return switch (this.getModVariant().getBoatSize()) {
            case SAILBOAT -> 1;
            case MEDIUM_SAILBOAT -> 2;
            case LARGE_SAILBOAT -> 3;
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
                case 0 -> new Vec3(0.0D, 0.0D, -0.65D);
                case 1 -> new Vec3(0.0D, 0.0D, -0.65D);
                default -> Vec3.ZERO;
            };

            case LARGE_SAILBOAT -> switch (index) {
                case 0 -> new Vec3(0.0D, 0.0D, -0.85D);
                case 1 -> new Vec3(-0.45D, 0.0D, 0.25D);
                case 2 -> new Vec3(0.45D, 0.0D, 0.25D);
                default -> Vec3.ZERO;
            };
        };
    }

    @Override
    public Item getDropItem() {
        return ModItems.getBoatItem(this.getModVariant()).get();
    }

    public void setVariant(Type variant) {
        this.entityData.set(DATA_ID_TYPE, variant.ordinal());
        this.refreshDimensions();
    }

    public Type getModVariant() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, Type.SAILBOAT.ordinal());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Type", this.getModVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Type", 8)) {
            this.setVariant(Type.byName(tag.getString("Type")));
        }
    }

    public enum BoatSize implements StringRepresentable {
        SAILBOAT("sailboat"),
        MEDIUM_SAILBOAT("medium_sailboat"),
        LARGE_SAILBOAT("large_sailboat");

        private final String name;

        BoatSize(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum WoodKind implements StringRepresentable {
        PINE("pine", Blocks.OAK_PLANKS),
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
        // These three keep your existing item ids and texture names working.
        SAILBOAT(WoodKind.PINE, BoatSize.SAILBOAT, "sailboat"),
        MEDIUM_SAILBOAT(WoodKind.PINE, BoatSize.MEDIUM_SAILBOAT, "medium_sailboat"),
        LARGE_SAILBOAT(WoodKind.PINE, BoatSize.LARGE_SAILBOAT, "large_sailboat"),

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
            return CODEC.byName(name, SAILBOAT);
        }
    }
}
