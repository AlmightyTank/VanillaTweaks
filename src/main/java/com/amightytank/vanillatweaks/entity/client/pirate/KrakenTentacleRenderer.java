package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.pirate.model.KrakenTentacleModel;
import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class KrakenTentacleRenderer extends EntityRenderer<KrakenTentacleEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/kraken_tentacle.png");

    private final KrakenTentacleModel model;

    public KrakenTentacleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new KrakenTentacleModel(context.bakeLayer(ModModelLayers.KRAKEN_TENTACLE_LAYER));
    }

    @Override
    public void render(
            KrakenTentacleEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        /*
         * DATA_ACTIVE is now the source of truth.
         * Do not also check getWarmupDelay() here.
         */
        if (!entity.isAttackActive() || !entity.hasAnimationStarted()) {
            return;
        }

        poseStack.pushPose();

        /*
         * Y rotation only.
         * This turns the circle tentacles inward.
         * Do not add X or Z rotation.
         */
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));

        /*
         * Keep this because it makes the Blockbench model render upright.
         */
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);

        this.model.setupAnim(
                entity,
                0.0F,
                0.0F,
                entity.tickCount + partialTick,
                0.0F,
                0.0F
        );

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        this.model.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(KrakenTentacleEntity entity) {
        return TEXTURE;
    }
}