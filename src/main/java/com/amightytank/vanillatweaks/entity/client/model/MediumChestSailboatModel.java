package com.amightytank.vanillatweaks.entity.client.model;

import com.amightytank.vanillatweaks.entity.client.SailboatPaddleAnimator;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

public class MediumChestSailboatModel extends ListModel<Boat> implements WaterPatchModel {
    private final ModelPart visualRoot;
    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;
    private final ModelPart paddleLeftBack;
    private final ModelPart paddleRightBack;
    private final ModelPart bannerSail;
    private final ModelPart bannerPanel;
    private final ModelPart waterPatch;

    private final SailboatPaddleAnimator.PaddleBase  leftFrontPaddleBase;
    private final SailboatPaddleAnimator.PaddleBase rightFrontPaddleBase;

    private final SailboatPaddleAnimator.PaddleBase leftBackPaddleBase;
    private final SailboatPaddleAnimator.PaddleBase rightBackPaddleBase;

    public MediumChestSailboatModel(ModelPart root) {
        this.visualRoot = root.getChild("visual_root");

        this.paddleLeft = root.getChild("left_paddle");
        this.paddleRight = root.getChild("right_paddle");
        this.paddleRightBack = root.getChild("right_paddle_back");
        this.paddleLeftBack = root.getChild("left_paddle_back");

        this.leftFrontPaddleBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeft);
        this.rightFrontPaddleBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRight);

        this.leftBackPaddleBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeftBack);
        this.rightBackPaddleBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRightBack);

        this.bannerSail = root.getChild("banner_sail");
        this.bannerPanel = root.getChild("banner_panel");

        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -3.0F, -22.0F, 22.0F, 6.0F, 44.0F, new CubeDeformation(0.0F))
                .texOffs(56, 106).addBox(-9.0F, -3.0F, 22.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 109).addBox(-9.0F, -3.0F, -24.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 114).addBox(-7.0F, -3.0F, 24.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 117).addBox(-7.0F, -3.0F, -26.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition visual_root = partdefinition.addOrReplaceChild("visual_root", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition chest_lid = visual_root.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(56, 89).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -15.0F, 10.0F));

        PartDefinition chest_knob = visual_root.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(48, 109).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -12.0F, 9.0F));

        PartDefinition chest_base = visual_root.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(8, 89).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -11.0F, 10.0F));

        PartDefinition front = visual_root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(96, 106).addBox(6.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(100, 125).addBox(4.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(108, 125).addBox(-10.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(80, 117).addBox(-8.0F, -7.0F, 11.0F, 12.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(116, 125).addBox(-12.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, -15.0F, 0.0F, -3.1416F, 0.0F));

        PartDefinition left = visual_root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 73).addBox(-22.0F, -7.0F, -1.0F, 44.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, -2.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition right = visual_root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 81).addBox(-22.0F, -7.0F, -1.0F, 44.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition back = visual_root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(124, 125).addBox(6.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(40, 126).addBox(4.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 126).addBox(-10.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(108, 117).addBox(-8.0F, -7.0F, 11.0F, 12.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(56, 126).addBox(-12.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -2.0F, 15.0F));

        PartDefinition bottom = visual_root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 50).addBox(-22.0F, -11.0F, -3.0F, 44.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(94, 70).addBox(22.0F, -9.0F, -3.0F, 2.0F, 16.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(70, 122).addBox(-24.0F, -9.0F, -3.0F, 2.0F, 16.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(80, 125).addBox(24.0F, -7.0F, -3.0F, 2.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(90, 125).addBox(-26.0F, -7.0F, -3.0F, 2.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition left_paddle = partdefinition.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(94, 50).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(8, 125).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.0F, -5.0F, 10.0F, -0.6409F, 0.6699F, 0.2139F));

        PartDefinition right_paddle = partdefinition.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(104, 70).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(24, 125).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.0F, -5.0F, -10.0F, 2.5007F, 0.6699F, -2.9277F));

        PartDefinition left_paddle_back = partdefinition.addOrReplaceChild("left_paddle_back", CubeListBuilder.create().texOffs(94, 50).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(8, 125).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -5.0F, 10.0F, -0.6409F, 0.6699F, 0.2139F));

        PartDefinition right_paddle_back = partdefinition.addOrReplaceChild("right_paddle_back", CubeListBuilder.create().texOffs(104, 70).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(24, 125).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -5.0F, -10.0F, 2.5007F, 0.6699F, -2.9277F));

        PartDefinition banner_sail = partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(0, 89).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(40, 122).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 124).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 6.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition banner_panel = partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(104, 90).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -29.0F, 6.0F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SailboatPaddleAnimator.animatePaddleFromBase(boat,0, this.paddleLeft, limbSwing, this.leftFrontPaddleBase,0.55F,0.45F);
        SailboatPaddleAnimator.animatePaddleFromBase(boat,1, this.paddleRight, limbSwing, this.rightFrontPaddleBase,0.55F,0.45F);
        SailboatPaddleAnimator.animatePaddleFromBase(boat,0, this.paddleLeftBack, limbSwing, this.leftBackPaddleBase,0.55F,0.45F);
        SailboatPaddleAnimator.animatePaddleFromBase(boat,1, this.paddleRightBack, limbSwing, this.rightBackPaddleBase,0.55F,0.45F);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(visualRoot, paddleLeft, paddleRight, paddleLeftBack, paddleRightBack, bannerSail, bannerPanel);
    }

    @Override
    public ModelPart waterPatch() {
        return this.waterPatch;
    }
}
