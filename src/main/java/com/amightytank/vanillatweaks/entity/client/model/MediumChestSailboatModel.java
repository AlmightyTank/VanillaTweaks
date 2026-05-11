package com.amightytank.vanillatweaks.entity.client.model;

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

    public MediumChestSailboatModel(ModelPart root) {
        this.visualRoot = root.getChild("visual_root");

        this.paddleLeft = root.getChild("left_paddle");
        this.paddleRight = root.getChild("right_paddle");
        this.paddleRightBack = root.getChild("right_paddle_back");
        this.paddleLeftBack = root.getChild("left_paddle_back");

        this.bannerSail = root.getChild("banner_sail");
        this.bannerPanel = root.getChild("banner_panel");

        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(120, 122).addBox(-11.0F, -3.0F, -22.0F, 22.0F, 6.0F, 44.0F, new CubeDeformation(0.0F))
                .texOffs(216, 232).addBox(-9.0F, -3.0F, 22.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(216, 240).addBox(-9.0F, -3.0F, -24.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(158, 248).addBox(-7.0F, -3.0F, 24.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(190, 248).addBox(-7.0F, -3.0F, -26.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition visual_root = partdefinition.addOrReplaceChild("visual_root", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition chest_lid = visual_root.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(56, 39).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -15.0F, -13.0F, 0.0F, -3.1329F, 0.0F));

        PartDefinition chest_knob = visual_root.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(94, 17).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -12.0F, -12.0F, 0.0F, -3.098F, 0.0F));

        PartDefinition chest_base = visual_root.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(8, 39).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -11.0F, -13.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition front = visual_root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(48, 59).addBox(6.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 67).addBox(4.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(40, 99).addBox(-10.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(92, 23).addBox(-8.0F, -7.0F, 11.0F, 12.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 101).addBox(-12.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, -15.0F, 0.0F, -3.1416F, 0.0F));

        PartDefinition left = visual_root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 23).addBox(-22.0F, -7.0F, -1.0F, 44.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, -2.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition right = visual_root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 31).addBox(-22.0F, -7.0F, -1.0F, 44.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition back = visual_root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(94, 103).addBox(6.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(102, 103).addBox(4.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(104, 39).addBox(-10.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(92, 31).addBox(-8.0F, -7.0F, 11.0F, 12.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(104, 47).addBox(-12.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -2.0F, 15.0F));

        PartDefinition bottom = visual_root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-22.0F, -11.0F, -3.0F, 44.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(96, 56).addBox(22.0F, -9.0F, -3.0F, 2.0F, 16.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(64, 96).addBox(-24.0F, -9.0F, -3.0F, 2.0F, 16.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(74, 96).addBox(24.0F, -7.0F, -3.0F, 2.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(84, 103).addBox(-26.0F, -7.0F, -3.0F, 2.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition left_paddle = partdefinition.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(56, 56).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(94, 4).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -6.0F, -10.0F, 2.5007F, -0.6699F, 2.9277F));

        PartDefinition right_paddle = partdefinition.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(8, 59).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 96).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -6.0F, 10.0F, -0.6409F, -0.6699F, -0.2139F));

        PartDefinition left_paddle_back = partdefinition.addOrReplaceChild("left_paddle_back", CubeListBuilder.create().texOffs(48, 76).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(8, 99).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -6.0F, -10.0F, 2.5007F, -0.6699F, 2.9277F));

        PartDefinition right_paddle_back = partdefinition.addOrReplaceChild("right_paddle_back", CubeListBuilder.create().texOffs(8, 79).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(24, 99).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -6.0F, 10.0F, -0.6409F, -0.6699F, -0.2139F));

        PartDefinition banner_sail = partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(0, 39).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(94, 0).addBox(-7.0F, -61.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(94, 2).addBox(-7.0F, -36.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 5.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition banner_panel = partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(88, 76).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -30.0F, -6.0F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animateSmallPaddle(boat, 0, this.paddleLeft, limbSwing);
        animateSmallPaddle(boat, 1, this.paddleRight, limbSwing);

        animateSmallPaddle(boat, 0, this.paddleLeftBack, limbSwing);
        animateSmallPaddle(boat, 1, this.paddleRightBack, limbSwing);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(visualRoot, paddleLeft, paddleRight, paddleLeftBack, paddleRightBack, bannerSail, bannerPanel);
    }

    @Override
    public ModelPart waterPatch() {
        return this.waterPatch;
    }

    private static void animateSmallPaddle(Boat boat, int side, ModelPart paddle, float limbSwing) {
        float f = boat.getRowingTime(side, limbSwing);

        boolean rightSide = side == 1;

        float yFix = rightSide ? -(float)Math.PI / 2F : (float)Math.PI / 2F;

        float baseX = rightSide ? -0.6409F : 2.5007F;
        float baseY = -0.6699F + yFix;
        float baseZ = rightSide ? -0.2139F : 2.9277F;

        float xAmount = 0.55F;
        float yAmount = 0.45F;

        paddle.xRot = Mth.clampedLerp(
                baseX - xAmount,
                baseX + xAmount,
                (Mth.sin(-f) + 1.0F) / 2.0F
        );

        if (rightSide) {
            paddle.yRot = Mth.clampedLerp(
                    baseY + yAmount,
                    baseY - yAmount,
                    (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F
            );
        } else {
            paddle.yRot = Mth.clampedLerp(
                    baseY - yAmount,
                    baseY + yAmount,
                    (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F
            );
        }

        paddle.zRot = baseZ;
    }
}
