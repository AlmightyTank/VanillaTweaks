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
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

public class BambooMediumSailboatModel extends ListModel<ModBoatEntity> implements BoatBannerModel, SailboatPaddleModel {
    private final ModelPart body;

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

    private final ModelPart chest1;
    private final ModelPart chest2;

    public BambooMediumSailboatModel(ModelPart root) {
        this.body = root.getChild("body");

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

        this.chest1 = root.getChild("chest");
        this.chest2 = root.getChild("chest2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition paddle_left = partdefinition.addOrReplaceChild("paddle_left", CubeListBuilder.create().texOffs(104, 148).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(104, 148).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 13.0F, -19.0F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition paddle_left_back = partdefinition.addOrReplaceChild("paddle_left_back", CubeListBuilder.create().texOffs(104, 148).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(104, 148).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 13.0F, 12.0F, 2.1642F, 0.8727F, 2.8798F));

        PartDefinition paddle_right = partdefinition.addOrReplaceChild("paddle_right", CubeListBuilder.create().texOffs(144, 148).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(144, 148).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 13.0F, -19.0F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition paddle_right_back = partdefinition.addOrReplaceChild("paddle_right_back", CubeListBuilder.create().texOffs(144, 148).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(144, 148).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 13.0F, 12.0F, 2.1642F, -0.8727F, -2.8798F));

        PartDefinition chest2 = partdefinition.addOrReplaceChild("chest2", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 7.0F, 5.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition chest_knob = chest2.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(59, 148).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition chest_base = chest2.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(0, 148).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 1.0F));

        PartDefinition chest_lid = chest2.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(56, 148).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -3.0F, 1.0F));

        PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 7.0F, 21.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition chest_knob2 = chest.addOrReplaceChild("chest_knob2", CubeListBuilder.create().texOffs(132, 168).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition chest_base2 = chest.addOrReplaceChild("chest_base2", CubeListBuilder.create().texOffs(0, 148).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 1.0F));

        PartDefinition chest_lid2 = chest.addOrReplaceChild("chest_lid2", CubeListBuilder.create().texOffs(56, 148).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -3.0F, 1.0F));

        PartDefinition banner_panel = partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(56, 165).addBox(-7.5F, -13.0F, 0.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -23.0F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition banner_sail = partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(48, 148).addBox(-1.625F, -17.25F, -3.0F, 2.0F, 57.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 168).addBox(-7.625F, -17.25F, -3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(86, 168).addBox(-7.625F, 7.75F, -3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.625F, -18.75F, 1.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 76).addBox(-10.0F, -4.0F, -34.0F, 20.0F, 4.0F, 68.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-12.0F, -8.0F, -36.0F, 24.0F, 4.0F, 72.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(ModBoatEntity boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SailboatPaddleAnimator.animateOarSetsByPassengerCount(
                boat,
                this.getPaddleSets(),
                limbSwing
        );

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
                body,
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
}