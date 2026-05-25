package com.amightytank.vanillatweaks.menu;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SailboatChestMenu extends AbstractContainerMenu {
    private final Container boatContainer;
    private final int rows;

    public SailboatChestMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(
                containerId,
                playerInventory,
                buffer.readInt(),
                buffer.readVarInt()
        );
    }

    private SailboatChestMenu(int containerId, Inventory playerInventory, int entityId, int rows) {
        this(
                containerId,
                playerInventory,
                getBoatContainer(playerInventory, entityId, rows),
                rows
        );
    }

    public SailboatChestMenu(int containerId, Inventory playerInventory, Container boatContainer, int rows) {
        super(ModMenuTypes.SAILBOAT_CHEST_MENU.get(), containerId);

        this.boatContainer = boatContainer;
        this.rows = Math.max(1, Math.min(9, rows));

        int boatSlotCount = this.rows * 9;

        checkContainerSize(boatContainer, boatSlotCount);
        boatContainer.startOpen(playerInventory.player);

        for (int row = 0; row < this.rows; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        boatContainer,
                        column + row * 9,
                        8 + column * 18,
                        18 + row * 18
                ));
            }
        }

        int playerInventoryY = 32 + this.rows * 18;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        playerInventoryY + row * 18
                ));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(
                    playerInventory,
                    column,
                    8 + column * 18,
                    playerInventoryY + 58
            ));
        }
    }

    private static Container getBoatContainer(Inventory playerInventory, int entityId, int rows) {
        Entity entity = playerInventory.player.level().getEntity(entityId);

        if (entity instanceof ModBoatEntity boat && boat.hasChest()) {
            return boat;
        }

        return new SimpleContainer(rows * 9);
    }

    public int getRows() {
        return this.rows;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copiedStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            copiedStack = slotStack.copy();

            int boatSlotCount = this.rows * 9;
            int totalSlotCount = boatSlotCount + 36;

            if (index < boatSlotCount) {
                if (!this.moveItemStackTo(slotStack, boatSlotCount, totalSlotCount, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, boatSlotCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return copiedStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.boatContainer.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.boatContainer.stopOpen(player);
    }
}