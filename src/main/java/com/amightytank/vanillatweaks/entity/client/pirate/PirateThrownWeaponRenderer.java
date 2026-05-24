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
import net.minecraft.world.item.ItemStack;

public class PirateThrownWeaponRenderer extends EntityRenderer<PirateThrownWeaponEntity> {
    private static final ResourceLocation TRIDENT_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/trident.png");

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
        poseStack.pushPose();

        if (entity.getWeaponType() == PirateThrownWeaponEntity.WeaponType.TRIDENT) {
            this.renderTrident(entity, partialTick, poseStack, buffer, packedLight);
        } else {
            this.renderAxe(entity, partialTick, poseStack, buffer, packedLight);
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderTrident(PirateThrownWeaponEntity entity,
                               float partialTick,
                               PoseStack poseStack,
                               MultiBufferSource buffer,
                               int packedLight) {
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        // Vanilla thrown trident orientation.
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch + 90.0F));

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer,
                this.tridentModel.renderType(TRIDENT_TEXTURE),
                false,
                false
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
    }

    private void renderAxe(PirateThrownWeaponEntity entity,
                           float partialTick,
                           PoseStack poseStack,
                           MultiBufferSource buffer,
                           int packedLight) {
        ItemStack stack = entity.getRenderStack();

        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        // Point axe along flight direction.
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch + 90.0F));

        // Rotate item model so it feels like a thrown weapon instead of a flat dropped item.
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));

        poseStack.scale(1.25F, 1.25F, 1.25F);

        this.itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );
    }

    @Override
    public ResourceLocation getTextureLocation(PirateThrownWeaponEntity entity) {
        return TRIDENT_TEXTURE;
    }
}