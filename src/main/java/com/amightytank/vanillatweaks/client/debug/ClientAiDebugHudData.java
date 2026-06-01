package com.amightytank.vanillatweaks.client.debug;

import com.amightytank.vanillatweaks.network.packet.S2CAiDebugPacket;

public final class ClientAiDebugHudData {
    private static boolean enabled = false;

    /*
     * -1 means no mob is locked.
     * When selectedEntityId is set, the HUD keeps requesting that mob from the server.
     */
    private static int selectedEntityId = -1;

    private static S2CAiDebugPacket latestPacket;
    private static int ageTicks = 0;

    private ClientAiDebugHudData() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void enable() {
        enabled = true;
    }

    public static void toggle() {
        enabled = !enabled;

        if (!enabled) {
            clearSelectedEntity();
            clearLatestPacket();
        }
    }

    public static boolean hasSelectedEntity() {
        return selectedEntityId != -1;
    }

    public static int getSelectedEntityId() {
        return selectedEntityId;
    }

    public static void selectEntity(int entityId) {
        selectedEntityId = entityId;
        enabled = true;
        clearLatestPacket();
    }

    public static void clearSelectedEntity() {
        selectedEntityId = -1;
        clearLatestPacket();
    }

    public static void setLatestPacket(S2CAiDebugPacket packet) {
        latestPacket = packet;
        ageTicks = 0;
    }

    public static S2CAiDebugPacket getLatestPacket() {
        return latestPacket;
    }

    public static void tickAge() {
        if (latestPacket == null) {
            return;
        }

        ageTicks++;

        if (ageTicks > 40) {
            clearLatestPacket();
        }
    }

    public static void clearLatestPacket() {
        latestPacket = null;
        ageTicks = 0;
    }
}