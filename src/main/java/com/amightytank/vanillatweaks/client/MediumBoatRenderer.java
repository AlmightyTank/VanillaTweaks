package com.amightytank.vanillatweaks.client;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.client.model.SailBoatModel;
import com.amightytank.vanillatweaks.entity.SailBoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.List;

public class SailBoatRenderer extends EntityRenderer<SailBoatEntity> {
    private final SailBoatModel<SailBoatEntity> model;

    // Vanilla banner flag model part.
    // We render ONLY the flag, not the pole/stick.
    private final ModelPart bannerFlag;

    public SailBoatRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SailBoatModel<>(context.bakeLayer(SailBoatModel.LAYER_LOCATION));
        this.bannerFlag = context.bakeLayer(ModelLayers.BANNER).getChild("flag");
    }

    @Override
    public void render(SailBoatEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0.0D, 1.6D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        poseStack.scale(1.0F, -1.0F, 1.0F);

        model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTicks, entityYaw, 0.0F);

        ResourceLocation texture = getTextureLocation(entity);
        var vertex = buffer.getBuffer(model.renderType(texture));

        model.renderToBuffer(
                poseStack,
                vertex,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1f, 1f, 1f, 1f
        );

        if (!entity.isUnderWater()) {
            VertexConsumer waterMask = buffer.getBuffer(RenderType.waterMask());
            model.waterPatch().render(
                    poseStack,
                    waterMask,
                    packedLight,
                    OverlayTexture.NO_OVERLAY
            );
        }

        renderBannerPatternOnly(entity, poseStack, buffer, packedLight);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderBannerPatternOnly(SailBoatEntity entity, PoseStack poseStack,
                                         MultiBufferSource buffer, int packedLight) {
        ItemStack bannerStack = entity.getBannerStack();

        if (bannerStack.isEmpty() || !(bannerStack.getItem() instanceof BannerItem bannerItem)) {
            return;
        }

        DyeColor baseColor = bannerItem.getColor();

        CompoundTag blockEntityTag = bannerStack.getTagElement("BlockEntityTag");
        ListTag patternsTag = null;

        if (blockEntityTag != null && blockEntityTag.contains("Patterns", Tag.TAG_LIST)) {
            patternsTag = blockEntityTag.getList("Patterns", Tag.TAG_COMPOUND);
        }

        List<Pair<Holder<BannerPattern>, DyeColor>> patterns =
                BannerBlockEntity.createPatterns(baseColor, patternsTag);

        poseStack.pushPose();

        /*
         * Alignment for your sail boat bb_main panel.
         * Tune these numbers if the pattern is slightly off.
         */
        poseStack.translate(0.0D, -0.625D, 0.735D);

        // Fit vanilla banner flag to your smaller sail panel.
        poseStack.scale(0.70F, 0.65F, 0.70F);

        // Face the sail surface correctly.
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        BannerRenderer.renderPatterns(
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                this.bannerFlag,
                ModelBakery.BANNER_BASE,
                true,
                patterns
        );

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SailBoatEntity entity) {
        return new ResourceLocation(
                VanillaTweaks.MODID,
                "textures/entity/sail_boat/" + entity.getWoodType() + ".png"
        );
    }
}