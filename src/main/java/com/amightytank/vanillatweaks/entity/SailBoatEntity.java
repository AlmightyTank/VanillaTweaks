package com.amightytank.vanillatweaks.entity;

import com.amightytank.vanillatweaks.registry.ModEntities;
import com.amightytank.vanillatweaks.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SailBoatEntity extends Boat {

    private static final int MAX_PASSENGERS = 1;

    private static final EntityDataAccessor<ItemStack> DATA_BANNER_STACK =
            SynchedEntityData.defineId(SailBoatEntity.class, EntityDataSerializers.ITEM_STACK);

    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputForward;
    private boolean inputBack;

    private boolean hasChest = false;
    private String woodType = "oak";

    public SailBoatEntity(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public SailBoatEntity(Level level, double x, double y, double z) {
        this(ModEntities.SAIL_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BANNER_STACK, ItemStack.EMPTY);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < MAX_PASSENGERS;
    }

    // --------------------
    // Banner
    // --------------------

    public void setBannerStack(ItemStack stack) {
        this.entityData.set(DATA_BANNER_STACK, stack.copy());
    }

    public ItemStack getBannerStack() {
        return this.entityData.get(DATA_BANNER_STACK);
    }

    // --------------------
    // Chest
    // --------------------

    public boolean hasChest() {
        return hasChest;
    }

    public void setHasChest(boolean value) {
        this.hasChest = value;
    }

    // --------------------
    // Wood Type
    // --------------------

    public void setWoodType(String woodType) {
        this.woodType = (woodType == null || woodType.isBlank()) ? "oak" : woodType;
    }

    public String getWoodType() {
        return woodType;
    }

    // --------------------
    // Interaction
    // --------------------

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Add chest after placement
        if (!this.hasChest && stack.is(Items.CHEST)) {
            this.setHasChest(true);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.interact(player, hand);
    }

    // --------------------
    // Drops
    // --------------------

    @Override
    public Item getDropItem() {
        return ModItems.SAIL_BOAT.get();
    }

    // --------------------
    // Rider Position
    // --------------------

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        double xOffset = 0.0D;
        double zOffset = 0.15D;

        double yaw = Math.toRadians(this.getYRot());

        double x = this.getX() + xOffset * Math.cos(yaw) - zOffset * Math.sin(yaw);
        double z = this.getZ() + xOffset * Math.sin(yaw) + zOffset * Math.cos(yaw);
        double y = this.getY() + 0.25D;

        callback.accept(passenger, x, y, z);
    }

    // --------------------
    // Input (for oars)
    // --------------------

    @Override
    public void setInput(boolean left, boolean right, boolean forward, boolean back) {
        super.setInput(left, right, forward, back);

        this.inputLeft = left;
        this.inputRight = right;
        this.inputForward = forward;
        this.inputBack = back;
    }

    public boolean isInputLeft() {
        return inputLeft;
    }

    public boolean isInputRight() {
        return inputRight;
    }

    public boolean isInputForward() {
        return inputForward;
    }

    public boolean isInputBack() {
        return inputBack;
    }

    // --------------------
    // Save / Load
    // --------------------

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putBoolean("HasChest", this.hasChest);
        tag.putString("WoodType", this.woodType);

        ItemStack banner = getBannerStack();
        if (!banner.isEmpty()) {
            CompoundTag bannerTag = new CompoundTag();
            banner.save(bannerTag);
            tag.put("BannerStack", bannerTag);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.hasChest = tag.getBoolean("HasChest");
        this.woodType = tag.contains("WoodType") ? tag.getString("WoodType") : "oak";

        if (tag.contains("BannerStack")) {
            this.setBannerStack(ItemStack.of(tag.getCompound("BannerStack")));
        }
    }
}