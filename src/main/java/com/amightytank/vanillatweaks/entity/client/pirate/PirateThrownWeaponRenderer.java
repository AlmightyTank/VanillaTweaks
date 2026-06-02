package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateThrownWeaponEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PirateThrownWeaponRenderer extends EntityRenderer<PirateThrownWeaponEntity> {
    private final ItemRenderer itemRenderer;

    public PirateThrownWeaponRenderer(EntityRendererProvider.Context context) {
        super(context);
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

        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        /*
         * Base projectile rotation.
         * This makes the thrown item point in the direction it is flying.
         */
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch + 90.0F));

        /*
         * Axe item models are opposite the trident orientation.
         * This fixes thrown axes appearing upside down.
         */
        if (entity.getWeaponType() == PirateThrownWeaponEntity.WeaponType.AXE) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }

        poseStack.scale(1.25F, 1.25F, 1.25F);

        ItemStack stack = entity.getRenderStack();

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

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PirateThrownWeaponEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}