package com.amightytank.vanillatweaks.client.debug;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = VanillaTweaks.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class AiDebugClientModEvents {
    public static final KeyMapping AI_DEBUG_HUD_KEY = new KeyMapping(
            "key.vanillatweaks.ai_debug_hud",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F9,
            "key.categories.vanillatweaks"
    );

    public static final KeyMapping AI_DEBUG_SELECT_KEY = new KeyMapping(
            "key.vanillatweaks.ai_debug_select",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F10,
            "key.categories.vanillatweaks"
    );

    private AiDebugClientModEvents() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(AI_DEBUG_HUD_KEY);
        event.register(AI_DEBUG_SELECT_KEY);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("ai_debug_hud", AiDebugHudOverlay::render);
    }
}