package com.amightytank.vanillatweaks.network;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SwapSailboatSeatC2SPacket {
    public enum Action {
        CYCLE_EMPTY,
        SWAP_OCCUPIED
    }

    private final Action action;

    public SwapSailboatSeatC2SPacket(Action action) {
        this.action = action;
    }

    public static void encode(SwapSailboatSeatC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.action);
    }

    public static SwapSailboatSeatC2SPacket decode(FriendlyByteBuf buf) {
        return new SwapSailboatSeatC2SPacket(buf.readEnum(Action.class));
    }

    public static void handle(SwapSailboatSeatC2SPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if (player == null) {
                return;
            }

            Entity vehicle = player.getVehicle();

            if (!(vehicle instanceof ModBoatEntity sailboat)) {
                return;
            }

            if (packet.action == Action.CYCLE_EMPTY) {
                sailboat.cycleEmptySeatFor(player);
            } else if (packet.action == Action.SWAP_OCCUPIED) {
                sailboat.swapSeatFor(player);
            }
        });

        context.setPacketHandled(true);
    }
}