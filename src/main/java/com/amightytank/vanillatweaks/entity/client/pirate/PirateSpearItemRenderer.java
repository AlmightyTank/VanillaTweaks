package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.pirate.model.PirateSpearModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PirateSpearItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/projectiles/pirate_spear.png");

    private PirateSpearModel model;

    public PirateSpearItemRenderer() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    private PirateSpearModel getModel() {
        if (this.model == null) {
            this.model = new PirateSpearModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.PIRATE_SPEAR_LAYER)
            );
        }

        return this.model;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext,
                             PoseStack poseStack, MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        poseStack.pushPose();

        // Move/rotate/scale the Java spear model into the player's hand.
        applyItemTransform(displayContext, poseStack);

        PirateSpearModel spearModel = this.getModel();

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer,
                spearModel.renderType(TEXTURE),
                false,
                stack.hasFoil()
        );

        spearModel.renderToBuffer(
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

    private void applyItemTransform(ItemDisplayContext displayContext, PoseStack poseStack) {
        switch (displayContext) {
            case THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.0D, 0.65D, 0.0D);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(0.0F));
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }

            case THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(0.0D, 0.65D, 0.0D);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(0.0F));
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }

            case FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.0D, -0.13D, 0.0D);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }

            case FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(0.0D, -0.13D, 0.0D);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }

            default -> {
                poseStack.translate(0.0D, 0.6D, 0.0D);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }
        }
    }
}