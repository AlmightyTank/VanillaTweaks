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
	private final ModelPart visualRoot;
	private final ModelPart paddleLeft;
	private final ModelPart paddleRight;
	private final ModelPart paddleLeftMiddle;
	private final ModelPart paddleRightMiddle;
	private final ModelPart paddleRightBack;
	private final ModelPart paddleLeftBack;
	private final ModelPart bannerSailFront;
	private final ModelPart bannerPanelFront;
	private final ModelPart bannerSailRear;
	private final ModelPart bannerPanelRear;
	private final ModelPart waterPatch;

	private final float leftLargePaddleBaseX;
	private final float leftLargePaddleBaseY;
	private final float leftLargePaddleBaseZ;

	private final float rightLargePaddleBaseX;
	private final float rightLargePaddleBaseY;
	private final float rightLargePaddleBaseZ;

	public LargeSailboatModel(ModelPart root) {
		this.visualRoot = root.getChild("visual_root");

		this.paddleLeft = root.getChild("left_paddle_front");
		this.paddleRight = root.getChild("right_paddle_front");
		this.paddleLeftMiddle = root.getChild("left_paddle_middle");
		this.paddleRightMiddle = root.getChild("right_paddle_middle");
		this.paddleRightBack = root.getChild("right_paddle_back");
		this.paddleLeftBack = root.getChild("left_paddle_back");

		this.leftLargePaddleBaseX = this.paddleLeft.xRot;
		this.leftLargePaddleBaseY = this.paddleLeft.yRot;
		this.leftLargePaddleBaseZ = this.paddleLeft.zRot;

		this.rightLargePaddleBaseX = this.paddleRight.xRot;
		this.rightLargePaddleBaseY = this.paddleRight.yRot;
		this.rightLargePaddleBaseZ = this.paddleRight.zRot;

		this.bannerSailFront = root.getChild("banner_sail_front");
		this.bannerPanelFront = root.getChild("banner_panel_front");
		this.bannerSailRear = root.getChild("banner_sail_rear");
		this.bannerPanelRear = root.getChild("banner_panel_rear");

		this.waterPatch = root.getChild("water_patch");
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
				.texOffs(215, 225).addBox(-59.0F, -6.0F, -6.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -6.0F, -1.0F, -1.5708F, 0.0F, 3.1416F));

		PartDefinition visual_root = partdefinition.addOrReplaceChild("visual_root", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition left = visual_root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 27).addBox(-46.0F, -7.0F, -1.0F, 68.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.0F, -2.0F, -13.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition right = visual_root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 35).addBox(-22.0F, -7.0F, -1.0F, 68.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -2.0F, -13.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition front = visual_root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(56, 102).addBox(5.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(90, 111).addBox(7.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(46, 102).addBox(8.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(110, 73).addBox(-14.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(110, 82).addBox(-15.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(100, 103).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(8, 105).addBox(3.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(56, 110).addBox(-9.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(82, 111).addBox(-11.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(16, 90).addBox(-7.0F, -7.0F, 11.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(74, 111).addBox(-13.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -2.0F, 34.0F));

		PartDefinition back = visual_root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(112, 47).addBox(5.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(98, 112).addBox(7.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(110, 91).addBox(8.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(110, 100).addBox(-14.0F, -7.0F, 2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(110, 109).addBox(-15.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(46, 111).addBox(9.0F, -7.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(8, 113).addBox(3.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(26, 115).addBox(-9.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(34, 115).addBox(-11.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(40, 94).addBox(-7.0F, -7.0F, 11.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(64, 115).addBox(-13.0F, -7.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, -36.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition bottom = visual_root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-46.0F, -13.0F, -3.0F, 68.0F, 24.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(76, 63).addBox(22.0F, -12.0F, -3.0F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(88, 63).addBox(-49.0F, -12.0F, -3.0F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(76, 88).addBox(25.0F, -11.0F, -3.0F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(16, 98).addBox(28.0F, -10.0F, -3.0F, 2.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(100, 73).addBox(30.0F, -8.0F, -3.0F, 2.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(36, 102).addBox(32.0F, -6.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(88, 88).addBox(-52.0F, -11.0F, -3.0F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(64, 94).addBox(-54.0F, -10.0F, -3.0F, 2.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(26, 98).addBox(-56.0F, -8.0F, -3.0F, 2.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(100, 90).addBox(-58.0F, -6.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, -13.0F, 0.0F, 1.5708F, -1.5708F));

		PartDefinition left_paddle_front = partdefinition.addOrReplaceChild("left_paddle_front", CubeListBuilder.create().texOffs(16, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(96, 47).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-24.0F, -8.0F, 12.0F, -0.6409F, 0.6699F, 0.2139F));

		PartDefinition right_paddle_front = partdefinition.addOrReplaceChild("right_paddle_front", CubeListBuilder.create().texOffs(56, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(100, 60).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-24.0F, -8.0F, -12.0F, 2.5007F, 0.6699F, -2.9277F));

		PartDefinition left_paddle_middle = partdefinition.addOrReplaceChild("left_paddle_middle", CubeListBuilder.create().texOffs(16, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(96, 47).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.0F, 12.0F, -0.6409F, 0.6699F, 0.2139F));

		PartDefinition right_paddle_middle = partdefinition.addOrReplaceChild("right_paddle_middle", CubeListBuilder.create().texOffs(56, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(100, 60).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.0F, -12.0F, 2.5007F, 0.6699F, -2.9277F));

		PartDefinition right_paddle_back = partdefinition.addOrReplaceChild("right_paddle_back", CubeListBuilder.create().texOffs(56, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(100, 60).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(26.0F, -8.0F, -12.0F, 2.5007F, 0.6699F, -2.9277F));

		PartDefinition left_paddle_back = partdefinition.addOrReplaceChild("left_paddle_back", CubeListBuilder.create().texOffs(16, 43).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(96, 47).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(26.0F, -8.0F, 12.0F, -0.6409F, 0.6699F, 0.2139F));

		PartDefinition banner_sail_front = partdefinition.addOrReplaceChild("banner_sail_front", CubeListBuilder.create().texOffs(8, 43).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(96, 43).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(96, 45).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition banner_panel_front = partdefinition.addOrReplaceChild("banner_panel_front", CubeListBuilder.create().texOffs(46, 63).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, -32.0F, 6.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition banner_sail_rear = partdefinition.addOrReplaceChild("banner_sail_rear", CubeListBuilder.create().texOffs(0, 43).addBox(-1.0F, -79.0F, -5.0F, 2.0F, 78.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(40, 90).addBox(-7.0F, -79.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(40, 92).addBox(-7.0F, -54.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition banner_panel_rear = partdefinition.addOrReplaceChild("banner_panel_rear", CubeListBuilder.create().texOffs(16, 63).addBox(-13.0F, -44.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -32.0F, 6.0F, 0.0F, -1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Boat boat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		animateLargePaddle(
				boat,
				0,
				this.paddleLeft,
				limbSwing,
				this.leftLargePaddleBaseX,
				this.leftLargePaddleBaseY,
				this.leftLargePaddleBaseZ
		);

		animateLargePaddle(
				boat,
				1,
				this.paddleRight,
				limbSwing,
				this.rightLargePaddleBaseX,
				this.rightLargePaddleBaseY,
				this.rightLargePaddleBaseZ
		);

		animateLargePaddle(
				boat,
				0,
				this.paddleLeftMiddle,
				limbSwing,
				this.leftLargePaddleBaseX,
				this.leftLargePaddleBaseY,
				this.leftLargePaddleBaseZ
		);

		animateLargePaddle(
				boat,
				1,
				this.paddleRightMiddle,
				limbSwing,
				this.rightLargePaddleBaseX,
				this.rightLargePaddleBaseY,
				this.rightLargePaddleBaseZ
		);

		animateLargePaddle(
				boat,
				0,
				this.paddleLeftBack,
				limbSwing,
				this.leftLargePaddleBaseX,
				this.leftLargePaddleBaseY,
				this.leftLargePaddleBaseZ
		);

		animateLargePaddle(
				boat,
				1,
				this.paddleRightBack,
				limbSwing,
				this.rightLargePaddleBaseX,
				this.rightLargePaddleBaseY,
				this.rightLargePaddleBaseZ
		);
	}

	@Override
	public Iterable<ModelPart> parts() {
		return ImmutableList.of(visualRoot, paddleLeft, paddleRight, paddleLeftMiddle, paddleRightMiddle, paddleLeftBack, paddleRightBack, bannerPanelFront, bannerSailFront, bannerPanelRear, bannerSailRear);
	}

	@Override
	public ModelPart waterPatch() {
		return this.waterPatch;
	}

	private static void animateLargePaddle(
			Boat boat,
			int side,
			ModelPart paddle,
			float limbSwing,
			float baseX,
			float baseY,
			float baseZ
	) {
		float f = boat.getRowingTime(side, limbSwing);

		float xAmount = 0.55F;
		float yAmount = 0.45F;

		float xDelta = Mth.sin(-f) * xAmount;
		float yDelta = (Mth.sin(-f + 1.0F) - Mth.sin(1.0F)) * yAmount;

		paddle.xRot = baseX + xDelta;

		// Left side was good, so use the same yRot direction for right too.
		paddle.yRot = baseY + yDelta;

		paddle.zRot = baseZ;
	}
}