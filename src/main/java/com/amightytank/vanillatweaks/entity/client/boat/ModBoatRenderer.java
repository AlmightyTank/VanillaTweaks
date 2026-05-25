package com.amightytank.vanillatweaks.entity.client.boat;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.boat.model.LargeSailboatModel;
import com.amightytank.vanillatweaks.entity.client.boat.model.MediumSailboatModel;
import com.amightytank.vanillatweaks.entity.client.boat.model.SmallSailboatModel;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class ModBoatRenderer extends EntityRenderer<ModBoatEntity> {
    private final EntityModel<ModBoatEntity> smallSailboatModel;
    private final EntityModel<ModBoatEntity> mediumSailboatModel;
    private final EntityModel<ModBoatEntity> largeSailboatModel;

    public ModBoatRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;

        this.smallSailboatModel = new SmallSailboatModel(context.bakeLayer(ModModelLayers.SAILBOAT_LAYER));
        this.mediumSailboatModel = new MediumSailboatModel(context.bakeLayer(ModModelLayers.MEDIUM_SAILBOAT_LAYER));
        this.largeSailboatModel = new LargeSailboatModel(context.bakeLayer(ModModelLayers.LARGE_SAILBOAT_LAYER));
    }

    @Override
    public void render(ModBoatEntity boat, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0.0D, 2.375D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        float hurtTime = (float) boat.getHurtTime() - partialTick;
        float damage = boat.getDamage() - partialTick;

        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (hurtTime > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(
                    Mth.sin(hurtTime) * hurtTime * damage / 10.0F * (float) boat.getHurtDir()
            ));
        }

        float bubbleAngle = boat.getBubbleAngle(partialTick);

        if (!Mth.equal(bubbleAngle, 0.0F)) {
            poseStack.mulPose(Axis.XP.rotationDegrees(bubbleAngle));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);

        EntityModel<ModBoatEntity> model = this.getModel(boat);
        ResourceLocation texture = this.getTextureLocation(boat);

        model.setupAnim(boat, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(model.renderType(texture));
        model.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        if (!boat.isUnderWater() && model instanceof WaterPatchModel waterPatchModel) {
            VertexConsumer waterVertexConsumer = bufferSource.getBuffer(RenderType.waterMask());
            waterPatchModel.waterPatch().render(
                    poseStack,
                    waterVertexConsumer,
                    packedLight,
                    OverlayTexture.NO_OVERLAY
            );
        }

        poseStack.popPose();

        super.render(boat, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private EntityModel<ModBoatEntity> getModel(ModBoatEntity boat) {
        if (boat.isLargeSailboat()) {
            return this.largeSailboatModel;
        }

        if (boat.isMediumSailboat()) {
            return this.mediumSailboatModel;
        }

        return this.smallSailboatModel;
    }

    @Override
    public ResourceLocation getTextureLocation(ModBoatEntity boat) {
        Boat.Type type = boat.getModVariant();

        if (boat.isLargeSailboat()) {
            return largeTexture(type);
        }

        if (boat.isMediumSailboat()) {
            return mediumTexture(type);
        }

        return smallTexture(type);
    }

    private static ResourceLocation smallTexture(Boat.Type type) {
        return new ResourceLocation(
                VanillaTweaks.MOD_ID,
                "textures/entity/boat/sailboat/" + type.getName() + ".png"
        );
    }

    private static ResourceLocation mediumTexture(Boat.Type type) {
        return new ResourceLocation(
                VanillaTweaks.MOD_ID,
                "textures/entity/boat/medium_sailboat/" + type.getName() + ".png"
        );
    }

    private static ResourceLocation largeTexture(Boat.Type type) {
        return new ResourceLocation(
                VanillaTweaks.MOD_ID,
                "textures/entity/boat/large_sailboat/" + type.getName() + ".png"
        );
    }
}