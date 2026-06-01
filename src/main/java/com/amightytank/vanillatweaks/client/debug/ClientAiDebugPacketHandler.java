package com.amightytank.vanillatweaks.client.debug;

import com.amightytank.vanillatweaks.network.packet.S2CAiDebugPacket;

public final class ClientAiDebugPacketHandler {
    private ClientAiDebugPacketHandler() {
    }

    public static void handle(S2CAiDebugPacket packet) {
        ClientAiDebugHudData.setLatestPacket(packet);
    }
}