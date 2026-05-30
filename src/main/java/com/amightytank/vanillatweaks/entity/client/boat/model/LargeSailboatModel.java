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
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.List;

public class LargeSailboatModel extends ListModel<ModBoatEntity> implements WaterPatchModel, BoatBannerModel, SailboatPaddleModel {
	private final ModelPart waterPatch;

	private final ModelPart left;
	private final ModelPart right;
	private final ModelPart front;
	private final ModelPart back;
	private final ModelPart bottom;

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

	public LargeSailboatModel(ModelPart root) {
		this.waterPatch = root.getChild("water_patch");

		this.left = root.getChild("left");
		this.right = root.getChild("right");
		this.front = root.getChild("front");
		this.back = root.getChild("back");
		this.bottom = root.getChild("bottom");

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

		PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 226).addBox(-47.0F, -13.0F, -6.0F, 68.0F, 24.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(228, 178).addBox(21.0F, -12.0F, -6.0F, 3.0F, 22.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(195, 218).addBox(-50.0F, -12.0F, -6.0F, 3.0F, 22.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(205, 218).addBox(24.0F, -11.0F, -6.0F, 3.0F, 20.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(205, 218).addBox(27.0F, -10.0F, -6.0F, 2.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(205, 218).addBox(29.0F, -8.0F, -6.0F, 2.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(205, 218).addBox(31.0F, -6.0F, -6.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(215, 225).addBox(-53.0F, -11.0F, -6.0F, 3.0F, 20.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(215, 225).addBox(-55.0F, -10.0F, -6.0F, 2.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(215, 225).addBox(-57.0F, -8.0F, -6.0F, 2.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(215, 225).addBox(-59.0F, -6.0F, -6.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.3276F, 15.0F, 11.5092F, 0.0F, -1.5708F, 1.5708F));

		PartDefinition left = partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-46.0F, -7.0F, -1.0F, 68.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(12.6724F, 22.0F, -13.4908F, 0.0F, 1.5708F, 0.0F));

		PartDefinition right = partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 35).mirror().addBox(-22.0F, -7.0F, -1.0F, 68.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-13.3276F, 22.0F, -13.4908F, 0.0F, -1.5708F, 0.0F));

		PartDefinition front = partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(84, 121).addBox(5.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(122, 122).addBox(7.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(112, 95).addBox(8.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(102, 114).addBox(-14.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(112, 114).addBox(-15.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(92, 114).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(122, 82).addBox(3.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(122, 90).addBox(-9.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(122, 114).addBox(-11.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 66).addBox(-7.0F, -7.0F, 11.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(122, 98).addBox(-13.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6724F, 22.0F, 33.5092F, 0.0F, 0.0F, 0.0F));

		PartDefinition back = partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 123).addBox(5.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(64, 123).addBox(7.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(116, 43).addBox(8.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(116, 52).mirror().addBox(-14.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(28, 107).addBox(-15.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(74, 121).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(92, 123).addBox(3.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(100, 123).addBox(-9.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(108, 123).addBox(-11.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(100, 106).addBox(-7.0F, -7.0F, 11.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(124, 106).addBox(-13.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3276F, 22.0F, -36.4908F, -3.1416F, 0.0F, -3.1416F));

		PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-46.0F, -13.0F, -3.0F, 68.0F, 24.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(76, 83).addBox(22.0F, -12.0F, -3.0F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(88, 83).addBox(-49.0F, -12.0F, -3.0F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(100, 83).addBox(25.0F, -11.0F, -3.0F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(18, 107).addBox(28.0F, -10.0F, -3.0F, 2.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(54, 110).addBox(30.0F, -8.0F, -3.0F, 2.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(112, 82).addBox(32.0F, -6.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(104, 43).addBox(-52.0F, -11.0F, -3.0F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(8, 107).addBox(-54.0F, -10.0F, -3.0F, 2.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(44, 110).addBox(-56.0F, -8.0F, -3.0F, 2.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(64, 110).addBox(-58.0F, -6.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6724F, 21.0F, -13.4908F, 0.0F, 1.5708F, -1.5708F));

		PartDefinition left_paddle_back = partdefinition.addOrReplaceChild("left_paddle_back", CubeListBuilder.create().texOffs(64, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(64, 43).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.6724F, 13.0F, 23.5092F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition right_paddle_back = partdefinition.addOrReplaceChild("right_paddle_back", CubeListBuilder.create().texOffs(64, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(64, 63).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.3276F, 13.0F, 23.5092F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition left_paddle_middle = partdefinition.addOrReplaceChild("left_paddle_middle", CubeListBuilder.create().texOffs(64, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(64, 43).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.6724F, 13.0F, -1.4908F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition right_paddle_middle = partdefinition.addOrReplaceChild("right_paddle_middle", CubeListBuilder.create().texOffs(64, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(64, 63).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.3276F, 13.0F, -1.4908F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition right_paddle_front = partdefinition.addOrReplaceChild("right_paddle_front", CubeListBuilder.create().texOffs(64, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(64, 63).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.3276F, 13.0F, -26.4908F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition left_paddle_front = partdefinition.addOrReplaceChild("left_paddle_front", CubeListBuilder.create().texOffs(64, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(64, 43).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.6724F, 13.0F, -26.4908F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition banner_sail_front = partdefinition.addOrReplaceChild("banner_sail_front", CubeListBuilder.create().texOffs(8, 43).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 74).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(104, 74).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3276F, 24.0F, -17.4908F, -3.1416F, 0.0F, -3.1416F));

		PartDefinition banner_panel_front = partdefinition.addOrReplaceChild("banner_panel_front", CubeListBuilder.create().texOffs(46, 83).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3276F, -24.0F, -14.9908F, 0.0F, 0.0F, 0.0F));

		PartDefinition banner_sail_rear = partdefinition.addOrReplaceChild("banner_sail_rear", CubeListBuilder.create().texOffs(0, 43).addBox(-1.0F, -79.0F, -5.0F, 2.0F, 78.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 74).addBox(-7.0F, -79.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(104, 74).addBox(-7.0F, -54.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3276F, 24.0F, 7.5092F, -3.1416F, 0.0F, -3.1416F));

		PartDefinition banner_panel_rear = partdefinition.addOrReplaceChild("banner_panel_rear", CubeListBuilder.create().texOffs(16, 80).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3276F, -42.0F, 10.0092F, 0.0F, 0.0F, 0.0F));

		PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.3276F, 13.0F, 30.5092F, 0.0F, 0.0F, 0.0F));

		PartDefinition chest_base = chest.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(16, 43).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition chest_knob = chest.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(92, 108).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, -1.0F));

		PartDefinition chest_lid = chest.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(16, 63).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition chest2 = partdefinition.addOrReplaceChild("chest2", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.3276F, 13.0F, 14.5092F, 0.0F, 0.0F, 0.0F));

		PartDefinition chest_base2 = chest2.addOrReplaceChild("chest_base2", CubeListBuilder.create().texOffs(16, 43).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition chest_knob2 = chest2.addOrReplaceChild("chest_knob2", CubeListBuilder.create().texOffs(92, 108).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, -1.0F));

		PartDefinition chest_lid2 = chest2.addOrReplaceChild("chest_lid2", CubeListBuilder.create().texOffs(16, 63).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition chest3 = partdefinition.addOrReplaceChild("chest3", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.3276F, 13.0F, -6.4908F, 0.0F, 0.0F, 0.0F));

		PartDefinition chest_base3 = chest3.addOrReplaceChild("chest_base3", CubeListBuilder.create().texOffs(16, 43).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition chest_knob3 = chest3.addOrReplaceChild("chest_knob3", CubeListBuilder.create().texOffs(92, 108).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, -1.0F));

		PartDefinition chest_lid3 = chest3.addOrReplaceChild("chest_lid3", CubeListBuilder.create().texOffs(16, 63).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 148, 148);
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
				left,
				right,
				front,
				back,
				bottom,
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

	@Override
	public ModelPart waterPatch() {
		return this.waterPatch;
	}
}