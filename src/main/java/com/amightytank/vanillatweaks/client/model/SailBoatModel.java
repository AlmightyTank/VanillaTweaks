package com.amightytank.vanillatweaks.client.model;

import com.amightytank.vanillatweaks.entity.SailBoatEntity;
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

public class SailBoatModel<T extends SailBoatEntity> extends EntityModel<T> implements WaterPatchModel {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(new ResourceLocation("vanillatweaks", "sail_boat"), "main");

	private final ModelPart front;
	private final ModelPart left;
	private final ModelPart right;
	private final ModelPart back;
	private final ModelPart bottom;
	private final ModelPart paddle_left;
	private final ModelPart paddle_right;
	private final ModelPart banner_sail;
	private final ModelPart banner_panel;
	private final ModelPart water_patch;

	public SailBoatModel(ModelPart root) {
		this.front = root.getChild("front");
		this.left = root.getChild("left");
		this.right = root.getChild("right");
		this.back = root.getChild("back");
		this.bottom = root.getChild("bottom");
		this.paddle_left = root.getChild("paddle_left");
		this.paddle_right = root.getChild("paddle_right");
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
						.texOffs(62, 44)
						.addBox(-9.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 22.0F, -15.0F, 0.0F, -3.1416F, 0.0F));

		partdefinition.addOrReplaceChild("left",
				CubeListBuilder.create()
						.texOffs(8, 55)
						.addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(9.0F, 22.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		partdefinition.addOrReplaceChild("right",
				CubeListBuilder.create()
						.texOffs(62, 36)
						.addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-9.0F, 22.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		partdefinition.addOrReplaceChild("back",
				CubeListBuilder.create()
						.texOffs(68, 52)
						.addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(4.0F, 22.0F, 15.0F));

		partdefinition.addOrReplaceChild("bottom",
				CubeListBuilder.create()
						.texOffs(0, 36)
						.addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.0F, 21.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

		partdefinition.addOrReplaceChild("paddle_left",
				CubeListBuilder.create()
						.texOffs(8, 63)
						.addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
						.texOffs(68, 83)
						.addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(9.0F, 13.0F, -3.0F, 2.1642F, 0.8727F, 2.8798F));

		partdefinition.addOrReplaceChild("paddle_right",
				CubeListBuilder.create()
						.texOffs(48, 63)
						.addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
						.texOffs(84, 83)
						.addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-9.0F, 13.0F, -3.0F, 2.1642F, -0.8727F, -2.8798F));

		partdefinition.addOrReplaceChild("banner_sail",
				CubeListBuilder.create()
						.texOffs(0, 55)
						.addBox(-1.0F, -17.25F, -1.625F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(68, 60)
						.addBox(-7.0F, -17.25F, -0.625F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(38, 83)
						.addBox(-7.0F, 7.75F, -0.625F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -18.75F, 11.375F, 0.0F, 3.1416F, 0.0F));

		partdefinition.addOrReplaceChild("banner_panel",
				CubeListBuilder.create()
						.texOffs(8, 83)
						.addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -23.0F, 10.5F));

		partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(3, 2).addBox(-8.0F, -1.0F, -15.0F, 16.0F, 2.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 1.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
	                      float ageInTicks, float netHeadYaw, float headPitch) {
		// Hide placeholder cloth when a crafted banner exists.
		// Renderer draws the real banner pattern instead.
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

		animateOar(paddle_left, ageInTicks, leftPower, true, direction);
		animateOar(paddle_right, ageInTicks, rightPower, false, direction);
	}

	private void animateOar(ModelPart paddle, float ageInTicks, float power,
	                        boolean leftSide, float direction) {
		float baseX = 2.1642F;
		float baseY = leftSide ? 0.8727F : -0.8727F;
		float baseZ = leftSide ? 2.8798F : -2.8798F;

		if (power <= 0.0F) {
			paddle.xRot = baseX;
			paddle.yRot = baseY;
			paddle.zRot = baseZ;
			return;
		}

		float cycle = ageInTicks * 0.75F;
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

		banner_sail.render(poseStack, vertexConsumer, packedLight, packedOverlay);

		if (banner_panel.visible) {
			banner_panel.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		}

		// Do NOT render water_patch here.
		// SailBoatRenderer renders it with RenderType.waterMask().
	}
}