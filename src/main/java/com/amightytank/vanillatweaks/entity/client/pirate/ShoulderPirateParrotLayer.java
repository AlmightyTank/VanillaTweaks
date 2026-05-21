package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.pirate.model.PirateCaptainModel;
import com.amightytank.vanillatweaks.entity.client.pirate.model.ShoulderPirateParrotModel;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ShoulderPirateParrotLayer extends RenderLayer<PirateCaptainEntity, PirateCaptainModel<PirateCaptainEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/shoulder_pirate_parrot.png");

    private final ShoulderPirateParrotModel<PirateCaptainEntity> parrotModel;

    public ShoulderPirateParrotLayer(PirateCaptainRenderer renderer,
                                     ShoulderPirateParrotModel<PirateCaptainEntity> parrotModel) {
        super(renderer);
        this.parrotModel = parrotModel;
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       PirateCaptainEntity captain,
                       float limbSwing,
                       float limbSwingAmount,
                       float partialTick,
                       float ageInTicks,
                       float netHeadYaw,
                       float headPitch) {

        if (!captain.hasShoulderParrot()) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(-0.38D, -1.1D, 0.08D);
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(15.0F));

        this.parrotModel.setupAnim(
                captain,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch
        );

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        this.parrotModel.renderToBuffer(
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
}