package com.amightytank.vanillatweaks.client.model;

import com.amightytank.vanillatweaks.entity.MediumBoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MediumBoatModel<T extends MediumBoatEntity> extends EntityModel<T> implements WaterPatchModel {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(new ResourceLocation("vanillatweaks", "medium_boat"), "main");

	private final ModelPart front;
	private final ModelPart left;
	private final ModelPart right;
	private final ModelPart back;
	private final ModelPart bottom;
	private final ModelPart paddle_left;
	private final ModelPart paddle_right;
	private final ModelPart paddle_left_back;
	private final ModelPart paddle_right_back;
	private final ModelPart banner_sail;
	private final ModelPart banner_panel;
	private final ModelPart water_patch;

	public MediumBoatModel(ModelPart root) {
		this.front = root.getChild("front");
		this.left = root.getChild("left");
		this.right = root.getChild("right");
		this.back = root.getChild("back");
		this.bottom = root.getChild("bottom");
		this.paddle_left = root.getChild("paddle_left");
		this.paddle_right = root.getChild("paddle_right");
		this.paddle_left_back = root.getChild("paddle_left_back");
		this.paddle_right_back = root.getChild("paddle_right_back");
		this.banner_sail = root.getChild("banner_sail");
		this.banner_panel = root.getChild("banner_panel");
		this.water_patch = root.getChild("water_patch");
	}

	public ModelPart getBannerPanel() {
		return this.banner_panel;
	}

	@Override
	public ModelPart waterPatch() {
		return this.water_patch;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("front",
				CubeListBuilder.create()
						.texOffs(92, 23).addBox(6.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(92, 31).addBox(4.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(94, 0).addBox(-10.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(38, 79).addBox(-8.0F, -7.0F, 11.0F, 12.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(94, 8).addBox(-12.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.0F, 22.0F, -15.0F, 0.0F, -3.1416F, 0.0F));

		partdefinition.addOrReplaceChild("left",
				CubeListBuilder.create()
						.texOffs(0, 23).addBox(-22.0F, -7.0F, -1.0F, 44.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(11.0F, 22.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		partdefinition.addOrReplaceChild("right",
				CubeListBuilder.create()
						.texOffs(0, 31).addBox(-22.0F, -7.0F, -1.0F, 44.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-11.0F, 22.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		partdefinition.addOrReplaceChild("back",
				CubeListBuilder.create()
						.texOffs(94, 78).addBox(6.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(94, 89).addBox(4.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(94, 97).addBox(-10.0F, -7.0F, 9.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(66, 79).addBox(-8.0F, -7.0F, 11.0F, 12.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(100, 16).addBox(-12.0F, -7.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(2.0F, 22.0F, 15.0F));

		partdefinition.addOrReplaceChild("bottom",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-22.0F, -11.0F, -3.0F, 44.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(38, 89).addBox(22.0F, -9.0F, -3.0F, 2.0F, 16.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(48, 89).addBox(-24.0F, -9.0F, -3.0F, 2.0F, 16.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(74, 89).addBox(24.0F, -7.0F, -3.0F, 2.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(84, 89).addBox(-26.0F, -7.0F, -3.0F, 2.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.0F, 21.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

		partdefinition.addOrReplaceChild("paddle_left",
				CubeListBuilder.create()
						.texOffs(8, 39).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
						.texOffs(88, 39).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(10.0F, 13.0F, -13.0F, 2.1642F, 0.8727F, 2.8798F));

		partdefinition.addOrReplaceChild("paddle_right",
				CubeListBuilder.create()
						.texOffs(48, 39).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
						.texOffs(88, 52).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-10.0F, 13.0F, -13.0F, 2.1642F, -0.8727F, -2.8798F));

		partdefinition.addOrReplaceChild("paddle_left_back",
				CubeListBuilder.create()
						.texOffs(8, 59).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
						.texOffs(88, 65).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(10.0F, 13.0F, 12.0F, 2.1642F, 0.8727F, 2.8798F));

		partdefinition.addOrReplaceChild("paddle_right_back",
				CubeListBuilder.create()
						.texOffs(48, 59).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
						.texOffs(58, 89).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-10.0F, 13.0F, 12.0F, 2.1642F, -0.8727F, -2.8798F));

		partdefinition.addOrReplaceChild("banner_sail",
				CubeListBuilder.create()
						.texOffs(0, 39).addBox(-1.0F, -61.0F, -5.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(38, 87).addBox(-7.0F, -61.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(68, 87).addBox(-7.0F, -36.0F, -4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		partdefinition.addOrReplaceChild("banner_panel",
				CubeListBuilder.create()
						.texOffs(8, 79).addBox(-13.0F, -26.0F, -1.0F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(6.0F, -11.0F, 3.0F));

		partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(-52, -42).addBox(-11.0F, -3.0F, -22.0F, 22.0F, 6.0F, 44.0F, new CubeDeformation(0.0F))
				.texOffs(-8, 0).addBox(-9.0F, -3.0F, 22.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(-8, 0).addBox(-9.0F, -3.0F, -24.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(-6, 0).addBox(-7.0F, -3.0F, 24.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(-6, 0).addBox(-7.0F, -3.0F, -26.0F, 14.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
	                      float ageInTicks, float netHeadYaw, float headPitch) {
		banner_panel.visible = entity.getBannerStack().isEmpty();

		boolean forward = entity.isInputForward();
		boolean back = entity.isInputBack();
		boolean left = entity.isInputLeft();
		boolean right = entity.isInputRight();

		float direction = back ? -1.0F : 1.0F;

		float leftPower = 0.0F;
		float rightPower = 0.0F;

		if (forward || back) {
			leftPower = 1.0F;
			rightPower = 1.0F;
		}

		if (left) {
			rightPower = 1.0F;
		}

		if (right) {
			leftPower = 1.0F;
		}

		animateBoatOar(paddle_left, ageInTicks, leftPower, true, direction, 0.0F);
		animateBoatOar(paddle_right, ageInTicks, rightPower, false, direction, 0.0F);
		animateBoatOar(paddle_left_back, ageInTicks, leftPower, true, direction, 0.7F);
		animateBoatOar(paddle_right_back, ageInTicks, rightPower, false, direction, 0.7F);
	}

	private void animateBoatOar(ModelPart paddle, float ageInTicks, float power,
	                            boolean leftSide, float direction, float offset) {
		float baseX = 2.1642F;
		float baseY = leftSide ? 0.8727F : -0.8727F;
		float baseZ = leftSide ? 2.8798F : -2.8798F;

		if (power <= 0.0F) {
			paddle.xRot = baseX;
			paddle.yRot = baseY;
			paddle.zRot = baseZ;
			return;
		}

		float cycle = ageInTicks * 0.75F + offset;
		float sweep = Mth.sin(cycle) * power * direction;
		float lift = Mth.cos(cycle) * power;

		paddle.xRot = baseX + sweep * 0.55F;
		paddle.yRot = leftSide ? baseY + lift * 0.35F : baseY - lift * 0.35F;
		paddle.zRot = baseZ;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
	                           int packedLight, int packedOverlay,
	                           float red, float green, float blue, float alpha) {
		front.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		left.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		right.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		back.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay);

		paddle_left.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		paddle_right.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		paddle_left_back.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		paddle_right_back.render(poseStack, vertexConsumer, packedLight, packedOverlay);

		banner_sail.render(poseStack, vertexConsumer, packedLight, packedOverlay);

		if (banner_panel.visible) {
			banner_panel.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		}
	}
}