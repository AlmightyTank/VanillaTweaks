package com.amightytank.vanillatweaks.event;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.network.ModMessages;
import com.amightytank.vanillatweaks.network.SwapSailboatSeatC2SPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class ClientKeyEvents {
    public static final String CATEGORY = "key.categories." + VanillaTweaks.MOD_ID;

    public static final KeyMapping SWAP_SAILBOAT_SEAT = new KeyMapping(
            "key." + VanillaTweaks.MOD_ID + ".swap_sailboat_seat",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    private static final int HOLD_TICKS = 8;

    private static boolean wasDown = false;
    private static boolean sentHoldPacket = false;
    private static int heldTicks = 0;

    @Mod.EventBusSubscriber(
            modid = VanillaTweaks.MOD_ID,
            value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(SWAP_SAILBOAT_SEAT);
        }
    }

    @Mod.EventBusSubscriber(
            modid = VanillaTweaks.MOD_ID,
            value = Dist.CLIENT
    )
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.player == null || minecraft.screen != null) {
                reset();
                return;
            }

            if (!(minecraft.player.getVehicle() instanceof ModBoatEntity)) {
                reset();
                return;
            }

            boolean isDown = SWAP_SAILBOAT_SEAT.isDown();

            if (isDown) {
                if (!wasDown) {
                    heldTicks = 0;
                    sentHoldPacket = false;
                }

                heldTicks++;

                if (!sentHoldPacket && heldTicks >= HOLD_TICKS) {
                    ModMessages.sendToServer(
                            new SwapSailboatSeatC2SPacket(SwapSailboatSeatC2SPacket.Action.SWAP_OCCUPIED)
                    );

                    sentHoldPacket = true;
                }
            } else {
                if (wasDown && !sentHoldPacket) {
                    ModMessages.sendToServer(
                            new SwapSailboatSeatC2SPacket(SwapSailboatSeatC2SPacket.Action.CYCLE_EMPTY)
                    );
                }

                heldTicks = 0;
                sentHoldPacket = false;
            }

            wasDown = isDown;
        }

        private static void reset() {
            wasDown = false;
            sentHoldPacket = false;
            heldTicks = 0;
        }
    }
}