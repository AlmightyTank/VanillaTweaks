package com.amightytank.vanillatweaks.network;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.network.packet.C2SRequestAiDebugPacket;
import com.amightytank.vanillatweaks.network.packet.S2CAiDebugPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(VanillaTweaks.MOD_ID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private ModNetworking() {
    }

    public static void register() {
        CHANNEL.messageBuilder(C2SRequestAiDebugPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SRequestAiDebugPacket::encode)
                .decoder(C2SRequestAiDebugPacket::decode)
                .consumerMainThread(C2SRequestAiDebugPacket::handle)
                .add();

        CHANNEL.messageBuilder(S2CAiDebugPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CAiDebugPacket::encode)
                .decoder(S2CAiDebugPacket::decode)
                .consumerMainThread(S2CAiDebugPacket::handle)
                .add();
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
}