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

public class LargeSailboatModel extends ListModel<Boat> implements WaterPatchModel {
	private final ModelPart visualRoot;
	private final ModelPart paddleLeft;
	private final ModelPart paddleRight;
	private final ModelPart paddleLeftMiddle;
	private final ModelPart paddleRightMiddle;
	private final ModelPart paddleRightBack;
	private final ModelPart paddleLeftBack;
	private final ModelPart waterPatch;

	public LargeSailboatModel(ModelPart root) {
		this.visualRoot = root.getChild("visual_root");

		this.paddleLeft = this.visualRoot.getChild("left_paddle_front");
		this.paddleRight = this.visualRoot.getChild("right_paddle_front");
		this.paddleLeftMiddle = this.visualRoot.getChild("left_paddle_middle");
		this.paddleRightMiddle = this.visualRoot.getChild("right_paddle_middle");
		this.paddleRightBack = this.visualRoot.getChild("right_paddle_back");
		this.paddleLeftBack = this.visualRoot.getChild("left_paddle_back");

		this.waterPatch = root.getChild("water_patch");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(-27, -3).addBox(-47.0F, -13.0F, -6.0F, 68.0F, 24.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(93, 53).addBox(21.0F, -12.0F, -6.0F, 3.0F, 22.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(60, 93).addBox(-50.0F, -12.0F, -6.0F, 3.0F, 22.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(70, 93).addBox(24.0F, -11.0F, -6.0F, 3.0F, 20.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(70, 93).addBox(27.0F, -10.0F, -6.0F, 2.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(70, 93).addBox(29.0F, -8.0F, -6.0F, 2.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(70, 93).addBox(31.0F, -6.0F, -6.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(80, 100).addBox(-53.0F, -11.0F, -6.0F, 3.0F, 20.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(80, 100).addBox(-55.0F, -10.0F, -6.0F, 2.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(80, 100).addBox(-57.0F, -8.0F, -6.0F, 2.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(80, 100).addBox(-59.0F, -6.0F, -6.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, 0.0F, -1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition visual_root = partdefinition.addOrReplaceChild("visual_root", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition left = visual_root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(-24, 23).addBox(-46.0F, -7.0F, -1.0F, 68.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.0F, -2.0F, -13.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition right = visual_root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 31).addBox(-22.0F, -7.0F, -1.0F, 68.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -2.0F, -13.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition front = visual_root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(94, 103).addBox(5.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(94, 103).addBox(7.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(93, 102).addBox(8.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(93, 102).addBox(-14.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(93, 102).addBox(-15.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(93, 102).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(102, 103).addBox(3.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 39).addBox(-9.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 39).addBox(-11.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(93, 31).addBox(-7.0F, -7.0F, 11.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 47).addBox(-13.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -2.0F, 34.0F));

		PartDefinition back = visual_root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(94, 103).addBox(5.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(94, 103).addBox(7.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(93, 102).addBox(8.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(93, 102).addBox(-14.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(93, 102).addBox(-15.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(93, 102).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(102, 103).addBox(3.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 39).addBox(-9.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 39).addBox(-11.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(93, 31).addBox(-7.0F, -7.0F, 11.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(104, 47).addBox(-13.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, -36.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition bottom = visual_root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(-24, 0).addBox(-46.0F, -13.0F, -3.0F, 68.0F, 24.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(96, 56).addBox(22.0F, -12.0F, -3.0F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(63, 96).addBox(-49.0F, -12.0F, -3.0F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(73, 96).addBox(25.0F, -11.0F, -3.0F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(73, 96).addBox(28.0F, -10.0F, -3.0F, 2.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(73, 96).addBox(30.0F, -8.0F, -3.0F, 2.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(73, 96).addBox(32.0F, -6.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(83, 103).addBox(-52.0F, -11.0F, -3.0F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(83, 103).addBox(-54.0F, -10.0F, -3.0F, 2.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(83, 103).addBox(-56.0F, -8.0F, -3.0F, 2.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(83, 103).addBox(-58.0F, -6.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, -13.0F, 0.0F, 1.5708F, -1.5708F));

		PartDefinition left_paddle_front = visual_root.addOrReplaceChild("left_paddle_front", CubeListBuilder.create().texOffs(48, 76).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(8, 99).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -11.0F, 24.0F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition right_paddle_front = visual_root.addOrReplaceChild("right_paddle_front", CubeListBuilder.create().texOffs(8, 79).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(24, 99).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -11.0F, 24.0F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition left_paddle_middle = visual_root.addOrReplaceChild("left_paddle_middle", CubeListBuilder.create().texOffs(48, 76).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(8, 99).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -11.0F, -1.0F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition right_paddle_middle = visual_root.addOrReplaceChild("right_paddle_middle", CubeListBuilder.create().texOffs(8, 79).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(24, 99).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -11.0F, -1.0F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition right_paddle_back = visual_root.addOrReplaceChild("right_paddle_back", CubeListBuilder.create().texOffs(8, 59).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(48, 96).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -11.0F, -26.0F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition left_paddle_back = visual_root.addOrReplaceChild("left_paddle_back", CubeListBuilder.create().texOffs(56, 56).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(94, 4).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -11.0F, -26.0F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition banner_sail_front = visual_root.addOrReplaceChild("banner_sail_front", CubeListBuilder.create().texOffs(0, 39).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(94, 0).addBox(-7.0F, -61.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(94, 2).addBox(-7.0F, -36.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition banner_panel_front = visual_root.addOrReplaceChild("banner_panel_front", CubeListBuilder.create().texOffs(88, 76).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -35.0F, 14.0F));

		PartDefinition banner_sail_rear = visual_root.addOrReplaceChild("banner_sail_rear", CubeListBuilder.create().texOffs(0, 39).addBox(-1.0F, -79.0F, -5.0F, 2.0F, 78.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(94, 0).addBox(-7.0F, -79.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(94, 2).addBox(-7.0F, -54.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -17.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition banner_panel_rear = visual_root.addOrReplaceChild("banner_panel_rear", CubeListBuilder.create().texOffs(88, 76).addBox(-13.0F, -44.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -35.0F, -11.0F));

		return LayerDefinition.create(meshdefinition, 176, 176);
	}

	@Override
	public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeft, limbSwing);
		SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRight, limbSwing);

		SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeftMiddle, limbSwing);
		SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRightMiddle, limbSwing);

		SailboatPaddleAnimator.animatePaddle(boat, 0, this.paddleLeftBack, limbSwing);
		SailboatPaddleAnimator.animatePaddle(boat, 1, this.paddleRightBack, limbSwing);
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