package com.amightytank.vanillatweaks.network.packet;

import com.amightytank.vanillatweaks.debug.ai.AiDebugCollector;
import com.amightytank.vanillatweaks.network.ModNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRequestAiDebugPacket {
    private final int entityId;

    public C2SRequestAiDebugPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(C2SRequestAiDebugPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
    }

    public static C2SRequestAiDebugPacket decode(FriendlyByteBuf buf) {
        return new C2SRequestAiDebugPacket(buf.readVarInt());
    }

    public static void handle(C2SRequestAiDebugPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        ServerPlayer player = context.getSender();
        if (player == null) {
            context.setPacketHandled(true);
            return;
        }

        Entity entity = player.serverLevel().getEntity(packet.entityId);

        if (!(entity instanceof Mob mob)) {
            ModNetworking.sendToPlayer(player, S2CAiDebugPacket.empty("Look at a mob to debug AI."));
            context.setPacketHandled(true);
            return;
        }

        if (player.distanceToSqr(mob) > 64.0D * 64.0D) {
            ModNetworking.sendToPlayer(player, S2CAiDebugPacket.empty("Target mob is too far away."));
            context.setPacketHandled(true);
            return;
        }

        ModNetworking.sendToPlayer(player, AiDebugCollector.collect(mob));

        context.setPacketHandled(true);
    }
}