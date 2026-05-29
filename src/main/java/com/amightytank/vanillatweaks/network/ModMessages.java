package com.amightytank.vanillatweaks.network;

import com.amightytank.vanillatweaks.VanillaTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(VanillaTweaks.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(
                packetId++,
                SwapSailboatSeatC2SPacket.class,
                SwapSailboatSeatC2SPacket::encode,
                SwapSailboatSeatC2SPacket::decode,
                SwapSailboatSeatC2SPacket::handle
        );
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}