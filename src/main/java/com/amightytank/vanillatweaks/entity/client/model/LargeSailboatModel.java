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

public class LargeSailboatModel extends ListModel<Boat> implements WaterPatchModel {
    private final ModelPart front;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart back;
    private final ModelPart bottom;
    private final ModelPart paddle_left;
    private final ModelPart paddle_right;
    private final ModelPart paddle_left_middle;
    private final ModelPart paddle_right_middle;
    private final ModelPart banner_sail_front;
    private final ModelPart banner_panel_front;
    private final ModelPart banner_panel_back;
    private final ModelPart banner_sail_back;
    private final ModelPart paddle_right_back;
    private final ModelPart paddle_left_back;
    private final ModelPart waterPatch;

    public LargeSailboatModel(ModelPart root) {
        this.front = root.getChild("front");
        this.left = root.getChild("left");
        this.right = root.getChild("right");
        this.back = root.getChild("back");
        this.bottom = root.getChild("bottom");
        this.paddle_left = root.getChild("paddle_left");
        this.paddle_right = root.getChild("paddle_right");
        this.paddle_left_middle = root.getChild("paddle_left_middle");
        this.paddle_right_middle = root.getChild("paddle_right_middle");
        this.banner_sail_front = root.getChild("banner_sail_front");
        this.banner_panel_front = root.getChild("banner_panel_front");
        this.banner_panel_back = root.getChild("banner_panel_back");
        this.banner_sail_back = root.getChild("banner_sail_back");
        this.paddle_right_back = root.getChild("paddle_right_back");
        this.paddle_left_back = root.getChild("paddle_left_back");
        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(132, 71).addBox(8.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(138, 123).addBox(6.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(138, 131).addBox(-13.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 131).addBox(-11.0F, -7.0F, 11.0F, 17.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 139).addBox(-15.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 4F, -15.0F, 0.0F, -3.1416F, 0.0F));

        partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 28).addBox(-47.0F, -7.0F, -1.0F, 69.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, 4F, 0.0F, 0.0F, 1.5708F, 0.0F));

        partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 36).addBox(-22.0F, -7.0F, -1.0F, 69.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, 4F, 0.0F, 0.0F, -1.5708F, 0.0F));

        partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(8, 139).addBox(8.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 139).addBox(6.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 139).addBox(-12.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(132, 44).addBox(-10.0F, -7.0F, 11.0F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(138, 139).addBox(-14.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 4F, 40.0F));

        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-48.0F, -13.0F, -3.0F, 70.0F, 25.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 44).addBox(-4.0F, -11.0F, -3.0F, 28.0F, 21.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 68).addBox(-50.0F, -11.0F, -3.0F, 28.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(70, 44).addBox(-2.0F, -9.0F, -3.0F, 28.0F, 17.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(70, 64).addBox(-52.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 3F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        partdefinition.addOrReplaceChild("paddle_left", CubeListBuilder.create().texOffs(78, 83).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 91).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, -5F, -13.0F, 2.1642F, 0.8727F, 2.8798F));

        partdefinition.addOrReplaceChild("paddle_right", CubeListBuilder.create().texOffs(8, 91).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 104).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -5F, -13.0F, 2.1642F, -0.8727F, -2.8798F));

        partdefinition.addOrReplaceChild("paddle_left_middle", CubeListBuilder.create().texOffs(78, 103).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(48, 117).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, -5F, 12.0F, 2.1642F, 0.8727F, 2.8798F));

        partdefinition.addOrReplaceChild("paddle_right_middle", CubeListBuilder.create().texOffs(8, 111).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(132, 58).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -5F, 12.0F, 2.1642F, -0.8727F, -2.8798F));

        partdefinition.addOrReplaceChild("banner_sail_front", CubeListBuilder.create().texOffs(70, 83).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(38, 131).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(132, 52).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6F, -4.0F, 0.0F, 3.1416F, 0.0F));

        partdefinition.addOrReplaceChild("banner_panel_front", CubeListBuilder.create().texOffs(78, 123).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -29F, -1.0F));

        partdefinition.addOrReplaceChild("banner_panel_back", CubeListBuilder.create().texOffs(108, 123).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -50F, 24.0F));

        partdefinition.addOrReplaceChild("banner_sail_back", CubeListBuilder.create().texOffs(0, 44).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 80.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(132, 54).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(132, 56).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -15F, 21.0F, 0.0F, 3.1416F, 0.0F));

        partdefinition.addOrReplaceChild("paddle_right_back", CubeListBuilder.create().texOffs(118, 83).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(38, 133).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -5F, 33.0F, 2.1642F, -0.8727F, -2.8798F));

        partdefinition.addOrReplaceChild("paddle_left_back", CubeListBuilder.create().texOffs(118, 103).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(54, 133).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, -5F, 34.0F, 2.1642F, 0.8727F, 2.8798F));

        // Same idea as vanilla BoatModel: this is not in parts(); BoatRenderer renders it only as the water mask.
        partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 0).addBox(-48.0F, -13.0F, -3.0F, 70.0F, 25.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 44).addBox(-4.0F, -11.0F, -3.0F, 28.0F, 21.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 68).addBox(-50.0F, -11.0F, -3.0F, 28.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(70, 44).addBox(-2.0F, -9.0F, -3.0F, 28.0F, 17.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(70, 64).addBox(-52.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animatePaddle(boat, 0, this.paddle_left, limbSwing);
        animatePaddle(boat, 1, this.paddle_right, limbSwing);
        animatePaddle(boat, 0, this.paddle_left_middle, limbSwing);
        animatePaddle(boat, 1, this.paddle_right_middle, limbSwing);
        animatePaddle(boat, 0, this.paddle_left_back, limbSwing);
        animatePaddle(boat, 1, this.paddle_right_back, limbSwing);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(
                front,
                left,
                right,
                back,
                bottom,
                paddle_left,
                paddle_right,
                paddle_left_middle,
                paddle_right_middle,
                banner_sail_front,
                banner_panel_front,
                banner_panel_back,
                banner_sail_back,
                paddle_right_back,
                paddle_left_back
        );
    }

    @Override
    public ModelPart waterPatch() {
        return this.waterPatch;
    }

    private static void animatePaddle(Boat boat, int side, ModelPart paddle, float limbSwing) {
        float f = boat.getRowingTime(side, limbSwing);
        paddle.xRot = Mth.clampedLerp(-(float)Math.PI / 3F, -0.2617994F, (Mth.sin(-f) + 1.0F) / 2.0F);
        paddle.yRot = Mth.clampedLerp(-(float)Math.PI / 4F, (float)Math.PI / 4F, (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F);

        if (side == 1) {
            paddle.yRot = (float)Math.PI - paddle.yRot;
        }
    }
}
