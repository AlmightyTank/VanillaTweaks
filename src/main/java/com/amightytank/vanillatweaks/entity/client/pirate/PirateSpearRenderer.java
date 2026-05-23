package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.pirate.model.PirateSpearModel;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateSpearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PirateSpearRenderer extends EntityRenderer<PirateSpearEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/projectiles/pirate_spear.png");

    private final PirateSpearModel model;

    public PirateSpearRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PirateSpearModel(context.bakeLayer(ModModelLayers.PIRATE_SPEAR_LAYER));
    }

    @Override
    public void render(PirateSpearEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer,
                this.model.renderType(this.getTextureLocation(entity)),
                false,
                entity.isFoil()
        );

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

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PirateSpearEntity entity) {
        return TEXTURE;
    }
}