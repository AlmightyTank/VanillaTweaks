package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ShoulderPirateParrotModel<T extends PirateCaptainEntity> extends EntityModel<T> {
	private final ModelPart head;
	private final ModelPart feather;
	private final ModelPart body;
	private final ModelPart left_wing;
	private final ModelPart left_wing_rotation;
	private final ModelPart right_wing;
	private final ModelPart right_wing_rotation;
	private final ModelPart left_leg;
	private final ModelPart right_leg;
	private final ModelPart tail;

	public ShoulderPirateParrotModel(ModelPart root) {
		this.head = root.getChild("head");
		this.feather = this.head.getChild("feather");
		this.body = root.getChild("body");
		this.left_wing = root.getChild("left_wing");
		this.left_wing_rotation = this.left_wing.getChild("left_wing_rotation");
		this.right_wing = root.getChild("right_wing");
		this.right_wing_rotation = this.right_wing.getChild("right_wing_rotation");
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
		this.tail = root.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head",
				CubeListBuilder.create()
						.texOffs(2, 2)
						.addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(10, 0)
						.addBox(-1.0F, -2.5F, -3.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(11, 7)
						.addBox(-0.5F, -1.5F, -2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(16, 7)
						.addBox(-0.5F, -1.75F, -2.95F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.01F)),
				PartPose.offset(0.0F, 16.0F, -0.5F));

		head.addOrReplaceChild("feather",
				CubeListBuilder.create()
						.texOffs(2, 18)
						.addBox(0.0F, -5.0F, 0.0F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -1.5F, -2.0F, -0.2618F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create()
						.texOffs(2, 8)
						.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 16.5F, -1.0F));

		PartDefinition leftWing = partdefinition.addOrReplaceChild("left_wing",
				CubeListBuilder.create(),
				PartPose.offset(1.5F, 16.9F, -0.8F));

		leftWing.addOrReplaceChild("left_wing_rotation",
				CubeListBuilder.create()
						.texOffs(19, 8)
						.addBox(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition rightWing = partdefinition.addOrReplaceChild("right_wing",
				CubeListBuilder.create(),
				PartPose.offset(-1.5F, 16.9F, -0.8F));

		rightWing.addOrReplaceChild("right_wing_rotation",
				CubeListBuilder.create()
						.texOffs(19, 8)
						.addBox(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

		partdefinition.addOrReplaceChild("left_leg",
				CubeListBuilder.create()
						.texOffs(14, 18)
						.addBox(-2.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(1.0F, 22.0F, -1.0F));

		partdefinition.addOrReplaceChild("right_leg",
				CubeListBuilder.create()
						.texOffs(14, 18)
						.addBox(1.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-1.0F, 22.0F, -1.0F));

		partdefinition.addOrReplaceChild("tail",
				CubeListBuilder.create()
						.texOffs(22, 1)
						.addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 21.1F, 1.2F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		resetPose();

		float bob = Mth.sin(ageInTicks * 0.15F) * 0.03F;
		float look = Mth.sin(ageInTicks * 0.18F) * 0.25F;
		float wingFlap = Mth.sin(ageInTicks * 0.05F) * 0.02F;

		this.head.yRot = look;
		this.head.xRot = bob;
		this.feather.xRot = -0.2618F + Mth.sin(ageInTicks * 0.25F) * 0.08F;

		this.left_wing.zRot = 0.02F + wingFlap;
		this.right_wing.zRot = -0.02F - wingFlap;

		this.tail.xRot = 0.15F + Mth.sin(ageInTicks * 0.2F) * 0.05F;
	}

	private void resetPose() {
		this.head.xRot = 0.0F;
		this.head.yRot = 0.0F;
		this.head.zRot = 0.0F;

		this.feather.xRot = -0.2618F;
		this.feather.yRot = 0.0F;
		this.feather.zRot = 0.0F;

		this.body.xRot = 0.0F;
		this.body.yRot = 0.0F;
		this.body.zRot = 0.0F;

		this.left_wing.xRot = 0.0F;
		this.left_wing.yRot = 0.0F;
		this.left_wing.zRot = 0.0F;

		this.left_wing_rotation.xRot = 0.0F;
		this.left_wing_rotation.yRot = 3.1416F;
		this.left_wing_rotation.zRot = 0.0F;

		this.right_wing.xRot = 0.0F;
		this.right_wing.yRot = 0.0F;
		this.right_wing.zRot = 0.0F;

		this.right_wing_rotation.xRot = 0.0F;
		this.right_wing_rotation.yRot = 3.1416F;
		this.right_wing_rotation.zRot = 0.0F;

		this.left_leg.xRot = 0.0F;
		this.left_leg.yRot = 0.0F;
		this.left_leg.zRot = 0.0F;

		this.right_leg.xRot = 0.0F;
		this.right_leg.yRot = 0.0F;
		this.right_leg.zRot = 0.0F;

		this.tail.xRot = 0.0F;
		this.tail.yRot = 0.0F;
		this.tail.zRot = 0.0F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
	                           float red, float green, float blue, float alpha) {
		this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.left_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.right_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}