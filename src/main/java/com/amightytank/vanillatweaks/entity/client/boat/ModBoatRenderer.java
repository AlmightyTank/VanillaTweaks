package com.amightytank.vanillatweaks.entity.client.boat;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.boat.model.*;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.List;

public class ModBoatRenderer extends EntityRenderer<ModBoatEntity> {
    private static final float BOAT_BANNER_SCALE = 0.7F;
    private static final float BOAT_BANNER_SCALE1 = 0.65F;

    /*
     * Your banner_panel cubes are 14x26.
     * Vanilla banner flag is 20x40.
     * 20 * 0.65 = 13 wide
     * 40 * 0.65 = 26 tall
     *
     * These offsets move the vanilla banner cloth into the banner_panel rectangle.
     */
    private static final double BANNER_PANEL_X_OFFSET = -0.029D;
    private static final double BANNER_PANEL_Y_OFFSET = -0.813D;
    private static final double BANNER_PANEL_Z_OFFSET = -0.02D;

    // Large sailboat banner offset.
    private static final double LARGE_BANNER_PANEL_X_OFFSET = -0.001D;
    private static final double LARGE_BANNER_PANEL_Y_OFFSET = -0.815D;
    private static final double LARGE_BANNER_PANEL_Z_OFFSET = -0.05D;

    private final EntityModel<ModBoatEntity> smallSailboatModel;
    private final EntityModel<ModBoatEntity> mediumSailboatModel;
    private final EntityModel<ModBoatEntity> largeSailboatModel;

    private final BambooSailboatModel bambooSailboatModel;
    private final BambooMediumSailboatModel bambooMediumSailboatModel;
    private final BambooLargeSailboatModel bambooLargeSailboatModel;

    private final ModelPart bannerFlag;
    private final ModelPart bannerPole;
    private final ModelPart bannerBar;

    public ModBoatRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;


        this.smallSailboatModel = new SmallSailboatModel(context.bakeLayer(ModModelLayers.SAILBOAT_LAYER));
        this.mediumSailboatModel = new MediumSailboatModel(context.bakeLayer(ModModelLayers.MEDIUM_SAILBOAT_LAYER));
        this.largeSailboatModel = new LargeSailboatModel(context.bakeLayer(ModModelLayers.LARGE_SAILBOAT_LAYER));

        this.bambooSailboatModel = new BambooSailboatModel(context.bakeLayer(ModModelLayers.BAMBOO_SAILBOAT_LAYER));
        this.bambooMediumSailboatModel = new BambooMediumSailboatModel(context.bakeLayer(ModModelLayers.BAMBOO_MEDIUM_SAILBOAT_LAYER));
        this.bambooLargeSailboatModel = new BambooLargeSailboatModel(context.bakeLayer(ModModelLayers.BAMBOO_LARGE_SAILBOAT_LAYER));

        ModelPart bannerRoot = context.bakeLayer(ModelLayers.BANNER);
        this.bannerFlag = bannerRoot.getChild("flag");
        this.bannerPole = bannerRoot.getChild("pole");
        this.bannerBar = bannerRoot.getChild("bar");

