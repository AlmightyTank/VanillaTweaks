package com.amightytank.vanillatweaks.client.debug;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.network.ModNetworking;
import com.amightytank.vanillatweaks.network.packet.C2SRequestAiDebugPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = VanillaTweaks.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class AiDebugClientForgeEvents {
    private static final double DEBUG_RANGE = 32.0D;

    private static int requestCooldownTicks = 0;

    private AiDebugClientForgeEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        while (AiDebugClientModEvents.AI_DEBUG_HUD_KEY.consumeClick()) {
            ClientAiDebugHudData.toggle();
        }

        while (AiDebugClientModEvents.AI_DEBUG_SELECT_KEY.consumeClick()) {
            handleSelectKey(minecraft);
        }

        if (!ClientAiDebugHudData.isEnabled()) {
            return;
        }

        ClientAiDebugHudData.tickAge();

        if (minecraft.player == null || minecraft.level == null) {
            ClientAiDebugHudData.clearLatestPacket();
            return;
        }

        requestCooldownTicks--;

        if (requestCooldownTicks > 0) {
            return;
        }

        requestCooldownTicks = 5;

        /*
         * If a mob is selected, keep tracking that selected mob.
         * If no mob is selected, fall back to the old behavior:
         * show the mob currently under the crosshair.
         */
        if (ClientAiDebugHudData.hasSelectedEntity()) {
            ModNetworking.sendToServer(
                    new C2SRequestAiDebugPacket(ClientAiDebugHudData.getSelectedEntityId())
            );
            return;
        }

        Entity lookedAt = getLookedAtEntity(minecraft, DEBUG_RANGE);

        if (!(lookedAt instanceof Mob)) {
            ClientAiDebugHudData.clearLatestPacket();
            return;
        }

        ModNetworking.sendToServer(new C2SRequestAiDebugPacket(lookedAt.getId()));
    }

    private static void handleSelectKey(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Entity lookedAt = getLookedAtEntity(minecraft, DEBUG_RANGE);

        if (lookedAt instanceof Mob mob) {
            ClientAiDebugHudData.selectEntity(mob.getId());

            minecraft.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "Selected AI debug mob: " + mob.getDisplayName().getString()
                    ),
                    true
            );

            return;
        }

        ClientAiDebugHudData.clearSelectedEntity();

        minecraft.player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("Cleared selected AI debug mob."),
                true
        );
    }

    private static Entity getLookedAtEntity(Minecraft minecraft, double range) {
        Entity cameraEntity = minecraft.getCameraEntity();

        if (cameraEntity == null) {
            return null;
        }

        Vec3 eyePosition = cameraEntity.getEyePosition(1.0F);
        Vec3 lookVector = cameraEntity.getViewVector(1.0F);
        Vec3 endPosition = eyePosition.add(lookVector.scale(range));

        AABB searchBox = cameraEntity.getBoundingBox()
                .expandTowards(lookVector.scale(range))
                .inflate(1.0D);

        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                cameraEntity,
                eyePosition,
                endPosition,
                searchBox,
                entity -> !entity.isSpectator() && entity.isPickable(),
                range * range
        );

        return hitResult == null ? null : hitResult.getEntity();
    }
}