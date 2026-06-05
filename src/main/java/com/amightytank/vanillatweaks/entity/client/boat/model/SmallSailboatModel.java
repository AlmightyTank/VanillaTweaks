package com.amightytank.vanillatweaks.entity.client.boat.model;

import com.amightytank.vanillatweaks.entity.client.boat.SailboatPaddleAnimator;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SmallSailboatModel extends ListModel<ModBoatEntity> implements WaterPatchModel, BoatBannerModel {
    private final ModelPart front;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart back;
    private final ModelPart bottom;

    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;

    private final SailboatPaddleAnimator.PaddleBase paddleLeftBase;
    private final SailboatPaddleAnimator.PaddleBase paddleRightBase;

    private final ModelPart bannerSail;
    private final ModelPart bannerPanel;
    private final ModelPart chest;
    private final ModelPart waterPatch;

    public SmallSailboatModel(ModelPart root) {
        this.front = root.getChild("front");
        this.left = root.getChild("left");
        this.right = root.getChild("right");
        this.back = root.getChild("back");
        this.bottom = root.getChild("bottom");

        this.paddleLeft = root.getChild("paddle_left");
        this.paddleRight = root.getChild("paddle_right");

        this.paddleLeftBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeft);
        this.paddleRightBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRight);

        this.chest = root.getChild("chest");

        this.bannerSail = root.getChild("banner_sail");
        this.bannerPanel = root.getChild("banner_panel");
        this.bannerPanel.visible = false;

        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition banner_panel = partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(49, 51).addBox(-7.5F, -13.0F, 0.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5341F, -23.0F, 9.1477F, 0.0F, 0.0F, 0.0F));

        PartDefinition front = partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0341F, 22.0F, -16.8523F, 3.1416F, 0.0F, 3.1416F));

        PartDefinition left = partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 43).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0341F, 22.0F, -1.8523F, 0.0F, 1.5708F, 0.0F));

        PartDefinition right = partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 35).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.9659F, 22.0F, -1.8523F, 0.0F, -1.5708F, 0.0F));

        PartDefinition back = partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 19).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0341F, 22.0F, 13.1477F, 0.0F, 0.0F, 0.0F));

        PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0341F, 21.0F, -1.8523F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 106).addBox(-14.0F, -9.0F, 0.0F, 28.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0341F, 21.0F, -1.8523F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition paddle_left = partdefinition.addOrReplaceChild("paddle_left", CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(62, 0).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0341F, 13.0F, -4.8523F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition paddle_right = partdefinition.addOrReplaceChild("paddle_right", CubeListBuilder.create().texOffs(62, 20).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(62, 20).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.9659F, 13.0F, -4.8523F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9659F, 12.0F, -0.8523F, 0.0F, 0.0F, 0.0F));

        PartDefinition chest_knob = chest.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(0, 51).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition chest_base = chest.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(0, 68).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 1.0F));

        PartDefinition chest_lid = chest.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(0, 51).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -3.0F, 1.0F));

        PartDefinition banner_sail = partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(84, 42).addBox(-1.625F, -17.25F, -3.0F, 2.0F, 57.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(66, 40).addBox(-7.625F, -17.25F, -3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(66, 40).addBox(-7.625F, 7.75F, -3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6591F, -18.75F, 13.1477F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(ModBoatEntity boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SailboatPaddleAnimator.animatePaddle(boat, 0, 0, this.paddleLeft, limbSwing, this.paddleLeftBase);
        SailboatPaddleAnimator.animatePaddle(boat, 1, 0, this.paddleRight, limbSwing, this.paddleRightBase);

        this.chest.visible = boat.getChestCount() >= 1;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(
                front,
                left,
                right,
                back,
                bottom,
                chest,
                paddleLeft,
                paddleRight,
                bannerSail
        );
    }

    @Override
    public void translateToBannerPanel(PoseStack poseStack) {
        this.bannerPanel.translateAndRotate(poseStack);
    }

    @Override
    public ModelPart waterPatch() {
        return this.waterPatch;
    }
}