package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateThrownWeaponEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

public class PirateThrownWeaponRenderer extends EntityRenderer<PirateThrownWeaponEntity> {
    private static final ResourceLocation TRIDENT_LOCATION =
            new ResourceLocation("textures/entity/trident.png");

    private final TridentModel tridentModel;
    private final ItemRenderer itemRenderer;

    public PirateThrownWeaponRenderer(EntityRendererProvider.Context context) {
        super(context);

        this.tridentModel = new TridentModel(context.bakeLayer(ModelLayers.TRIDENT));
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PirateThrownWeaponEntity entity,
                       float entityYaw,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight) {
        if (entity.getWeaponType() == PirateThrownWeaponEntity.WeaponType.AXE) {
            this.renderAxe(entity, partialTick, poseStack, buffer, packedLight);
        } else {
            this.renderVanillaTrident(entity, partialTick, poseStack, buffer, packedLight);
        }

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderVanillaTrident(PirateThrownWeaponEntity entity,
                                      float partialTick,
                                      PoseStack poseStack,
                                      MultiBufferSource buffer,
                                      int packedLight) {
        poseStack.pushPose();

        /*
         * Same rotation style as vanilla ThrownTridentRenderer.
         * Do not put the axe flip in this branch.
         */
        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F
        ));

        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0F
        ));

        VertexConsumer vertexConsumer = buffer.getBuffer(
                this.tridentModel.renderType(TRIDENT_LOCATION)
        );

        this.tridentModel.renderToBuffer(
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
    }

    private void renderAxe(PirateThrownWeaponEntity entity,
                           float partialTick,
                           PoseStack poseStack,
                           MultiBufferSource buffer,
                           int packedLight) {
        poseStack.pushPose();

        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch + 90.0F));

        /*
         * AXE ONLY.
         * This fixes the upside-down axe without touching tridents.
         */
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        poseStack.scale(1.25F, 1.25F, 1.25F);

        this.itemRenderer.renderStatic(
                entity.getRenderStack(),
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PirateThrownWeaponEntity entity) {
        return TRIDENT_LOCATION;
    }
}