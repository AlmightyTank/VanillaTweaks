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

public class SailboatModel extends ListModel<Boat> implements WaterPatchModel {
    private final ModelPart front;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart back;
    private final ModelPart bottom;
    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;
    private final ModelPart bannerSail;
    private final ModelPart bannerPanel;
    private final ModelPart waterPatch;

    public SailboatModel(ModelPart root) {
        this.front = root.getChild("front");
        this.left = root.getChild("left");
        this.right = root.getChild("right");
        this.back = root.getChild("back");
        this.bottom = root.getChild("bottom");
        this.paddleLeft = root.getChild("paddle_left");
        this.paddleRight = root.getChild("paddle_right");
        this.bannerSail = root.getChild("banner_sail");
        this.bannerPanel = root.getChild("banner_panel");
        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(62, 44).addBox(-9.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4F, -15.0F, 0.0F, -3.1416F, 0.0F));

        partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(8, 55).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 4F, 0.0F, 0.0F, 1.5708F, 0.0F));

        partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(62, 36).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 4F, 0.0F, 0.0F, -1.5708F, 0.0F));

        partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(68, 52).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 4F, 15.0F));

        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 36).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 3F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        partdefinition.addOrReplaceChild("paddle_left", CubeListBuilder.create().texOffs(8, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(68, 83).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -5F, -3.0F, 2.1642F, 0.8727F, 2.8798F));

        partdefinition.addOrReplaceChild("paddle_right", CubeListBuilder.create().texOffs(48, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(84, 83).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -5F, -3.0F, 2.1642F, -0.8727F, -2.8798F));

        partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(0, 55).addBox(-1.0F, -17.25F, -1.625F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 60).addBox(-7.0F, -17.25F, -0.625F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 83).addBox(-7.0F, 7.75F, -0.625F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -36.75F, 11.375F, 0.0F, 3.1416F, 0.0F));

        partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(8, 83).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -41F, 10.5F));

        // Same idea as vanilla BoatModel: this is not in parts(); BoatRenderer renders it only as the water mask.
        partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animatePaddle(boat, 0, this.paddleLeft, limbSwing);
        animatePaddle(boat, 1, this.paddleRight, limbSwing);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(
                front,
                left,
                right,
                back,
                bottom,
                paddleLeft,
                paddleRight,
                bannerSail,
                bannerPanel
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
