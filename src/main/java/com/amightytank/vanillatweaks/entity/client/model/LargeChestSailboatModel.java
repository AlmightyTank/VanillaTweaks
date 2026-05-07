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

public class LargeChestSailboatModel extends ListModel<Boat> implements WaterPatchModel {
    private final ModelPart chestLid;
    private final ModelPart chestKnob;
    private final ModelPart chestBase;
    private final ModelPart front;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart back;
    private final ModelPart bottom;
    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;
    private final ModelPart paddleLeftMiddle;
    private final ModelPart paddleRightMiddle;
    private final ModelPart bannerSailFront;
    private final ModelPart bannerPanelFront;
    private final ModelPart bannerPanelBack;
    private final ModelPart bannerSailBack;
    private final ModelPart paddleRightBack;
    private final ModelPart paddleLeftBack;
    private final ModelPart waterPatch;

    public LargeChestSailboatModel(ModelPart root) {
        this.chestLid = root.getChild("chest_lid");
        this.chestKnob = root.getChild("chest_knob");
        this.chestBase = root.getChild("chest_base");
        this.front = root.getChild("front");
        this.left = root.getChild("left");
        this.right = root.getChild("right");
        this.back = root.getChild("back");
        this.bottom = root.getChild("bottom");
        this.paddleLeft = root.getChild("paddle_left");
        this.paddleRight = root.getChild("paddle_right");
        this.paddleLeftMiddle = root.getChild("paddle_left_middle");
        this.paddleRightMiddle = root.getChild("paddle_right_middle");
        this.bannerSailFront = root.getChild("banner_sail_front");
        this.bannerPanelFront = root.getChild("banner_panel_front");
        this.bannerPanelBack = root.getChild("banner_panel_back");
        this.bannerSailBack = root.getChild("banner_sail_back");
        this.paddleRightBack = root.getChild("paddle_right_back");
        this.paddleLeftBack = root.getChild("paddle_left_back");
        this.waterPatch = root.getChild("water_patch");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        /*
         * Blockbench exported this model with the boat body around Y 21/22.
         * Your existing large sailboat and vanilla boats use the body around Y 3/4.
         * These offsets are normalized by -18 so the chest version sits in the same place
         * as the normal large sailboat.
         */

        partdefinition.addOrReplaceChild("chest_lid", CubeListBuilder.create()
                        .texOffs(16, 64).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-6.0F, -9.0F, 31.0F));

