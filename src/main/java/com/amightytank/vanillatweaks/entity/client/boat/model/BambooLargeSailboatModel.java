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

public class BambooLargeSailboatModel extends ListModel<ModBoatEntity> implements BoatBannerModel, SailboatPaddleModel {
	private final ModelPart body;

	private final ModelPart paddleLeft;
	private final ModelPart paddleRight;
	private final ModelPart paddleLeftMiddle;
	private final ModelPart paddleRightMiddle;
	private final ModelPart paddleRightBack;
	private final ModelPart paddleLeftBack;

	private final SailboatPaddleAnimator.PaddleBase paddleLeftBase;
	private final SailboatPaddleAnimator.PaddleBase paddleRightBase;
	private final SailboatPaddleAnimator.PaddleBase paddleLeftMiddleBase;
	private final SailboatPaddleAnimator.PaddleBase paddleRightMiddleBase;
	private final SailboatPaddleAnimator.PaddleBase paddleLeftBackBase;
	private final SailboatPaddleAnimator.PaddleBase paddleRightBackBase;

	private final ModelPart bannerSailFront;
	private final ModelPart bannerPanelFront;
	private final ModelPart bannerSailRear;
	private final ModelPart bannerPanelRear;

	private final ModelPart chest1;
	private final ModelPart chest2;
	private final ModelPart chest3;

