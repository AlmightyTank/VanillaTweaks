package com.amightytank.vanillatweaks.screen;

import com.amightytank.vanillatweaks.menu.SailboatChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SailboatChestScreen extends AbstractContainerScreen<SailboatChestMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/generic_54.png");

    private final int rows;

    public SailboatChestScreen(SailboatChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.rows = menu.getRows();
        this.imageHeight = 114 + this.rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, 17);

        for (int row = 0; row < this.rows; row++) {
            guiGraphics.blit(TEXTURE, x, y + 17 + row * 18, 0, 17, this.imageWidth, 18);
        }

        guiGraphics.blit(TEXTURE, x, y + 17 + this.rows * 18, 0, 126, this.imageWidth, 96);
    }
}