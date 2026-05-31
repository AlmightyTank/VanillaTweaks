package com.amightytank.vanillatweaks.entity.custom.boat;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.item.ModItems;
import com.amightytank.vanillatweaks.menu.SailboatChestMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModBoatEntity extends Boat implements Container {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_BOAT_SIZE_TIER =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<ItemStack> DATA_BANNER_STACK =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<ItemStack> DATA_SECOND_BANNER_STACK =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<Integer> DATA_CHEST_COUNT =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_SEAT_0 =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_SEAT_1 =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_SEAT_2 =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_SEAT_3 =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final int EMPTY_SEAT = -1;

    private static final double SPEED_PER_BANNER = 0.08D;

    private static final int PIRATE_RAID_ROWING_TICKS = 5;
    private static final double PIRATE_RAID_BOAT_SPEED = 0.035D;
    private static final double PIRATE_RAID_BOAT_MAX_SPEED = 0.42D;


    private static final int ROWS_PER_CHEST = 2;
    private static final int SLOTS_PER_CHEST = ROWS_PER_CHEST * 9;
    private static final int MAX_CHESTS = 3;
    private static final int MAX_SLOTS = SLOTS_PER_CHEST * MAX_CHESTS;

    private boolean frontPlayerPressingForward;

    private boolean sailboatInputLeft;
    private boolean sailboatInputRight;
    private boolean sailboatInputForward;
    private boolean sailboatInputBack;

    private int pirateRaidRowingTicks;

    private float sailboatTurnVelocity;

    private NonNullList<ItemStack> inventory = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
    private final List<SailboatCollisionPartEntity> sailboatCollisionParts = new ArrayList<>();

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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, Boat.Type.OAK.ordinal());
        this.entityData.define(DATA_BOAT_SIZE_TIER, 1);
        this.entityData.define(DATA_BANNER_STACK, ItemStack.EMPTY);
        this.entityData.define(DATA_SECOND_BANNER_STACK, ItemStack.EMPTY);
        this.entityData.define(DATA_CHEST_COUNT, 0);
        this.entityData.define(DATA_SEAT_0, EMPTY_SEAT);
        this.entityData.define(DATA_SEAT_1, EMPTY_SEAT);
        this.entityData.define(DATA_SEAT_2, EMPTY_SEAT);
        this.entityData.define(DATA_SEAT_3, EMPTY_SEAT);
    }

    public Boat.Type getModVariant() {
        return Boat.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    public void setModVariant(Boat.Type type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    public int getBoatSizeTier() {
        return this.entityData.get(DATA_BOAT_SIZE_TIER);
    }

    public void setBoatSizeTier(int boatSizeTier) {
        this.entityData.set(DATA_BOAT_SIZE_TIER, Math.max(1, Math.min(3, boatSizeTier)));

        if (!this.isLargeSailboat()) {
            this.entityData.set(DATA_SECOND_BANNER_STACK, ItemStack.EMPTY);
        }

        if (!this.level().isClientSide) {
            this.updateSailboatCollisionParts();
        }
    }

    @Deprecated
    public int getBannerCount() {
        return this.getBoatSizeTier();
    }

    @Deprecated
    public void setBannerCount(int bannerCount) {
        this.setBoatSizeTier(bannerCount);
    }

    public ItemStack getBannerStack() {
        return this.entityData.get(DATA_BANNER_STACK);
    }

    public void setBannerStack(ItemStack stack) {
        if (stack.isEmpty()) {
            this.entityData.set(DATA_BANNER_STACK, ItemStack.EMPTY);
            return;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        this.entityData.set(DATA_BANNER_STACK, copy);
    }

    public boolean hasBanner() {
        return !this.getBannerStack().isEmpty();
    }

    public ItemStack getSecondBannerStack() {
        if (!this.isLargeSailboat()) {
            return ItemStack.EMPTY;
        }

        return this.entityData.get(DATA_SECOND_BANNER_STACK);
    }

    public void setSecondBannerStack(ItemStack stack) {
        if (!this.isLargeSailboat() || stack.isEmpty()) {
            this.entityData.set(DATA_SECOND_BANNER_STACK, ItemStack.EMPTY);
            return;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        this.entityData.set(DATA_SECOND_BANNER_STACK, copy);
    }

    public boolean hasSecondBanner() {
        return !this.getSecondBannerStack().isEmpty();
    }

    public boolean isMediumSailboat() {
        return this.getBoatSizeTier() == 2;
    }

    public boolean isLargeSailboat() {
        return this.getBoatSizeTier() >= 3;
    }

    public boolean isBambooSailboat() {
        return this.getModVariant() == Boat.Type.BAMBOO;
    }

    public int getBannerSlotCount() {
        return this.isLargeSailboat() ? 2 : 1;
    }

    public int getActiveBannerCount() {
        int count = 0;

        if (!this.getBannerStack().isEmpty()) {
            count++;
        }

        if (this.isLargeSailboat() && !this.getSecondBannerStack().isEmpty()) {
            count++;
        }

        return count;
    }

    public double getBannerSpeedMultiplier() {
        return 1.0D + (this.getActiveBannerCount() * SPEED_PER_BANNER);
    }

    private int getSailboatCollisionPartCount() {
        if (this.isLargeSailboat()) {
            return 6;
        }

        if (this.isMediumSailboat()) {
            return 4;
        }

        return 2;
    }

    private double getSailboatCollisionDeckLength() {
        if (this.isLargeSailboat()) {
            return 97.0D / 16.0D;
        }

        if (this.isMediumSailboat()) {
            return 72.0D / 16.0D;
        }

        return 33.0D / 16.0D;
    }

    private float getSailboatCollisionPartWidth() {
        if (this.isLargeSailboat()) {
            return 28.0F / 16.0F;
        }

        if (this.isMediumSailboat()) {
            return 24.0F / 16.0F;
        }

        return 21.0F / 16.0F;
    }

    private float getSailboatCollisionPartHeight() {
        if (this.isLargeSailboat()) {
            return 10.0F / 16.0F;
        }

        if (this.isMediumSailboat()) {
            return 9.0F / 16.0F;
        }

        return 10.0F / 16.0F;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (entity instanceof SailboatCollisionPartEntity part && part.isParentBoat(this)) {
            return false;
        }

        return super.canCollideWith(entity);
    }

    private void updateSailboatCollisionParts() {
        if (this.isRemoved()) {
            this.removeSailboatCollisionParts();
            return;
        }

        Iterator<SailboatCollisionPartEntity> iterator = this.sailboatCollisionParts.iterator();

        while (iterator.hasNext()) {
            SailboatCollisionPartEntity part = iterator.next();

            if (part == null || part.isRemoved()) {
                iterator.remove();
            }
        }

        int wantedCount = this.getSailboatCollisionPartCount();

        while (this.sailboatCollisionParts.size() > wantedCount) {
            SailboatCollisionPartEntity part = this.sailboatCollisionParts.remove(this.sailboatCollisionParts.size() - 1);
            part.discard();
        }

        while (this.sailboatCollisionParts.size() < wantedCount) {
            SailboatCollisionPartEntity part = new SailboatCollisionPartEntity(this.level(), this);
            part.setPartSize(this.getSailboatCollisionPartWidth(), this.getSailboatCollisionPartHeight());
            part.setPos(this.getX(), this.getY(), this.getZ());
            part.setYRot(this.getYRot());
            part.setXRot(0.0F);
            part.setDeltaMovement(Vec3.ZERO);

            this.level().addFreshEntity(part);
            this.sailboatCollisionParts.add(part);
        }

        float partWidth = this.getSailboatCollisionPartWidth();
        float partHeight = this.getSailboatCollisionPartHeight();

        double deckLength = this.getSailboatCollisionDeckLength();
        double halfDeckLength = deckLength * 0.5D;
        double halfPartWidth = partWidth * 0.5D;
        double usableHalfLength = Math.max(0.0D, halfDeckLength - halfPartWidth);

        double yawRadians = Math.toRadians(this.getYRot());

        /*
         * This follows the model's long X axis.
         * If the line of hitboxes is sideways, swap this axis with the alternate one below.
         */
        double axisX = -Math.sin(yawRadians);
        double axisZ = Math.cos(yawRadians);

        /*
         * Alternate axis:
         * double axisX = -Math.sin(yawRadians);
         * double axisZ = Math.cos(yawRadians);
         * double axisX = Math.cos(yawRadians);
            double axisZ = Math.sin(yawRadians);
         */

        int count = this.sailboatCollisionParts.size();

        for (int i = 0; i < count; i++) {
            SailboatCollisionPartEntity part = this.sailboatCollisionParts.get(i);

            double along;

            if (count <= 1) {
                along = 0.0D;
            } else {
                double progress = (double) i / (double) (count - 1);
                along = -usableHalfLength + progress * usableHalfLength * 2.0D;
            }

            double x = this.getX() + axisX * along;
            double y = this.getY() + 0.02D;
            double z = this.getZ() + axisZ * along;

            part.setParentBoat(this);
            part.setPartSize(partWidth, partHeight);
            part.setPos(x, y, z);
            part.setYRot(this.getYRot());
            part.setXRot(0.0F);
            part.setDeltaMovement(Vec3.ZERO);
        }
    }

    private void removeSailboatCollisionParts() {
        for (SailboatCollisionPartEntity part : this.sailboatCollisionParts) {
            if (part != null && !part.isRemoved()) {
                part.discard();
            }
        }

        this.sailboatCollisionParts.clear();
    }

    @Override
    public void tick() {
        super.tick();

        this.keepPirateRaidRowingAlive();

        this.applySmoothSailboatTurning();

        SailboatRowingPhysics.apply(
                this,
                this.frontPlayerPressingForward,
                this.isMediumSailboat(),
                this.isLargeSailboat()
        );

        this.applyPirateRaidRowingMotion();

        if (!this.level().isClientSide) {
            this.updateSailboatCollisionParts();
            this.normalizeSailboatSeatSlots();
        }
    }

    @Override
    public void setInput(boolean left, boolean right, boolean forward, boolean back) {
        this.sailboatInputLeft = left;
        this.sailboatInputRight = right;
        this.sailboatInputForward = forward;
        this.sailboatInputBack = back;

        this.frontPlayerPressingForward = forward;

        /*
         * Keep vanilla forward/back movement.
         * Disable vanilla left/right turning because we handle turning smoothly ourselves.
         */
        super.setInput(false, false, forward, back);
    }

    public void setPirateRaidRowing(boolean rowing) {
        if (rowing) {
            this.pirateRaidRowingTicks = PIRATE_RAID_ROWING_TICKS;
        } else {
            this.pirateRaidRowingTicks = 0;
        }

        this.sailboatInputLeft = rowing;
        this.sailboatInputRight = rowing;
        this.sailboatInputForward = rowing;
        this.sailboatInputBack = false;

        this.frontPlayerPressingForward = rowing;

        /*
         * Force vanilla paddle animation.
         * This also keeps SailboatRowingPhysics active.
         */
        super.setInput(rowing, rowing, rowing, false);
    }

    private void keepPirateRaidRowingAlive() {
        if (this.pirateRaidRowingTicks <= 0) {
            return;
        }

        this.pirateRaidRowingTicks--;

        this.sailboatInputForward = true;
        this.sailboatInputBack = false;
        this.frontPlayerPressingForward = true;

        super.setInput(true, true, true, false);
    }

    private void applyPirateRaidRowingMotion() {
        if (this.pirateRaidRowingTicks <= 0) {
            return;
        }

        if (!this.hasPirateRaidPassenger()) {
            return;
        }

        float yaw = this.getYRot() * Mth.DEG_TO_RAD;

        Vec3 motion = this.getDeltaMovement().add(
                Mth.sin(-yaw) * PIRATE_RAID_BOAT_SPEED,
                0.0D,
                Mth.cos(yaw) * PIRATE_RAID_BOAT_SPEED
        );

        double horizontalSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        if (horizontalSpeed > PIRATE_RAID_BOAT_MAX_SPEED) {
            double scale = PIRATE_RAID_BOAT_MAX_SPEED / horizontalSpeed;
            motion = new Vec3(motion.x * scale, motion.y, motion.z * scale);
        }

        this.setDeltaMovement(motion);
        this.hasImpulse = true;
    }

    private void applySmoothSailboatTurning() {
        if (!this.isVehicle()) {
            this.sailboatTurnVelocity *= 0.75F;
            return;
        }

        if (this.getControllingPassenger() == null) {
            this.sailboatTurnVelocity *= 0.75F;
            return;
        }

        float direction = 0.0F;

        if (this.sailboatInputLeft && !this.sailboatInputRight) {
            direction = -1.0F;
        } else if (this.sailboatInputRight && !this.sailboatInputLeft) {
            direction = 1.0F;
        }

        float maxTurnSpeed = this.getMaxSmoothTurnSpeed();
        float turnAcceleration = this.getSmoothTurnAcceleration();
        float turnDrag = this.getSmoothTurnDrag();

        /*
         * Standing still should not spin the boat like a top.
         * Forward/back input gives the rudder more bite.
         */
        if (!this.sailboatInputForward && !this.sailboatInputBack) {
            maxTurnSpeed *= 0.35F;
            turnAcceleration *= 0.45F;
        }

        if (direction != 0.0F) {
            this.sailboatTurnVelocity += direction * turnAcceleration;
            this.sailboatTurnVelocity = Mth.clamp(
                    this.sailboatTurnVelocity,
                    -maxTurnSpeed,
                    maxTurnSpeed
            );
        } else {
            this.sailboatTurnVelocity *= turnDrag;
        }

        if (Math.abs(this.sailboatTurnVelocity) < 0.01F) {
            this.sailboatTurnVelocity = 0.0F;
        }

        this.setYRot(this.getYRot() + this.sailboatTurnVelocity);
    }

    private float getMaxSmoothTurnSpeed() {
        if (this.isLargeSailboat()) {
            return 2.4F;
        }

        if (this.isMediumSailboat()) {
            return 3.8F;
        }

        return 6.5F;
    }

    private float getSmoothTurnAcceleration() {
        if (this.isLargeSailboat()) {
            return 0.12F;
        }

        if (this.isMediumSailboat()) {
            return 0.22F;
        }

        return 0.38F;
    }

    private float getSmoothTurnDrag() {
        if (this.isLargeSailboat()) {
            return 0.92F;
        }

        if (this.isMediumSailboat()) {
            return 0.88F;
        }

        return 0.82F;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity frontPassenger = this.getPassengerInSailboatSeat(0);

        if (frontPassenger instanceof Player player && !player.isSpectator()) {
            return player;
        }

        if (frontPassenger instanceof Mob mob && this.isPirateRaidPassenger(mob)) {
            return mob;
        }

        /*
         * Fallback for pirate raid boats:
         * If seat syncing puts the pirate somewhere other than seat 0,
         * still let one pirate count as the controller.
         */
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Mob mob && this.isPirateRaidPassenger(mob)) {
                return mob;
            }
        }

        return null;
    }

    private boolean hasPirateRaidPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Mob mob && this.isPirateRaidPassenger(mob)) {
                return true;
            }
        }

        return false;
    }

    private boolean isPirateRaidPassenger(Mob mob) {
        return mob.isAlive() && mob.getTags().contains("PirateTreasureRaid");
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.removeSailboatCollisionParts();
    }

    public int getChestCount() {
        return this.entityData.get(DATA_CHEST_COUNT);
    }

    public void setChestCount(int chestCount) {
        this.entityData.set(DATA_CHEST_COUNT, Math.max(0, Math.min(this.getMaxChestCount(), chestCount)));
    }

    public boolean hasChest() {
        return this.getChestCount() > 0;
    }

    public int getChestRows() {
        return this.getChestCount() * ROWS_PER_CHEST;
    }

    public int getActiveSlotCount() {
        return this.getChestCount() * SLOTS_PER_CHEST;
    }

    public int getMaxChestCount() {
        if (this.isLargeSailboat()) {
            return 3;
        }

        if (this.isMediumSailboat()) {
            return 2;
        }

        return 1;
    }

    public int getBasePassengerSlots() {
        if (this.isLargeSailboat()) {
            return 4;
        }

        if (this.isMediumSailboat()) {
            return 3;
        }

        return 2;
    }

    @Override
    protected int getMaxPassengers() {
        return Math.max(1, this.getBasePassengerSlots() - this.getChestCount());
    }

    public int getSailboatPassengerLimit() {
        return this.getMaxPassengers();
    }

    public boolean hasOpenSailboatSeat() {
        return this.getPassengers().size() < this.getSailboatPassengerLimit();
    }

    public boolean addMobToSailboat(Mob mob) {
        if (this.level().isClientSide) {
            return false;
        }

        if (mob == null || !mob.isAlive()) {
            return false;
        }

        if (!this.hasOpenSailboatSeat()) {
            return false;
        }

        if (mob.getVehicle() != null) {
            mob.stopRiding();
        }

        mob.setPos(this.getX(), this.getY() + 0.25D, this.getZ());
        mob.setYRot(this.getYRot());
        mob.setXRot(0.0F);
        mob.setYHeadRot(this.getYRot());
        mob.setDeltaMovement(Vec3.ZERO);

        boolean mounted = mob.startRiding(this, true);

        if (mounted) {
            this.normalizeSailboatSeatSlots();
        }

        return mounted;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        int passengerIndex = this.getPassengerSeatIndex(passenger);
        Vec3 seatOffset = this.getSailboatSeatOffset(passengerIndex);

        double yawRadians = Math.toRadians(this.getYRot());

        /*
         * seatOffset.x = left/right
         * seatOffset.y = up/down
         * seatOffset.z = front/back
         */
        double rotatedX = seatOffset.x * Math.cos(yawRadians) - seatOffset.z * Math.sin(yawRadians);
        double rotatedZ = seatOffset.x * Math.sin(yawRadians) + seatOffset.z * Math.cos(yawRadians);

        double riderY = this.getY() + this.getPassengersRidingOffset() + seatOffset.y;

        callback.accept(
                passenger,
                this.getX() + rotatedX,
                riderY,
                this.getZ() + rotatedZ
        );
        float yawDelta = Mth.wrapDegrees(this.getYRot() - this.yRotO);

        passenger.setYRot(passenger.getYRot() + yawDelta);
        passenger.setYHeadRot(passenger.getYHeadRot() + yawDelta);
    }

    private Vec3 getSailboatSeatOffset(int index) {
        int chestCount = this.getChestCount();

        Vec3 seatOffset;

        if (this.isLargeSailboat()) {
            seatOffset = getLargeSailboatSeatOffset(index, chestCount);
        } else if (this.isMediumSailboat()) {
            seatOffset = getMediumSailboatSeatOffset(index, chestCount);
        } else {
            seatOffset = getSmallSailboatSeatOffset(index, chestCount);
        }

        return applyBoatTypeSeatOffset(seatOffset);
    }

    private Vec3 applyBoatTypeSeatOffset(Vec3 seatOffset) {

        if (this.isBambooSailboat()) {
            if (this.isLargeSailboat()) {
                return seatOffset.add(0.0D, -0.0D, 0.0D);
            }
            if  (this.isMediumSailboat()) {
                return seatOffset.add(0.0D, -0.0D, 0.0D);
            }
            else {
                return seatOffset.add(0.0D, -0.0D, 0.0D);
            }
        }

        return seatOffset;
    }

    private Vec3 getSmallSailboatSeatOffset(int index, int chestCount) {
        double y = -0.35D;
        double SmallSailboatFirstPos = 0.25D;
        double SmallSailboatSecondPos = -0.45D;

        if (chestCount > 0) {
            return new Vec3(0.0D, y, SmallSailboatFirstPos);
        }

        return switch (index) {
            case 0 -> new Vec3(0.0D, y, SmallSailboatFirstPos);
            case 1 -> new Vec3(0.0D, y, SmallSailboatSecondPos);
            default -> Vec3.ZERO;
        };
    }

    private Vec3 getMediumSailboatSeatOffset(int index, int chestCount) {
        double y = -0.32D;
        double MediumSailboatFirstPos = 0.95D;
        double MediumSailboatSecondPos = -1.15D;
        double MediumSailboatThirdPos = -1.75D;

        if (chestCount >= 2) {
            return new Vec3(0.0D, y, MediumSailboatFirstPos);
        }

        if (chestCount == 1) {
            return switch (index) {
                case 0 -> new Vec3(0.0D, y, MediumSailboatFirstPos);
                case 1 -> new Vec3(0.0D, y, MediumSailboatSecondPos);
                default -> Vec3.ZERO;
            };
        }

        return switch (index) {
            case 0 -> new Vec3(0.0D, y, MediumSailboatFirstPos);
            case 1 -> new Vec3(0.0D, y, MediumSailboatSecondPos);
            case 2 -> new Vec3(0.0D, y, MediumSailboatThirdPos);
            default -> Vec3.ZERO;
        };
    }

    private Vec3 getLargeSailboatSeatOffset(int index, int chestCount) {
        double y = -0.30D;
        double LargeSailboatFirstPos = 1.25D;
        double LargeSailboatSecondPos = -0.15D;
        double LargeSailboatThirdPos = -1.75D;
        double LargeSailboatFourthPos = -2.35D;

        if (chestCount >= 3) {
            return new Vec3(0.0D, y, 0.85D);
        }

        if (chestCount == 2) {
            return switch (index) {
                case 0 -> new Vec3(0.0D, y, LargeSailboatFirstPos);
                case 1 -> new Vec3(0.0D, y, LargeSailboatSecondPos);
                default -> Vec3.ZERO;
            };
        }

        if (chestCount == 1) {
            return switch (index) {
                case 0 -> new Vec3(0.0D, y, LargeSailboatFirstPos);
                case 1 -> new Vec3(0.0D, y, LargeSailboatSecondPos);
                case 2 -> new Vec3(0.0D, y, LargeSailboatThirdPos);
                default -> Vec3.ZERO;
            };
        }

        return switch (index) {
            case 0 -> new Vec3(0.0D, y, LargeSailboatFirstPos);
            case 1 -> new Vec3(0.0D, y, LargeSailboatSecondPos);
            case 2 -> new Vec3(0.0D, y, LargeSailboatThirdPos);
            case 3 -> new Vec3(0.0D, y, LargeSailboatFourthPos);
            default -> Vec3.ZERO;
        };
    }

    private int getSeatEntityId(int seatIndex) {
        return switch (seatIndex) {
            case 0 -> this.entityData.get(DATA_SEAT_0);
            case 1 -> this.entityData.get(DATA_SEAT_1);
            case 2 -> this.entityData.get(DATA_SEAT_2);
            case 3 -> this.entityData.get(DATA_SEAT_3);
            default -> EMPTY_SEAT;
        };
    }

    private void setSeatEntityId(int seatIndex, int entityId) {
        switch (seatIndex) {
            case 0 -> this.entityData.set(DATA_SEAT_0, entityId);
            case 1 -> this.entityData.set(DATA_SEAT_1, entityId);
            case 2 -> this.entityData.set(DATA_SEAT_2, entityId);
            case 3 -> this.entityData.set(DATA_SEAT_3, entityId);
        }
    }

    private int getActiveSeatCount() {
        return Math.max(1, this.getMaxPassengers());
    }

    private boolean hasPassengerWithEntityId(int entityId) {
        for (Entity passenger : this.getPassengers()) {
            if (passenger.getId() == entityId) {
                return true;
            }
        }

        return false;
    }

    private int findSeatForEntityId(int entityId, int seatCount) {
        for (int i = 0; i < seatCount; i++) {
            if (this.getSeatEntityId(i) == entityId) {
                return i;
            }
        }

        return -1;
    }

    private int findFirstEmptySeat(int seatCount) {
        for (int i = 0; i < seatCount; i++) {
            if (this.getSeatEntityId(i) == EMPTY_SEAT) {
                return i;
            }
        }

        return -1;
    }

    private boolean seatIdWasUsedEarlier(int seatIndex, int entityId) {
        for (int i = 0; i < seatIndex; i++) {
            if (this.getSeatEntityId(i) == entityId) {
                return true;
            }
        }

        return false;
    }

    private void normalizeSailboatSeatSlots() {
        if (this.level().isClientSide) {
            return;
        }

        int seatCount = this.getActiveSeatCount();

        for (int i = 0; i < 4; i++) {
            int entityId = this.getSeatEntityId(i);

            if (i >= seatCount) {
                this.setSeatEntityId(i, EMPTY_SEAT);
                continue;
            }

            if (entityId == EMPTY_SEAT) {
                continue;
            }

            if (!this.hasPassengerWithEntityId(entityId) || this.seatIdWasUsedEarlier(i, entityId)) {
                this.setSeatEntityId(i, EMPTY_SEAT);
            }
        }

        for (Entity passenger : this.getPassengers()) {
            if (this.findSeatForEntityId(passenger.getId(), seatCount) >= 0) {
                continue;
            }

            int emptySeat = this.findFirstEmptySeat(seatCount);

            if (emptySeat >= 0) {
                this.setSeatEntityId(emptySeat, passenger.getId());
            }
        }
    }

    private Entity getPassengerInSailboatSeat(int seatIndex) {
        int entityId = this.getSeatEntityId(seatIndex);

        if (entityId == EMPTY_SEAT) {
            return null;
        }

        for (Entity passenger : this.getPassengers()) {
            if (passenger.getId() == entityId) {
                return passenger;
            }
        }

        return null;
    }

    private int getPassengerSeatIndex(Entity passenger) {
        int seatCount = this.getActiveSeatCount();
        int syncedSeat = this.findSeatForEntityId(passenger.getId(), seatCount);

        if (syncedSeat >= 0) {
            return syncedSeat;
        }

        int fallbackIndex = this.getPassengers().indexOf(passenger);

        if (fallbackIndex >= 0 && fallbackIndex < seatCount) {
            return fallbackIndex;
        }

        return 0;
    }

    public void cycleEmptySeatFor(Entity passenger) {
        if (this.level().isClientSide) {
            return;
        }

        if (passenger.getVehicle() != this) {
            return;
        }

        this.normalizeSailboatSeatSlots();

        int seatCount = this.getActiveSeatCount();

        if (seatCount <= 1) {
            return;
        }

        int currentSeat = this.findSeatForEntityId(passenger.getId(), seatCount);

        if (currentSeat < 0) {
            return;
        }

        for (int i = 1; i < seatCount; i++) {
            int nextSeat = (currentSeat + i) % seatCount;

            if (this.getSeatEntityId(nextSeat) == EMPTY_SEAT) {
                this.setSeatEntityId(currentSeat, EMPTY_SEAT);
                this.setSeatEntityId(nextSeat, passenger.getId());
                return;
            }
        }
    }

    public void swapSeatFor(Entity passenger) {
        if (this.level().isClientSide) {
            return;
        }

        if (passenger.getVehicle() != this) {
            return;
        }

        this.normalizeSailboatSeatSlots();

        int seatCount = this.getActiveSeatCount();

        if (seatCount <= 1) {
            return;
        }

        int currentSeat = this.findSeatForEntityId(passenger.getId(), seatCount);

        if (currentSeat < 0) {
            return;
        }

        for (int i = 1; i < seatCount; i++) {
            int nextSeat = (currentSeat + i) % seatCount;
            int nextSeatEntityId = this.getSeatEntityId(nextSeat);

            if (nextSeatEntityId != EMPTY_SEAT) {
                this.setSeatEntityId(currentSeat, nextSeatEntityId);
                this.setSeatEntityId(nextSeat, passenger.getId());
                return;
            }
        }
    }

    public static boolean canBoatPassengerAttackTarget(
            Boat boat,
            Entity passenger,
            LivingEntity target,
            float maxAttackAngle
    ) {
        if (boat == null || passenger == null || target == null) {
            return false;
        }

        if (!target.isAlive()) {
            return false;
        }

        if (!passenger.isPassengerOfSameVehicle(boat)) {
            return false;
        }

        Vec3 toTarget = target.position().subtract(boat.position());

        if (toTarget.lengthSqr() < 0.001D) {
            return true;
        }

        float targetYaw = (float) (Mth.atan2(toTarget.z, toTarget.x) * Mth.RAD_TO_DEG) - 90.0F;
        float boatYaw = boat.getYRot();

        float angleDifference = Math.abs(Mth.wrapDegrees(targetYaw - boatYaw));

        return angleDifference <= maxAttackAngle * 0.5F;
    }

    public boolean canAddChest() {
        if (this.getChestCount() >= this.getMaxChestCount()) {
            return false;
        }

        int newChestCount = this.getChestCount() + 1;
        int newPassengerLimit = Math.max(1, this.getBasePassengerSlots() - newChestCount);

        return this.getPassengers().size() <= newPassengerLimit;
    }

    public boolean canRemoveLastChest() {
        int chestCount = this.getChestCount();

        if (chestCount <= 0) {
            return false;
        }

        int start = (chestCount - 1) * SLOTS_PER_CHEST;
        int end = start + SLOTS_PER_CHEST;

        for (int i = start; i < end; i++) {
            if (!this.inventory.get(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void removeLastChest() {
        if (!this.canRemoveLastChest()) {
            return;
        }

        int chestCount = this.getChestCount();
        int start = (chestCount - 1) * SLOTS_PER_CHEST;
        int end = start + SLOTS_PER_CHEST;

        for (int i = start; i < end; i++) {
            this.inventory.set(i, ItemStack.EMPTY);
        }

        this.setChestCount(chestCount - 1);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof BannerItem && player.isShiftKeyDown()) {
            if (this.level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            ItemStack newBanner = stack.copy();
            newBanner.setCount(1);

            if (this.getBannerStack().isEmpty()) {
                this.setBannerStack(newBanner);
            } else if (this.isLargeSailboat() && this.getSecondBannerStack().isEmpty()) {
                this.setSecondBannerStack(newBanner);
            } else {
                ItemStack oldBanner = this.getBannerStack();

                if (!oldBanner.isEmpty()) {
                    this.spawnAtLocation(oldBanner.copy());
                }

                this.setBannerStack(newBanner);
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }

        if (stack.is(Items.CHEST)) {
            if (this.level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (!this.canAddChest()) {
                player.displayClientMessage(Component.literal("There is no room to add another chest."), true);
                return InteractionResult.CONSUME;
            }

            this.setChestCount(this.getChestCount() + 1);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }

        if (this.hasChest() && player.isShiftKeyDown()) {
            if (this.level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (stack.isEmpty()) {
                if (this.canRemoveLastChest()) {
                    this.removeLastChest();
                    this.spawnAtLocation(Items.CHEST);
                    return InteractionResult.CONSUME;
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    this.openChestInventory(serverPlayer);
                }

                return InteractionResult.CONSUME;
            }

            if (player instanceof ServerPlayer serverPlayer) {
                this.openChestInventory(serverPlayer);
            }

            return InteractionResult.CONSUME;
        }

        return super.interact(player, hand);
    }

    private void openChestInventory(ServerPlayer player) {
        NetworkHooks.openScreen(
                player,
                new SimpleMenuProvider(
                        (containerId, playerInventory, p) ->
                                new SailboatChestMenu(containerId, playerInventory, this, this.getChestRows()),
                        Component.literal("Sailboat")
                ),
                buffer -> {
                    buffer.writeInt(this.getId());
                    buffer.writeVarInt(this.getChestRows());
                }
        );
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putString("Type", this.getModVariant().getName());
        tag.putInt("BoatSizeTier", this.getBoatSizeTier());
        tag.putInt("ChestCount", this.getChestCount());

        if (!this.getBannerStack().isEmpty()) {
            tag.put("BannerStack", this.getBannerStack().save(new CompoundTag()));
        }

        if (!this.getSecondBannerStack().isEmpty()) {
            tag.put("SecondBannerStack", this.getSecondBannerStack().save(new CompoundTag()));
        }

        ContainerHelper.saveAllItems(tag, this.inventory);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Type", 8)) {
            this.setModVariant(Boat.Type.byName(tag.getString("Type")));
        }

        if (tag.contains("BoatSizeTier")) {
            this.setBoatSizeTier(tag.getInt("BoatSizeTier"));
        } else if (tag.contains("BannerCount")) {
            this.setBoatSizeTier(tag.getInt("BannerCount"));
        }

        if (tag.contains("BannerStack", 10)) {
            this.setBannerStack(ItemStack.of(tag.getCompound("BannerStack")));
        } else {
            this.setBannerStack(ItemStack.EMPTY);
        }

        if (tag.contains("SecondBannerStack", 10)) {
            this.setSecondBannerStack(ItemStack.of(tag.getCompound("SecondBannerStack")));
        } else {
            this.setSecondBannerStack(ItemStack.EMPTY);
        }

        this.inventory = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inventory);

        if (tag.contains("ChestCount")) {
            this.setChestCount(tag.getInt("ChestCount"));
        } else {
            this.setChestCount(0);
        }
    }

    @Override
    public Item getDropItem() {
        if (this.isLargeSailboat()) {
            return ModItems.LARGE_SAILBOAT.get();
        }

        if (this.isMediumSailboat()) {
            return ModItems.MEDIUM_SAILBOAT.get();
        }

        return ModItems.MOD_BOAT.get();
    }

    @Override
    public void destroy(DamageSource damageSource) {
        int chestCount = this.getChestCount();
        ItemStack bannerStack = this.getBannerStack().copy();
        ItemStack secondBannerStack = this.getSecondBannerStack().copy();

        super.destroy(damageSource);

        if (!this.level().isClientSide) {
            this.removeSailboatCollisionParts();

            if (!bannerStack.isEmpty()) {
                this.spawnAtLocation(bannerStack);
            }

            if (!secondBannerStack.isEmpty()) {
                this.spawnAtLocation(secondBannerStack);
            }

            Containers.dropContents(this.level(), this, this);

            for (int i = 0; i < chestCount; i++) {
                this.spawnAtLocation(Items.CHEST);
            }
        }
    }

    @Override
    public int getContainerSize() {
        return this.getActiveSlotCount();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (!this.inventory.get(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= this.getContainerSize()) {
            return ItemStack.EMPTY;
        }

        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= this.getContainerSize()) {
            return ItemStack.EMPTY;
        }

        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= this.getContainerSize()) {
            return ItemStack.EMPTY;
        }

        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= this.getContainerSize()) {
            return;
        }

        this.inventory.set(slot, stack);

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return !this.isRemoved() && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            this.inventory.set(i, ItemStack.EMPTY);
        }
    }

    public boolean isFrontSailboatPassenger(Entity passenger) {
        return passenger != null && this.getPassengerSeatIndex(passenger) == 0;
    }
}