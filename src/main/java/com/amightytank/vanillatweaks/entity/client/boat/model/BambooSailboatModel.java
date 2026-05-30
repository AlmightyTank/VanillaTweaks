package com.amightytank.vanillatweaks.entity.client.boat.model;

import com.amightytank.vanillatweaks.entity.client.boat.SailboatPaddleAnimator;
import com.amightytank.vanillatweaks.entity.client.boat.SailboatPaddleModel;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

public class BambooSailboatModel extends ListModel<ModBoatEntity> implements BoatBannerModel, SailboatPaddleModel {
    private final ModelPart body;

    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;

    private final SailboatPaddleAnimator.PaddleBase paddleLeftBase;
    private final SailboatPaddleAnimator.PaddleBase paddleRightBase;

    private final ModelPart bannerSail;
    private final ModelPart bannerPanel;
    private final ModelPart chest;

    public BambooSailboatModel(ModelPart root) {
        this.body = root.getChild("body");

        this.paddleLeft = root.getChild("paddle_left");
        this.paddleRight = root.getChild("paddle_right");

        this.paddleLeftBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeft);
        this.paddleRightBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRight);

        this.chest = root.getChild("chest");

        this.bannerSail = root.getChild("banner_sail");
        this.bannerPanel = root.getChild("banner_panel");
        this.bannerPanel.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition banner_panel = partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(88, 36).addBox(-7.5F, -13.0F, 0.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5341F, -23.0F, 9.1477F, 0.0F, 0.0F, 0.0F));

        PartDefinition paddle_left = partdefinition.addOrReplaceChild("paddle_left", CubeListBuilder.create().texOffs(56, 85).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(56, 85).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0341F, 13.0F, -4.8523F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition paddle_right = partdefinition.addOrReplaceChild("paddle_right", CubeListBuilder.create().texOffs(0, 88).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(0, 88).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.9659F, 13.0F, -4.8523F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9659F, 7.0F, -0.8523F, 0.0F, 0.0F, 0.0F));

        PartDefinition chest_knob = chest.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(59, 68).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition chest_base = chest.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(0, 68).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 1.0F));

        PartDefinition chest_lid = chest.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(56, 68).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -3.0F, 1.0F));

        PartDefinition banner_sail = partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(48, 68).addBox(-1.625F, -17.25F, -3.0F, 2.0F, 56.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(88, 63).addBox(-7.625F, -17.25F, -3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(88, 63).addBox(-7.625F, 7.75F, -3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6591F, -18.75F, 13.1477F, 0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 36).addBox(-10.0F, -4.0F, -16.0F, 16.0F, 4.0F, 28.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-12.0F, -8.0F, -18.0F, 20.0F, 4.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(ModBoatEntity boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeft, limbSwing, this.paddleLeftBase);
        SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRight, limbSwing, this.paddleRightBase);

        this.chest.visible = boat.getChestCount() >= 1;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(
                body,
                chest,
                paddleLeft,
                paddleRight,
                bannerSail
        );
    }

    public List<SailboatPaddleAnimator.PaddleSet> getPaddleSets() {
        return List.of(
                new SailboatPaddleAnimator.PaddleSet(
                        this.paddleLeft,
                        this.paddleRight,
                        this.paddleLeftBase,
                        this.paddleRightBase
                )
        );
    }

    @Override
    public void translateToBannerPanel(PoseStack poseStack) {
        this.bannerPanel.translateAndRotate(poseStack);
    }
}