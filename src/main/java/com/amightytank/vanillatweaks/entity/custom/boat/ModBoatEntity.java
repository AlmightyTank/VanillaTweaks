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

    private static final int ROWS_PER_CHEST = 2;
    private static final int SLOTS_PER_CHEST = ROWS_PER_CHEST * 9;
    private static final int MAX_CHESTS = 3;
    private static final int MAX_SLOTS = SLOTS_PER_CHEST * MAX_CHESTS;

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

        return this.getBannerStack();
    }

    public void setSecondBannerStack(ItemStack stack) {
        this.setBannerStack(stack);
    }

    public boolean hasSecondBanner() {
        return this.isLargeSailboat() && this.hasBanner();
    }

    public boolean isMediumSailboat() {
        return this.getBoatSizeTier() == 2;
    }

    public boolean isLargeSailboat() {
        return this.getBoatSizeTier() >= 3;
    }

    public int getBannerSlotCount() {
        return this.isLargeSailboat() ? 2 : 1;
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

        if (!this.level().isClientSide) {
            this.updateSailboatCollisionParts();
        }
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

            ItemStack oldBanner = this.getBannerStack();

            if (!oldBanner.isEmpty()) {
                this.spawnAtLocation(oldBanner.copy());
            }

            ItemStack newBanner = stack.copy();
            newBanner.setCount(1);
            this.setBannerStack(newBanner);

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

        super.destroy(damageSource);

        if (!this.level().isClientSide) {
            this.removeSailboatCollisionParts();

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
}