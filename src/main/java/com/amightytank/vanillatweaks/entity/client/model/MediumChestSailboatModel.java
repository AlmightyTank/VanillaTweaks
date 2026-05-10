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
    private final ModelPart waterPatch;

    public MediumChestSailboatModel(ModelPart root) {
        this.visualRoot = root.getChild("visual_root");

        this.paddleLeft = this.visualRoot.getChild("left_paddle");
        this.paddleRight = this.visualRoot.getChild("right_paddle");
        this.paddleRightBack = this.visualRoot.getChild("right_paddle_back");
        this.paddleLeftBack = this.visualRoot.getChild("left_paddle_back");

        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(120, 122).addBox(-11.0F, -3.0F, -22.0F, 22.0F, 6.0F, 44.0F, new CubeDeformation(0.0F))
                .texOffs(216, 232).addBox(-9.0F, -3.0F, 22.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(216, 240).addBox(-9.0F, -3.0F, -24.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(158, 248).addBox(-7.0F, -3.0F, 24.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(190, 248).addBox(-7.0F, -3.0F, -26.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition visual_root = partdefinition.addOrReplaceChild("visual_root", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition chest_lid = visual_root.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(56, 39).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -15.0F, -13.0F, 0.0F, -3.098F, 0.0F));

        PartDefinition chest_knob = visual_root.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(94, 17).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -12.0F, -12.0F, 0.0F, -3.098F, 0.0F));

        PartDefinition chest_base = visual_root.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(8, 39).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -11.0F, -13.0F, 0.0F, -3.098F, 0.0F));

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

        PartDefinition left_paddle = visual_root.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(56, 56).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(94, 4).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, -11.0F, -13.0F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition right_paddle = visual_root.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(8, 59).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 96).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -11.0F, -13.0F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition left_paddle_back = visual_root.addOrReplaceChild("left_paddle_back", CubeListBuilder.create().texOffs(48, 76).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(8, 99).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, -11.0F, 12.0F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition right_paddle_back = visual_root.addOrReplaceChild("right_paddle_back", CubeListBuilder.create().texOffs(8, 79).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(24, 99).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -11.0F, 12.0F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition banner_sail = visual_root.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(0, 39).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(94, 0).addBox(-7.0F, -61.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(94, 2).addBox(-7.0F, -36.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition banner_panel = visual_root.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(88, 76).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -35.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animatePaddle(boat, 0, this.paddleLeft, limbSwing);
        animatePaddle(boat, 1, this.paddleRight, limbSwing);
        animatePaddle(boat, 0, this.paddleLeftBack, limbSwing);
        animatePaddle(boat, 1, this.paddleRightBack, limbSwing);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(visualRoot);
    }

    @Override
    public ModelPart waterPatch() {
        return this.waterPatch;
    }

    private static void animatePaddle(Boat boat, int side, ModelPart paddle, float limbSwing) {
        float f = boat.getRowingTime(side, limbSwing);

        float vanillaX = Mth.clampedLerp(
                -(float)Math.PI / 3F,
                -0.2617994F,
                (Mth.sin(-f) + 1.0F) / 2.0F
        );

        float vanillaY = Mth.clampedLerp(
                -(float)Math.PI / 4F,
                (float)Math.PI / 4F,
                (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F
        );

        if (side == 1) {
            vanillaY = (float)Math.PI - vanillaY;
        }

        // Your Blockbench base rotations
        float baseXRot = 2.1642F;
        float baseYRot = side == 1 ? -0.8727F : 0.8727F;
        float baseZRot = side == 1 ? -2.8798F : 2.8798F;

        // Turn vanilla rotation into an offset instead of replacing the Blockbench pose
        float centerX = (-(float)Math.PI / 3F + -0.2617994F) / 2.0F;
        float centerY = side == 1 ? (float)Math.PI : 0.0F;

        float strength = 1.2F;

        paddle.xRot = baseXRot + (vanillaX - centerX) * strength;
        paddle.yRot = baseYRot + (vanillaY - centerY) * strength;
        paddle.zRot = baseZRot;
    }
}