        // Your sailboat model already has its own mast/poles.
        this.bannerPole.visible = false;
        this.bannerBar.visible = false;
    }

    @Override
    public boolean shouldRender(ModBoatEntity boat, Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(
            ModBoatEntity boat,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();

        poseStack.translate(0.0D, 1.375D, 0.0D);
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

        if (model instanceof SailboatPaddleModel paddleModel) {
            SailboatPaddleAnimator.animateOarSetsByPassengerCount(
                    boat,
                    paddleModel.getPaddleSets(),
                    partialTick
            );
        }

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

        this.renderBoatBanners(boat, model, poseStack, bufferSource, packedLight);

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

    private void renderBoatBanners(
            ModBoatEntity boat,
            EntityModel<ModBoatEntity> model,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        if (!(model instanceof BoatBannerModel bannerModel)) {
            return;
        }

        ItemStack bannerStack = this.getVisualBannerStack(boat);

        poseStack.pushPose();

        bannerModel.translateToBannerPanel(poseStack);
        this.alignBannerToPanel(boat, poseStack);
        this.renderBoatBanner(bannerStack, poseStack, bufferSource, packedLight);

        poseStack.popPose();

        if (boat.isLargeSailboat() && bannerModel.hasRearBannerPanel()) {
            poseStack.pushPose();

            bannerModel.translateToRearBannerPanel(poseStack);
            this.alignBannerToPanel(boat, poseStack);
            this.renderBoatBanner(bannerStack, poseStack, bufferSource, packedLight);

            poseStack.popPose();
        }
    }

    private void alignBannerToPanel(ModBoatEntity boat, PoseStack poseStack) {
        double xOffset = boat.isLargeSailboat()
                ? LARGE_BANNER_PANEL_X_OFFSET
                : BANNER_PANEL_X_OFFSET;

        double yOffset = boat.isLargeSailboat()
                ? LARGE_BANNER_PANEL_Y_OFFSET
                : BANNER_PANEL_Y_OFFSET;

        double zOffset = boat.isLargeSailboat()
                ? LARGE_BANNER_PANEL_Z_OFFSET
                : BANNER_PANEL_Z_OFFSET;

        poseStack.translate(xOffset, yOffset, zOffset);

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(BOAT_BANNER_SCALE, BOAT_BANNER_SCALE1, BOAT_BANNER_SCALE1);
    }

    private EntityModel<ModBoatEntity> getModel(ModBoatEntity boat) {
        if (boat.isBambooSailboat()) {
            if (boat.isLargeSailboat()) {
                return this.bambooLargeSailboatModel;
            }

            if (boat.isMediumSailboat()) {
                return this.bambooMediumSailboatModel;
            }

            return this.bambooSailboatModel;
        }

        if (boat.isLargeSailboat()) {
            return this.largeSailboatModel;
        }

        if (boat.isMediumSailboat()) {
            return this.mediumSailboatModel;
        }

        return this.smallSailboatModel;
    }

    private void renderBoatBanner(
            ItemStack bannerStack,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (!(bannerStack.getItem() instanceof BannerItem bannerItem)) {
            return;
        }

        DyeColor baseColor = bannerItem.getColor();

        CompoundTag blockEntityTag = BlockItem.getBlockEntityData(bannerStack);
        ListTag patternsTag = null;

        if (blockEntityTag != null && blockEntityTag.contains("Patterns", Tag.TAG_LIST)) {
            patternsTag = blockEntityTag.getList("Patterns", Tag.TAG_COMPOUND);
        }

        List<Pair<Holder<BannerPattern>, DyeColor>> patterns =
                BannerBlockEntity.createPatterns(baseColor, patternsTag);

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
    }

    private ItemStack getVisualBannerStack(ModBoatEntity boat) {
        ItemStack bannerStack = boat.getBannerStack();

        if (!bannerStack.isEmpty()) {
            return bannerStack;
        }

        return new ItemStack(Items.WHITE_BANNER);
    }

    @Override
    public ResourceLocation getTextureLocation(ModBoatEntity boat) {
        if (boat.isBambooSailboat()) {
            if (boat.isLargeSailboat()) {
                return BAMBOO_LARGE_SAILBOAT_TEXTURE;
            }

            if (boat.isMediumSailboat()) {
                return BAMBOO_MEDIUM_SAILBOAT_TEXTURE;
            }

            return BAMBOO_SAILBOAT_TEXTURE;
        }

        return getNormalSailboatTexture(boat);
    }

    private ResourceLocation getNormalSailboatTexture(ModBoatEntity boat) {
        String woodName = boat.getModVariant().getName();

        if (boat.isLargeSailboat()) {
            return new ResourceLocation(
                    VanillaTweaks.MOD_ID,
                    "textures/entity/boat/large_sailboat/" + woodName + ".png"
            );
        }

        if (boat.isMediumSailboat()) {
            return new ResourceLocation(
                    VanillaTweaks.MOD_ID,
                    "textures/entity/boat/medium_sailboat/" + woodName + ".png"
            );
        }

        return new ResourceLocation(
                VanillaTweaks.MOD_ID,
                "textures/entity/boat/sailboat/" + woodName + ".png"
        );
    }

    private static final ResourceLocation BAMBOO_SAILBOAT_TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/boat/sailboat/bamboo.png");

    private static final ResourceLocation BAMBOO_MEDIUM_SAILBOAT_TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/boat/medium_sailboat/bamboo.png");

    private static final ResourceLocation BAMBOO_LARGE_SAILBOAT_TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/boat/large_sailboat/bamboo.png");
}