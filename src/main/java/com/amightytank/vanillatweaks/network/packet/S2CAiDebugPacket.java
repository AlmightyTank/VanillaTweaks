package com.amightytank.vanillatweaks.network.packet;

import com.amightytank.vanillatweaks.client.debug.ClientAiDebugPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CAiDebugPacket {
    public final int entityId;
    public final String title;
    public final String entityType;
    public final float health;
    public final float maxHealth;
    public final List<String> statusLines;
    public final List<String> goalLines;
    public final List<String> targetGoalLines;

    public S2CAiDebugPacket(
            int entityId,
            String title,
            String entityType,
            float health,
            float maxHealth,
            List<String> statusLines,
            List<String> goalLines,
            List<String> targetGoalLines
    ) {
        this.entityId = entityId;
        this.title = title;
        this.entityType = entityType;
        this.health = health;
        this.maxHealth = maxHealth;
        this.statusLines = statusLines;
        this.goalLines = goalLines;
        this.targetGoalLines = targetGoalLines;
    }

    public static S2CAiDebugPacket empty(String message) {
        return new S2CAiDebugPacket(
                -1,
                "AI Debug",
                "",
                0.0F,
                0.0F,
                List.of(message),
                List.of(),
                List.of()
        );
    }

    public static void encode(S2CAiDebugPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
        buf.writeUtf(packet.title, 256);
        buf.writeUtf(packet.entityType, 256);
        buf.writeFloat(packet.health);
        buf.writeFloat(packet.maxHealth);

        writeStringList(buf, packet.statusLines);
        writeStringList(buf, packet.goalLines);
        writeStringList(buf, packet.targetGoalLines);
    }

    public static S2CAiDebugPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        String title = buf.readUtf(256);
        String entityType = buf.readUtf(256);
        float health = buf.readFloat();
        float maxHealth = buf.readFloat();

        List<String> statusLines = readStringList(buf);
        List<String> goalLines = readStringList(buf);
        List<String> targetGoalLines = readStringList(buf);

        return new S2CAiDebugPacket(
                entityId,
                title,
                entityType,
                health,
                maxHealth,
                statusLines,
                goalLines,
                targetGoalLines
        );
    }

    public static void handle(S2CAiDebugPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> ClientAiDebugPacketHandler.handle(packet)
        ));

        context.setPacketHandled(true);
    }

    private static void writeStringList(FriendlyByteBuf buf, List<String> lines) {
        buf.writeVarInt(lines.size());
        for (String line : lines) {
            buf.writeUtf(line, 512);
        }
    }

    private static List<String> readStringList(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            lines.add(buf.readUtf(512));
        }

        return lines;
    }
}