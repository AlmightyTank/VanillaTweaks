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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class ModBoatEntity extends Boat implements Container {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_BANNER_COUNT =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_CHEST_COUNT =
            SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

    private static final int SLOTS_PER_CHEST = 27;
    private static final int MAX_CHESTS = 3;
    private static final int MAX_SLOTS = SLOTS_PER_CHEST * MAX_CHESTS;

    private NonNullList<ItemStack> inventory = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);

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
        this.entityData.define(DATA_BANNER_COUNT, 1);
        this.entityData.define(DATA_CHEST_COUNT, 0);
    }

    public Boat.Type getModVariant() {
        return Boat.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    public void setModVariant(Boat.Type type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    public int getBannerCount() {
        return this.entityData.get(DATA_BANNER_COUNT);
    }

    public void setBannerCount(int bannerCount) {
        this.entityData.set(DATA_BANNER_COUNT, Math.max(1, Math.min(3, bannerCount)));
    }

    public boolean isMediumSailboat() {
        return this.getBannerCount() == 2;
    }

    public boolean isLargeSailboat() {
        return this.getBannerCount() >= 3;
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
        return this.getChestCount() * 3;
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

        if (stack.isEmpty() && player.isShiftKeyDown() && this.hasChest()) {
            if (this.level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (!this.canRemoveLastChest()) {
                player.displayClientMessage(Component.literal("That chest is not empty."), true);
                return InteractionResult.CONSUME;
            }

            this.removeLastChest();
            this.spawnAtLocation(Items.CHEST);

            return InteractionResult.CONSUME;
        }

        if (this.hasChest() && player.isShiftKeyDown()) {
            if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                this.openChestInventory(serverPlayer);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
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
        tag.putInt("BannerCount", this.getBannerCount());
        tag.putInt("ChestCount", this.getChestCount());

        ContainerHelper.saveAllItems(tag, this.inventory);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Type", 8)) {
            this.setModVariant(Boat.Type.byName(tag.getString("Type")));
        }

        if (tag.contains("BannerCount")) {
            this.setBannerCount(tag.getInt("BannerCount"));
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