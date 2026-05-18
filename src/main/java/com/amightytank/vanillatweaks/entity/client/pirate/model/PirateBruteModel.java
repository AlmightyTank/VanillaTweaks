package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateBruteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class PirateBruteModel<T extends AbstractPirateBruteEntity> extends HierarchicalModel<T> implements ArmedModel, HeadedModel {
    private final ModelPart root;

    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart nose;
    private final ModelPart body;

    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public PirateBruteModel(ModelPart root) {
        this.root = root;

        this.head = root.getChild("head");
        this.hat = this.head.getChild("hat");
        this.nose = this.head.getChild("nose");
        this.body = root.getChild("body");

        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("hat",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)),
                PartPose.ZERO);

        head.addOrReplaceChild("nose",
                CubeListBuilder.create()
                        .texOffs(24, 0)
                        .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 20)
                        .addBox(-4.5F, 0.0F, -3.0F, 9.0F, 13.0F, 6.0F)
                        .texOffs(0, 38)
                        .addBox(-4.5F, 0.0F, -3.0F, 9.0F, 13.0F, 6.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(40, 46)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F),
                PartPose.offset(-5.5F, 2.0F, 0.0F));

        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(40, 46)
                        .mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F),
                PartPose.offset(5.5F, 2.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetParts();

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.2F * limbSwingAmount;

        int state = entity.getAttackState();
        int tick = entity.getAttackTick();

        if (state == AbstractPirateBruteEntity.ATTACK_NONE) {
            this.animateCrossedArms(ageInTicks);
        } else if (state == AbstractPirateBruteEntity.SPEAR_WINDUP ||
                state == AbstractPirateBruteEntity.SPEAR_LUNGE ||
                state == AbstractPirateBruteEntity.SPEAR_RECOVER) {
            this.animateSpearAttack(state, tick);
        } else if (state == AbstractPirateBruteEntity.AXE_WINDUP ||
                state == AbstractPirateBruteEntity.AXE_CHOP ||
                state == AbstractPirateBruteEntity.AXE_RECOVER) {
            this.animateAxeAttack(state, tick);
        }
    }

    private void resetParts() {
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        this.rightArm.xRot = 0.0F;
        this.rightArm.yRot = 0.0F;
        this.rightArm.zRot = 0.0F;

        this.leftArm.xRot = 0.0F;
        this.leftArm.yRot = 0.0F;
        this.leftArm.zRot = 0.0F;

        this.rightLeg.xRot = 0.0F;
        this.leftLeg.xRot = 0.0F;
    }

    private void animateCrossedArms(float ageInTicks) {
        float breathing = Mth.sin(ageInTicks * 0.08F) * 0.025F;

        this.body.xRot = breathing;

        this.rightArm.xRot = -0.75F;
        this.rightArm.yRot = -0.65F;
        this.rightArm.zRot = 0.25F;

        this.leftArm.xRot = -0.75F;
        this.leftArm.yRot = 0.65F;
        this.leftArm.zRot = -0.25F;
    }

    private void animateSpearAttack(int state, int tick) {
        this.body.xRot = -0.08F;

        this.leftArm.xRot = -0.45F;
        this.leftArm.yRot = 0.35F;

        if (state == AbstractPirateBruteEntity.SPEAR_WINDUP) {
            float p = Mth.clamp(tick / 10.0F, 0.0F, 1.0F);

            this.rightArm.xRot = Mth.lerp(p, -0.4F, -1.65F);
            this.rightArm.yRot = Mth.lerp(p, -0.2F, -0.95F);
            this.rightArm.zRot = Mth.lerp(p, 0.0F, 0.25F);

            this.body.yRot = Mth.lerp(p, 0.0F, 0.25F);
        }

        if (state == AbstractPirateBruteEntity.SPEAR_LUNGE) {
            this.body.xRot = -0.28F;
            this.body.yRot = -0.15F;

            this.rightArm.xRot = -1.45F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;

            this.leftArm.xRot = 0.35F;
            this.leftArm.yRot = 0.5F;
        }

        if (state == AbstractPirateBruteEntity.SPEAR_RECOVER) {
            this.rightArm.xRot = -0.85F;
            this.rightArm.yRot = -0.25F;
            this.rightArm.zRot = 0.1F;

            this.leftArm.xRot = -0.25F;
        }
    }

    private void animateAxeAttack(int state, int tick) {
        this.leftArm.xRot = -0.4F;
        this.leftArm.yRot = 0.35F;

        if (state == AbstractPirateBruteEntity.AXE_WINDUP) {
            float p = Mth.clamp(tick / 14.0F, 0.0F, 1.0F);

            this.body.yRot = Mth.lerp(p, 0.0F, 0.35F);

            this.rightArm.xRot = Mth.lerp(p, -0.3F, -2.55F);
            this.rightArm.yRot = Mth.lerp(p, -0.1F, -0.45F);
            this.rightArm.zRot = Mth.lerp(p, 0.0F, 0.35F);
        }

        if (state == AbstractPirateBruteEntity.AXE_CHOP) {
            this.body.xRot = -0.22F;
            this.body.yRot = -0.25F;

            this.rightArm.xRot = -0.35F;
            this.rightArm.yRot = -0.15F;
            this.rightArm.zRot = 0.05F;

            this.leftArm.xRot = 0.15F;
            this.leftArm.yRot = 0.5F;
        }

        if (state == AbstractPirateBruteEntity.AXE_RECOVER) {
            this.body.xRot = -0.05F;

            this.rightArm.xRot = -0.85F;
            this.rightArm.yRot = -0.25F;
            this.rightArm.zRot = 0.1F;
        }
    }

    private ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        this.getArm(arm).translateAndRotate(poseStack);
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}