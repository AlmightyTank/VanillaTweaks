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
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.List;

public class MediumSailboatModel extends ListModel<ModBoatEntity> implements WaterPatchModel, BoatBannerModel, SailboatPaddleModel {
    private final ModelPart front;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart back;
    private final ModelPart bottom;

    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;
    private final ModelPart paddleLeftBack;
    private final ModelPart paddleRightBack;

    private final SailboatPaddleAnimator.PaddleBase paddleLeftBase;
    private final SailboatPaddleAnimator.PaddleBase paddleRightBase;
    private final SailboatPaddleAnimator.PaddleBase paddleLeftBackBase;
    private final SailboatPaddleAnimator.PaddleBase paddleRightBackBase;

    private final ModelPart bannerSail;
    private final ModelPart bannerPanel;
    private final ModelPart waterPatch;

    private final ModelPart chest1;
    private final ModelPart chest2;

    public MediumSailboatModel(ModelPart root) {
        this.front = root.getChild("front");
        this.left = root.getChild("left");
        this.right = root.getChild("right");
        this.back = root.getChild("back");
        this.bottom = root.getChild("bottom");

        this.paddleLeft = root.getChild("paddle_left");
        this.paddleRight = root.getChild("paddle_right");
        this.paddleLeftBack = root.getChild("paddle_left_back");
        this.paddleRightBack = root.getChild("paddle_right_back");

        this.paddleLeftBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeft);
        this.paddleRightBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRight);
        this.paddleLeftBackBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeftBack);
        this.paddleRightBackBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRightBack);

        this.bannerSail = root.getChild("banner_sail");
        this.bannerPanel = root.getChild("banner_panel");
        this.bannerPanel.visible = false;

        this.chest1 = root.getChild("chest1");
        this.chest2 = root.getChild("chest2");

        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition front = partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(91, 47).addBox(-6.0F, -7.0F, -5.0F, 12.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(115, 39).addBox(-8.0F, -7.0F, -3.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(107, 39).addBox(-10.0F, -7.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(99, 39).addBox(6.0F, -7.0F, -3.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(91, 39).addBox(8.0F, -7.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, -31.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition left = partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 31).addBox(-30.0F, -7.0F, 1.0F, 60.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 22.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition right = partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 23).addBox(-30.0F, -7.0F, 1.0F, 60.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 22.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-30.0F, -11.0F, -3.0F, 60.0F, 20.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 21.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition cube_r1 = bottom.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 84).addBox(-7.0F, -3.0F, -1.0F, 14.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-33.0F, -1.0F, -3.0F, -1.5708F, 0.0F, 1.5708F));

        PartDefinition cube_r2 = bottom.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(48, 79).addBox(-9.0F, -3.0F, -1.0F, 18.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-31.0F, -1.0F, -3.0F, -1.5708F, 0.0F, 1.5708F));

        PartDefinition cube_r3 = bottom.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 94).addBox(-6.0F, -3.0F, -1.0F, 12.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(33.0F, -1.0F, -3.0F, -1.5708F, 0.0F, 1.5708F));

        PartDefinition cube_r4 = bottom.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(48, 89).addBox(-8.0F, -3.0F, -1.0F, 16.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(31.0F, -1.0F, -3.0F, -1.5708F, 0.0F, 1.5708F));

        PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(-3, 102).addBox(-30.0F, -11.0F, -3.0F, 60.0F, 20.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 18.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition cube_r5 = water_patch.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(48, 109).addBox(-7.0F, -6.0F, -1.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-33.0F, -1.0F, -3.0F, -1.5708F, 0.0F, 1.5708F));

        PartDefinition cube_r6 = water_patch.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(48, 104).addBox(-9.0F, -6.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-31.0F, -1.0F, -3.0F, -1.5708F, 0.0F, 1.5708F));

        PartDefinition cube_r7 = water_patch.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(48, 119).addBox(-6.0F, -6.0F, -1.0F, 12.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(33.0F, -1.0F, -3.0F, -1.5708F, 0.0F, 1.5708F));

        PartDefinition cube_r8 = water_patch.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(48, 114).addBox(-8.0F, -6.0F, -1.0F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(31.0F, -1.0F, -3.0F, -1.5708F, 0.0F, 1.5708F));

        PartDefinition back = partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(90, 47).addBox(-7.0F, -7.0F, -5.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(115, 39).addBox(-9.0F, -7.0F, -3.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(107, 39).addBox(-11.0F, -7.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(99, 39).addBox(7.0F, -7.0F, -3.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(91, 39).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, 31.0F, -3.1416F, 0.0F, -3.1416F));

        PartDefinition paddle_left = partdefinition.addOrReplaceChild("paddle_left", CubeListBuilder.create().texOffs(48, 59).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 59).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 13.0F, -19.0F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition paddle_left_back = partdefinition.addOrReplaceChild("paddle_left_back", CubeListBuilder.create().texOffs(48, 59).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 59).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 13.0F, 12.0F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition paddle_right = partdefinition.addOrReplaceChild("paddle_right", CubeListBuilder.create().texOffs(48, 39).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 39).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 13.0F, -19.0F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition paddle_right_back = partdefinition.addOrReplaceChild("paddle_right_back", CubeListBuilder.create().texOffs(48, 39).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 39).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 13.0F, 12.0F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition chest1 = partdefinition.addOrReplaceChild("chest1", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 12.0F, 5.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition chest_knob = chest1.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(0, 39).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition chest_base = chest1.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(0, 56).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 1.0F));

        PartDefinition chest_lid = chest1.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(0, 39).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -3.0F, 1.0F));

        PartDefinition chest2 = partdefinition.addOrReplaceChild("chest2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 12.0F, 21.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition chest_knob2 = chest2.addOrReplaceChild("chest_knob2", CubeListBuilder.create().texOffs(0, 39).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition chest_base2 = chest2.addOrReplaceChild("chest_base2", CubeListBuilder.create().texOffs(0, 56).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 1.0F));

        PartDefinition chest_lid2 = chest2.addOrReplaceChild("chest_lid2", CubeListBuilder.create().texOffs(0, 39).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -3.0F, 1.0F));

        PartDefinition banner_panel = partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(0, 76).addBox(-7.5F, -13.0F, 0.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -23.0F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition banner_sail = partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(103, 57).addBox(-1.625F, -17.25F, -3.0F, 2.0F, 57.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(91, 55).addBox(-7.625F, -17.25F, -3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(91, 55).addBox(-7.625F, 7.75F, -3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.625F, -18.75F, 1.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(ModBoatEntity boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeft, limbSwing, this.paddleLeftBase);
        SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRight, limbSwing, this.paddleRightBase);

        SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeftBack, limbSwing, this.paddleLeftBackBase);
        SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRightBack, limbSwing, this.paddleRightBackBase);

        this.chest1.visible = boat.getChestCount() >= 1;
        this.chest2.visible = boat.getChestCount() >= 2;
    }

    public List<SailboatPaddleAnimator.PaddleSet> getPaddleSets() {
        return List.of(
                new SailboatPaddleAnimator.PaddleSet(
                        this.paddleLeft,
                        this.paddleRight,
                        this.paddleLeftBase,
                        this.paddleRightBase
                ),
                new SailboatPaddleAnimator.PaddleSet(
                        this.paddleLeftBack,
                        this.paddleRightBack,
                        this.paddleLeftBackBase,
                        this.paddleRightBackBase
                )
        );
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(
                front,
                left,
                right,
                back,
                bottom,
                chest1,
                chest2,
                paddleLeft,
                paddleRight,
                paddleLeftBack,
                paddleRightBack,
                bannerSail,
                bannerPanel
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