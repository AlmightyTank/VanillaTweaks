package com.amightytank.vanillatweaks.client.debug;

import com.amightytank.vanillatweaks.network.packet.S2CAiDebugPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.ArrayList;
import java.util.List;

public final class AiDebugHudOverlay {
    private AiDebugHudOverlay() {
    }

    public static void render(
            ForgeGui gui,
            GuiGraphics guiGraphics,
            float partialTick,
            int screenWidth,
            int screenHeight
    ) {
        if (!ClientAiDebugHudData.isEnabled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Font font = minecraft.font;

        S2CAiDebugPacket packet = ClientAiDebugHudData.getLatestPacket();

        List<Line> lines = new ArrayList<>();

        lines.add(new Line("AI DEBUG HUD", 0xFFFFD966));
        lines.add(new Line("F9 toggle | F10 select/clear mob", 0xFFAAAAAA));

        if (ClientAiDebugHudData.hasSelectedEntity()) {
            lines.add(new Line("Mode: LOCKED ENTITY #" + ClientAiDebugHudData.getSelectedEntityId(), 0xFF55FF55));
        } else {
            lines.add(new Line("Mode: LOOK TARGET", 0xFFFFFF55));
        }

        lines.add(new Line("", 0xFFFFFFFF));

        if (packet == null) {
            if (ClientAiDebugHudData.hasSelectedEntity()) {
                lines.add(new Line("Waiting for selected mob data...", 0xFFFFFFFF));
            } else {
                lines.add(new Line("Look at a mob or press F10 to select one.", 0xFFFFFFFF));
            }
        } else {
            lines.add(new Line(packet.title, 0xFF55FFFF));

            for (String statusLine : packet.statusLines) {
                lines.add(new Line(statusLine, 0xFFFFFFFF));
            }

            lines.add(new Line("", 0xFFFFFFFF));
            lines.add(new Line("GOAL SELECTOR", 0xFFFFD966));

            for (String goalLine : packet.goalLines) {
                lines.add(new Line(goalLine, 0xFFFFFFFF));
            }

            lines.add(new Line("", 0xFFFFFFFF));
            lines.add(new Line("TARGET SELECTOR", 0xFFFFD966));

            for (String targetGoalLine : packet.targetGoalLines) {
                lines.add(new Line(targetGoalLine, 0xFFFFFFFF));
            }
        }

        int x = 8;
        int y = 8;
        int lineHeight = 10;

        int width = 280;
        for (Line line : lines) {
            width = Math.max(width, font.width(line.text) + 12);
        }

        int height = lines.size() * lineHeight + 8;

        guiGraphics.fill(x - 4, y - 4, x + width, y + height, 0xAA000000);

        int drawY = y;

        for (Line line : lines) {
            if (!line.text.isEmpty()) {
                guiGraphics.drawString(font, line.text, x, drawY, line.color, false);
            }

            drawY += lineHeight;
        }
    }

    private record Line(String text, int color) {
    }
}