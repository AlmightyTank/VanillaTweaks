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

public class SmallSailboatModel extends ListModel<Boat> implements WaterPatchModel {
    private final ModelPart visualRoot;
    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;
    private final SailboatPaddleAnimator.PaddlePose leftPaddlePose;
    private final SailboatPaddleAnimator.PaddlePose rightPaddlePose;
    private final ModelPart bannerSail;
    private final ModelPart bannerPanel;
    private final ModelPart waterPatch;


    public SmallSailboatModel(ModelPart root) {
        this.visualRoot = root.getChild("visual_root");

        this.paddleLeft = root.getChild("left_paddle");
        this.paddleRight = root.getChild("right_paddle");

        this.leftPaddlePose = SailboatPaddleAnimator.PaddlePose.from(this.paddleLeft);
        this.rightPaddlePose = SailboatPaddleAnimator.PaddlePose.from(this.paddleRight);

        this.bannerSail = root.getChild("banner_sail");
        this.bannerPanel = root.getChild("banner_panel");

        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(40, 93).addBox(-9.0F, -19.0F, -14.0F, 16.0F, 7.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 17.0F, 1.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition visual_root = partdefinition.addOrReplaceChild("visual_root", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition front = visual_root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(68, 27).addBox(-9.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -15.0F, 0.0F, -3.1416F, 0.0F));

        PartDefinition left = visual_root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(8, 19).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -2.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition right = visual_root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(8, 27).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition back = visual_root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(8, 72).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -2.0F, 15.0F));

        PartDefinition bottom = visual_root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition left_paddle = partdefinition.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(56, 35).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(78, 75).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, -9.0F, 2.5007F, -0.6699F, 2.9277F));

        PartDefinition right_paddle = partdefinition.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(56, 55).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 79).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, 9.0F, -0.6409F, -0.6699F, -0.2139F));

        PartDefinition banner_sail = partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, -17.25F, -2.375F, 2.0F, 57.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 75).addBox(-7.0F, -17.25F, -1.375F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(48, 77).addBox(-7.0F, 7.75F, -1.375F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.625F, -35.75F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition banner_panel = partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(68, 0).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.5F, -40.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeft, limbSwing, this.leftPaddlePose);
        SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRight, limbSwing, this.rightPaddlePose);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(visualRoot, paddleLeft, paddleRight, bannerSail ,bannerPanel);
    }

    @Override
    public ModelPart waterPatch() {
        return this.waterPatch;
    }
}