	public BambooLargeSailboatModel(ModelPart root) {
		this.body = root.getChild("body");

		this.paddleLeft = root.getChild("left_paddle_front");
		this.paddleRight = root.getChild("right_paddle_front");
		this.paddleLeftMiddle = root.getChild("left_paddle_middle");
		this.paddleRightMiddle = root.getChild("right_paddle_middle");
		this.paddleLeftBack = root.getChild("left_paddle_back");
		this.paddleRightBack = root.getChild("right_paddle_back");

		this.paddleLeftBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeft);
		this.paddleRightBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRight);
		this.paddleLeftMiddleBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeftMiddle);
		this.paddleRightMiddleBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRightMiddle);
		this.paddleLeftBackBase = new SailboatPaddleAnimator.PaddleBase(this.paddleLeftBack);
		this.paddleRightBackBase = new SailboatPaddleAnimator.PaddleBase(this.paddleRightBack);

		this.bannerSailFront = root.getChild("banner_sail_front");
		this.bannerPanelFront = root.getChild("banner_panel_front");
		this.bannerSailRear = root.getChild("banner_sail_rear");
		this.bannerPanelRear = root.getChild("banner_panel_rear");
		this.bannerPanelFront.visible = false;
		this.bannerPanelRear.visible = false;

		this.chest1 = root.getChild("chest");
		this.chest2 = root.getChild("chest2");
		this.chest3 = root.getChild("chest3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition left_paddle_back = partdefinition.addOrReplaceChild("left_paddle_back", CubeListBuilder.create().texOffs(144, 196).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(144, 196).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.6724F, 14.0F, 23.5092F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition right_paddle_back = partdefinition.addOrReplaceChild("right_paddle_back", CubeListBuilder.create().texOffs(104, 196).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(104, 196).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.3276F, 14.0F, 23.5092F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition left_paddle_middle = partdefinition.addOrReplaceChild("left_paddle_middle", CubeListBuilder.create().texOffs(144, 196).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(144, 196).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.6724F, 14.0F, -1.4908F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition right_paddle_middle = partdefinition.addOrReplaceChild("right_paddle_middle", CubeListBuilder.create().texOffs(104, 196).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(104, 196).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.3276F, 14.0F, -1.4908F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition right_paddle_front = partdefinition.addOrReplaceChild("right_paddle_front", CubeListBuilder.create().texOffs(104, 196).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(104, 196).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.3276F, 14.0F, -26.4908F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition left_paddle_front = partdefinition.addOrReplaceChild("left_paddle_front", CubeListBuilder.create().texOffs(144, 196).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(144, 196).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.6724F, 14.0F, -26.4908F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition banner_sail_front = partdefinition.addOrReplaceChild("banner_sail_front", CubeListBuilder.create().texOffs(146, 106).addBox(-1.0F, -60.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(158, 230).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(222, 168).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6724F, 23.0F, -17.4908F, -3.1416F, 0.0F, -3.1416F));

		PartDefinition banner_panel_front = partdefinition.addOrReplaceChild("banner_panel_front", CubeListBuilder.create().texOffs(184, 196).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6724F, -25.0F, -14.9908F, 0.0F, 0.0F, 0.0F));

		PartDefinition banner_sail_rear = partdefinition.addOrReplaceChild("banner_sail_rear", CubeListBuilder.create().texOffs(146, 106).addBox(-1.0F, -79.0F, -5.0F, 2.0F, 78.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(56, 213).addBox(-7.0F, -79.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(214, 196).addBox(-7.0F, -54.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6724F, 23.0F, 7.5092F, -3.1416F, 0.0F, -3.1416F));

		PartDefinition banner_panel_rear = partdefinition.addOrReplaceChild("banner_panel_rear", CubeListBuilder.create().texOffs(184, 196).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6724F, -43.0F, 10.0092F, 0.0F, 0.0F, 0.0F));

		PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.3276F, 8.0F, 30.5092F, 0.0F, 0.0F, 0.0F));

		PartDefinition chest_base = chest.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(8, 196).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition chest_knob = chest.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(214, 211).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, -1.0F));

		PartDefinition chest_lid = chest.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(56, 196).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition chest2 = partdefinition.addOrReplaceChild("chest2", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.3276F, 8.0F, 14.5092F, 0.0F, 0.0F, 0.0F));

		PartDefinition chest_base2 = chest2.addOrReplaceChild("chest_base2", CubeListBuilder.create().texOffs(8, 196).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition chest_knob2 = chest2.addOrReplaceChild("chest_knob2", CubeListBuilder.create().texOffs(214, 211).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, -1.0F));

		PartDefinition chest_lid2 = chest2.addOrReplaceChild("chest_lid2", CubeListBuilder.create().texOffs(56, 196).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition chest3 = partdefinition.addOrReplaceChild("chest3", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.3276F, 8.0F, -6.4908F, 0.0F, 0.0F, 0.0F));

		PartDefinition chest_base3 = chest3.addOrReplaceChild("chest_base3", CubeListBuilder.create().texOffs(8, 196).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition chest_knob3 = chest3.addOrReplaceChild("chest_knob3", CubeListBuilder.create().texOffs(214, 211).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, -1.0F));

		PartDefinition chest_lid3 = chest3.addOrReplaceChild("chest_lid3", CubeListBuilder.create().texOffs(56, 196).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-16.5F, -7.0F, -50.0F, 28.0F, 4.0F, 97.0F, new CubeDeformation(0.0F))
				.texOffs(0, 101).addBox(-14.5F, -3.0F, -47.0F, 24.0F, 4.0F, 91.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 23.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(ModBoatEntity boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeft, limbSwing, this.paddleLeftBase);
		SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRight, limbSwing, this.paddleRightBase);

		SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeftMiddle, limbSwing, this.paddleLeftMiddleBase);
		SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRightMiddle, limbSwing, this.paddleRightMiddleBase);

		SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeftBack, limbSwing, this.paddleLeftBackBase);
		SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRightBack, limbSwing, this.paddleRightBackBase);

		this.chest1.visible = boat.getChestCount() >= 1;
		this.chest2.visible = boat.getChestCount() >= 2;
		this.chest3.visible = boat.getChestCount() >= 3;
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
						this.paddleLeftMiddle,
						this.paddleRightMiddle,
						this.paddleLeftMiddleBase,
						this.paddleRightMiddleBase
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
	public void translateToBannerPanel(PoseStack poseStack) {
		this.bannerPanelFront.translateAndRotate(poseStack);
	}

	@Override
	public void translateToRearBannerPanel(PoseStack poseStack) {
		this.bannerPanelRear.translateAndRotate(poseStack);
	}

	@Override
	public boolean hasRearBannerPanel() {
		return true;
	}

	@Override
	public Iterable<ModelPart> parts() {
		return ImmutableList.of(
				body,
				chest1,
				chest2,
				chest3,
				paddleLeft,
				paddleRight,
				paddleLeftMiddle,
				paddleRightMiddle,
				paddleLeftBack,
				paddleRightBack,
				bannerSailFront,
				bannerSailRear
		);
	}
}