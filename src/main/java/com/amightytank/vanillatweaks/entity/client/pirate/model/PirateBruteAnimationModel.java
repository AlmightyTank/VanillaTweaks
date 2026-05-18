package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateBruteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class PirateBruteAnimationModel<T extends PirateBruteEntity> extends HierarchicalModel<T> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public PirateBruteAnimationModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.arms = root.getChild("arms");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        this.arms.visible = false;
        this.rightArm.visible = true;
        this.leftArm.visible = true;

        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        this.rightArm.yRot = 0.0F;
        this.leftArm.yRot = 0.0F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;

        int lungeTicks = entity.getLungeTicks();

        if (lungeTicks > 0) {
            if (entity.isHoldingSpear()) {
                this.animateSpearLunge(lungeTicks);
            } else {
                this.animateAxeLunge(lungeTicks);
            }
        } else if (!entity.isAggressive()) {
            this.arms.visible = true;
            this.rightArm.visible = false;
            this.leftArm.visible = false;
        }
    }

    private void animateSpearLunge(int lungeTicks) {
        float windup = Mth.clamp(lungeTicks / 10.0F, 0.0F, 1.0F);
        float thrust = Mth.clamp((lungeTicks - 10.0F) / 6.0F, 0.0F, 1.0F);
        float recover = Mth.clamp((lungeTicks - 16.0F) / 8.0F, 0.0F, 1.0F);

        this.rightArm.xRot = Mth.lerp(windup, -0.7F, -2.45F);
        this.rightArm.yRot = Mth.lerp(windup, -0.15F, -0.55F);
        this.rightArm.zRot = Mth.lerp(windup, 0.0F, 0.18F);

        this.leftArm.xRot = Mth.lerp(windup, -0.5F, -1.35F);
        this.leftArm.yRot = Mth.lerp(windup, 0.15F, 0.45F);
        this.leftArm.zRot = Mth.lerp(windup, 0.0F, -0.12F);

        if (thrust > 0.0F) {
            this.rightArm.xRot = Mth.lerp(thrust, -2.45F, -1.15F);
            this.rightArm.yRot = Mth.lerp(thrust, -0.55F, -0.05F);
            this.rightArm.zRot = Mth.lerp(thrust, 0.18F, 0.0F);

            this.leftArm.xRot = Mth.lerp(thrust, -1.35F, -1.05F);
            this.leftArm.yRot = Mth.lerp(thrust, 0.45F, 0.08F);
            this.leftArm.zRot = Mth.lerp(thrust, -0.12F, 0.0F);
        }

        if (recover > 0.0F) {
            this.rightArm.xRot = Mth.lerp(recover, this.rightArm.xRot, -0.85F);
            this.rightArm.yRot = Mth.lerp(recover, this.rightArm.yRot, -0.15F);
            this.rightArm.zRot = Mth.lerp(recover, this.rightArm.zRot, 0.0F);

            this.leftArm.xRot = Mth.lerp(recover, this.leftArm.xRot, -0.65F);
            this.leftArm.yRot = Mth.lerp(recover, this.leftArm.yRot, 0.15F);
            this.leftArm.zRot = Mth.lerp(recover, this.leftArm.zRot, 0.0F);
        }
    }

    private void animateAxeLunge(int lungeTicks) {
        float windup = Mth.clamp(lungeTicks / 10.0F, 0.0F, 1.0F);
        float chop = Mth.clamp((lungeTicks - 10.0F) / 6.0F, 0.0F, 1.0F);
        float recover = Mth.clamp((lungeTicks - 16.0F) / 8.0F, 0.0F, 1.0F);

        this.rightArm.xRot = Mth.lerp(windup, -0.7F, -2.85F);
        this.rightArm.yRot = Mth.lerp(windup, -0.15F, -0.35F);
        this.rightArm.zRot = Mth.lerp(windup, 0.0F, 0.12F);

        this.leftArm.xRot = Mth.lerp(windup, -0.4F, -1.35F);
        this.leftArm.yRot = Mth.lerp(windup, 0.15F, 0.35F);
        this.leftArm.zRot = 0.0F;

        if (chop > 0.0F) {
            this.rightArm.xRot = Mth.lerp(chop, -2.85F, -0.55F);
            this.rightArm.yRot = Mth.lerp(chop, -0.35F, -0.1F);
            this.rightArm.zRot = Mth.lerp(chop, 0.12F, 0.0F);

            this.leftArm.xRot = Mth.lerp(chop, -1.35F, -0.75F);
            this.leftArm.yRot = Mth.lerp(chop, 0.35F, 0.1F);
        }

        if (recover > 0.0F) {
            this.rightArm.xRot = Mth.lerp(recover, this.rightArm.xRot, -0.8F);
            this.rightArm.yRot = Mth.lerp(recover, this.rightArm.yRot, -0.15F);
            this.rightArm.zRot = Mth.lerp(recover, this.rightArm.zRot, 0.0F);

            this.leftArm.xRot = Mth.lerp(recover, this.leftArm.xRot, -0.6F);
            this.leftArm.yRot = Mth.lerp(recover, this.leftArm.yRot, 0.15F);
            this.leftArm.zRot = 0.0F;
        }
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        ModelPart modelpart = arm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;

        this.root.translateAndRotate(poseStack);
        modelpart.translateAndRotate(poseStack);

        if (arm == HumanoidArm.RIGHT) {
            poseStack.translate(-0.0625F, 0.625F, 0.0F);
        } else {
            poseStack.translate(0.0625F, 0.625F, 0.0F);
        }
    }
}