        partdefinition.addOrReplaceChild("chest_knob", CubeListBuilder.create()
                        .texOffs(8, 114).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.0F, -6.0F, 30.0F));

        partdefinition.addOrReplaceChild("chest_base", CubeListBuilder.create()
                        .texOffs(16, 44).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-6.0F, -5.0F, 31.0F));

        partdefinition.addOrReplaceChild("front", CubeListBuilder.create()
                        .texOffs(8, 106).addBox(8.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(124, 104).addBox(6.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 126).addBox(-13.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(104, 71).addBox(-11.0F, -7.0F, 11.0F, 17.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(72, 131).addBox(-15.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 4.0F, -15.0F, 0.0F, -3.1416F, 0.0F));

        partdefinition.addOrReplaceChild("left", CubeListBuilder.create()
                        .texOffs(0, 28).addBox(-48.0F, -7.0F, -1.0F, 70.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(14.0F, 4.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        partdefinition.addOrReplaceChild("right", CubeListBuilder.create()
                        .texOffs(0, 36).addBox(-22.0F, -7.0F, -1.0F, 70.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-13.0F, 4.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        partdefinition.addOrReplaceChild("back", CubeListBuilder.create()
                        .texOffs(132, 104).addBox(9.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 134).addBox(7.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 134).addBox(-12.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(86, 104).addBox(-10.0F, -7.0F, 11.0F, 17.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 134).addBox(-14.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 4.0F, 41.0F));

        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-48.0F, -13.0F, -3.0F, 70.0F, 25.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(86, 112).addBox(22.0F, -11.0F, -3.0F, 2.0F, 21.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(96, 112).addBox(-50.0F, -11.0F, -3.0F, 2.0F, 21.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(106, 116).addBox(24.0F, -9.0F, -3.0F, 2.0F, 17.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(116, 116).addBox(-52.0F, -9.0F, -3.0F, 2.0F, 17.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 3.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

        partdefinition.addOrReplaceChild("paddle_left", CubeListBuilder.create()
                        .texOffs(64, 44).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 121).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(14.0F, -5.0F, -13.0F, 2.1642F, 0.8727F, 2.8798F));

        partdefinition.addOrReplaceChild("paddle_right", CubeListBuilder.create()
                        .texOffs(64, 64).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 121).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-13.0F, -5.0F, -13.0F, 2.1642F, -0.8727F, -2.8798F));

        partdefinition.addOrReplaceChild("paddle_left_middle", CubeListBuilder.create()
                        .texOffs(16, 81).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 121).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.0F, -5.0F, 12.0F, 2.1642F, 0.8727F, 2.8798F));

        partdefinition.addOrReplaceChild("paddle_right_middle", CubeListBuilder.create()
                        .texOffs(56, 84).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(126, 116).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-13.0F, -5.0F, 12.0F, 2.1642F, -0.8727F, -2.8798F));

        partdefinition.addOrReplaceChild("banner_sail_front", CubeListBuilder.create()
                        .texOffs(8, 44).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(104, 79).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(104, 81).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 6.0F, -4.0F, 0.0F, 3.1416F, 0.0F));

        partdefinition.addOrReplaceChild("banner_panel_front", CubeListBuilder.create()
                        .texOffs(104, 44).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(6.0F, -29.0F, -1.0F));

        partdefinition.addOrReplaceChild("banner_panel_back", CubeListBuilder.create()
                        .texOffs(56, 104).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(6.0F, -50.0F, 24.0F));

        partdefinition.addOrReplaceChild("banner_sail_back", CubeListBuilder.create()
                        .texOffs(0, 44).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 80.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(106, 112).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(106, 114).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -15.0F, 21.0F, 0.0F, 3.1416F, 0.0F));

        // Do not add water_patch to parts(); BoatRenderer renders this separately as the water mask.
        partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-11.0F, -3.0F, -22.0F, 22.0F, 6.0F, 44.0F, new CubeDeformation(0.0F))
                        .texOffs(96, 110).addBox(-9.0F, -3.0F, 22.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(96, 118).addBox(-9.0F, -3.0F, -24.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(38, 126).addBox(-7.0F, -3.0F, 24.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(70, 126).addBox(-7.0F, -3.0F, -26.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("paddle_right_back", CubeListBuilder.create()
                        .texOffs(96, 84).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(126, 129).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-13.0F, -5.0F, 33.0F, 2.1642F, -0.8727F, -2.8798F));

        partdefinition.addOrReplaceChild("paddle_left_back", CubeListBuilder.create()
                        .texOffs(16, 101).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(56, 131).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(14.0F, -5.0F, 34.0F, 2.1642F, 0.8727F, 2.8798F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animatePaddle(boat, 0, this.paddleLeft, limbSwing);
        animatePaddle(boat, 1, this.paddleRight, limbSwing);
        animatePaddle(boat, 0, this.paddleLeftMiddle, limbSwing);
        animatePaddle(boat, 1, this.paddleRightMiddle, limbSwing);
        animatePaddle(boat, 0, this.paddleLeftBack, limbSwing);
        animatePaddle(boat, 1, this.paddleRightBack, limbSwing);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(
                chestLid,
                chestKnob,
                chestBase,
                front,
                left,
                right,
                back,
                bottom,
                paddleLeft,
                paddleRight,
                paddleLeftMiddle,
                paddleRightMiddle,
                bannerSailFront,
                bannerPanelFront,
                bannerPanelBack,
                bannerSailBack,
                paddleRightBack,
                paddleLeftBack
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
