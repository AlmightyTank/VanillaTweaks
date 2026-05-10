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

public class SmallChestSailboatModel extends ListModel<Boat> implements WaterPatchModel {
    private final ModelPart visualRoot;
    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;
    private final ModelPart waterPatch;


    public SmallChestSailboatModel(ModelPart root) {
        this.visualRoot = root.getChild("visual_root");

        this.paddleLeft = this.visualRoot.getChild("left_paddle");
        this.paddleRight = this.visualRoot.getChild("right_paddle");

        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(40, 93).addBox(-9.0F, -19.0F, -14.0F, 16.0F, 7.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 17.0F, 1.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition visual_root = partdefinition.addOrReplaceChild("visual_root", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition chest_lid = visual_root.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(8, 55).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -15.0F, -14.0F));

        PartDefinition chest_knob = visual_root.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(62, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -12.0F, -2.0F));

        PartDefinition chest_base = visual_root.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(8, 35).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -11.0F, -14.0F));

        PartDefinition front = visual_root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(68, 27).addBox(-9.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -15.0F, 0.0F, -3.1416F, 0.0F));

        PartDefinition left = visual_root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(8, 19).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -2.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition right = visual_root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(8, 27).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition back = visual_root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(8, 72).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -2.0F, 15.0F));

        PartDefinition bottom = visual_root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        PartDefinition left_paddle = visual_root.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(56, 35).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(78, 75).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -11.0F, 2.0F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition right_paddle = visual_root.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(56, 55).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 79).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -11.0F, 2.0F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition banner_sail = visual_root.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, -17.25F, -2.375F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 75).addBox(-7.0F, -17.25F, -1.375F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(48, 77).addBox(-7.0F, 7.75F, -1.375F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -42.75F, -11.625F));

        PartDefinition banner_panel = visual_root.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(68, 0).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -47.0F, -11.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeft, limbSwing);
        SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRight, limbSwing);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(visualRoot);
    }

    @Override
    public ModelPart waterPatch() {
        return this.waterPatch;
    }
}
