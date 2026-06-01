package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.AbstractIllager;

public class PirateCaptainModel<T extends PirateCaptainEntity> extends EntityModel<T> {
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart nose;
	private final ModelPart body;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart left_leg;
	private final ModelPart right_leg;

	public PirateCaptainModel(ModelPart root) {
		this.head = root.getChild("head");
		this.hat = root.getChild("hat");
		this.nose = root.getChild("nose");
		this.body = root.getChild("body");
		this.left_arm = root.getChild("left_arm");
		this.right_arm = root.getChild("right_arm");
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("head",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("hat",
				CubeListBuilder.create()
						.texOffs(64, 0)
						.addBox(-6.0F, -21.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
						.texOffs(102, 24)
						.addBox(-7.0F, -21.0F, -6.0F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(76, 33)
						.addBox(-4.0F, -13.0F, -5.0F, 8.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("nose",
				CubeListBuilder.create()
						.texOffs(24, 0)
						.addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -2.0F, 0.0F));

		partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create()
						.texOffs(16, 20)
						.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
						.texOffs(0, 38)
						.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_arm",
				CubeListBuilder.create()
						.texOffs(40, 46)
						.mirror()
						.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offset(5.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_arm",
				CubeListBuilder.create()
						.texOffs(40, 46)
						.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-5.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_leg",
				CubeListBuilder.create()
						.texOffs(0, 22)
						.mirror()
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offset(2.0F, 12.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg",
				CubeListBuilder.create()
						.texOffs(0, 22)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 112, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	private void resetPose() {
		this.head.xRot = 0.0F;
		this.head.yRot = 0.0F;
		this.head.zRot = 0.0F;

		this.hat.xRot = 0.0F;
		this.hat.yRot = 0.0F;
		this.hat.zRot = 0.0F;

		this.nose.xRot = 0.0F;
		this.nose.yRot = 0.0F;
		this.nose.zRot = 0.0F;

		this.body.xRot = 0.0F;
		this.body.yRot = 0.0F;
		this.body.zRot = 0.0F;

		this.left_arm.xRot = 0.0F;
		this.left_arm.yRot = 0.0F;
		this.left_arm.zRot = 0.0F;

		this.right_arm.xRot = 0.0F;
		this.right_arm.yRot = 0.0F;
		this.right_arm.zRot = 0.0F;

		this.left_leg.xRot = 0.0F;
		this.left_leg.yRot = 0.0F;
		this.left_leg.zRot = 0.0F;

		this.right_leg.xRot = 0.0F;
		this.right_leg.yRot = 0.0F;
		this.right_leg.zRot = 0.0F;
	}

	private void animateSpellCasting(float ageInTicks) {
		float wave = Mth.sin(ageInTicks * 0.35F) * 0.15F;

		this.right_arm.xRot = -1.9F + wave;
		this.right_arm.yRot = -0.45F;
		this.right_arm.zRot = 0.35F;

		this.left_arm.xRot = -1.9F - wave;
		this.left_arm.yRot = 0.45F;
		this.left_arm.zRot = -0.35F;
	}

	private void animateAttacking(float ageInTicks) {
		float swing = Mth.sin(ageInTicks * 0.35F) * 0.2F;

		this.right_arm.xRot = -1.2F + swing;
		this.left_arm.xRot = -0.6F - swing;
	}

	private void animateIdle(float ageInTicks) {
		float sway = Mth.sin(ageInTicks * 0.08F) * 0.03F;

		this.body.zRot = sway;
		this.head.zRot = -sway;
		this.hat.zRot = this.head.zRot;
		this.nose.zRot = this.head.zRot;

		this.right_arm.xRot = 0.05F;
		this.left_arm.xRot = 0.05F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
	                           float red, float green, float blue, float alpha) {
		this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.nose.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}