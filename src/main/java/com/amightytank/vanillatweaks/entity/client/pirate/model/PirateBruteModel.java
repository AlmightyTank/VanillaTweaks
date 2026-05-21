package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateBruteEntity;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class PirateBruteModel<T extends PirateBruteEntity> extends IllagerModel<T> {
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public PirateBruteModel(ModelPart root) {
        super(root);

        this.body = root.getChild("body");
        this.arms = root.getChild("arms");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
    }

    @Override
    public void setupAnim(T entity,
                          float limbSwing,
                          float limbSwingAmount,
                          float ageInTicks,
                          float netHeadYaw,
                          float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Always use normal weapon arms.
        // Vanilla crossed illager arms hide right_arm and left_arm,
        // so we force weapon arms visible for the brute.
        this.showWeaponArms();

        int state = entity.getAttackState();
        int tick = entity.getAttackTick();

        if (state == PirateBruteEntity.ATTACK_NONE) {
            if (entity.isAggressive()) {
                if (entity.isSpearBrute()) {
                    this.animateSpearChase(limbSwing, limbSwingAmount);
                } else {
                    this.animateAxeChase(limbSwing, limbSwingAmount);
                }
            } else {
                if (entity.isSpearBrute()) {
                    this.animateSpearNeutral(limbSwing, limbSwingAmount, ageInTicks);
                } else {
                    this.animateAxeNeutral(limbSwing, limbSwingAmount, ageInTicks);
                }
            }

            return;
        }

        if (state == PirateBruteEntity.SPEAR_WINDUP ||
                state == PirateBruteEntity.SPEAR_LUNGE ||
                state == PirateBruteEntity.SPEAR_RECOVER) {
            this.animateSpearAttack(state, tick);
        }

        if (state == PirateBruteEntity.AXE_WINDUP ||
                state == PirateBruteEntity.AXE_CHOP ||
                state == PirateBruteEntity.AXE_RECOVER) {
            this.animateAxeAttack(state, tick);
        }
    }

    private void showWeaponArms() {
        this.arms.visible = false;
        this.rightArm.visible = true;
        this.leftArm.visible = true;
    }

    private void animateSpearNeutral(float limbSwing, float limbSwingAmount, float ageInTicks) {
        float breathing = Mth.sin(ageInTicks * 0.08F) * 0.025F;

        // Free hand swings a little like vanilla walking.
        float freeArmSwing = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        freeArmSwing *= 0.45F;

        this.body.xRot = breathing;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        // Weapon arm: spear held ready and mostly steady.
        this.rightArm.xRot = -0.85F + breathing;
        this.rightArm.yRot = -0.18F;
        this.rightArm.zRot = 0.05F;

        // Free hand: small vanilla-style swing.
        this.leftArm.xRot = freeArmSwing + breathing;
        this.leftArm.yRot = 0.12F;
        this.leftArm.zRot = -0.05F;
    }

    private void animateAxeNeutral(float limbSwing, float limbSwingAmount, float ageInTicks) {
        float breathing = Mth.sin(ageInTicks * 0.08F) * 0.025F;

        // Free hand swings a little like vanilla walking.
        float freeArmSwing = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        freeArmSwing *= 0.45F;

        this.body.xRot = breathing;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        // Weapon arm: axe held low and ready.
        this.rightArm.xRot = -0.55F + breathing;
        this.rightArm.yRot = -0.22F;
        this.rightArm.zRot = 0.08F;

        // Free hand: small vanilla-style swing.
        this.leftArm.xRot = freeArmSwing + breathing;
        this.leftArm.yRot = 0.12F;
        this.leftArm.zRot = -0.05F;
    }

    private void animateSpearChase(float limbSwing, float limbSwingAmount) {
        float walk = Mth.cos(limbSwing * 0.6662F) * 0.25F * limbSwingAmount;

        this.body.xRot = -0.05F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        // Spear forward while chasing.
        this.rightArm.xRot = -1.15F + walk;
        this.rightArm.yRot = -0.12F;
        this.rightArm.zRot = 0.0F;

        // Free hand moves opposite.
        this.leftArm.xRot = -0.35F - walk;
        this.leftArm.yRot = 0.25F;
        this.leftArm.zRot = 0.0F;
    }

    private void animateAxeChase(float limbSwing, float limbSwingAmount) {
        float walk = Mth.cos(limbSwing * 0.6662F) * 0.25F * limbSwingAmount;

        this.body.xRot = -0.04F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        // Axe ready while chasing.
        this.rightArm.xRot = -0.75F + walk;
        this.rightArm.yRot = -0.25F;
        this.rightArm.zRot = 0.1F;

        // Free hand moves opposite.
        this.leftArm.xRot = -0.25F - walk;
        this.leftArm.yRot = 0.25F;
        this.leftArm.zRot = 0.0F;
    }

    private void animateSpearAttack(int state, int tick) {
        this.body.xRot = -0.08F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        this.leftArm.xRot = -0.45F;
        this.leftArm.yRot = 0.35F;
        this.leftArm.zRot = 0.0F;

        if (state == PirateBruteEntity.SPEAR_WINDUP) {
            float p = Mth.clamp(tick / 10.0F, 0.0F, 1.0F);

            this.rightArm.xRot = Mth.lerp(p, -0.5F, -1.65F);
            this.rightArm.yRot = Mth.lerp(p, -0.15F, -0.95F);
            this.rightArm.zRot = Mth.lerp(p, 0.0F, 0.25F);

            this.body.yRot = Mth.lerp(p, 0.0F, 0.25F);
        }

        if (state == PirateBruteEntity.SPEAR_LUNGE) {
            this.body.xRot = -0.28F;
            this.body.yRot = -0.15F;
            this.body.zRot = 0.0F;

            this.rightArm.xRot = -1.45F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;

            this.leftArm.xRot = 0.35F;
            this.leftArm.yRot = 0.5F;
            this.leftArm.zRot = 0.0F;
        }

        if (state == PirateBruteEntity.SPEAR_RECOVER) {
            this.body.xRot = -0.05F;
            this.body.yRot = 0.0F;
            this.body.zRot = 0.0F;

            this.rightArm.xRot = -0.85F;
            this.rightArm.yRot = -0.25F;
            this.rightArm.zRot = 0.1F;

            this.leftArm.xRot = -0.25F;
            this.leftArm.yRot = 0.25F;
            this.leftArm.zRot = 0.0F;
        }
    }

    private void animateAxeAttack(int state, int tick) {
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        this.leftArm.xRot = -0.4F;
        this.leftArm.yRot = 0.35F;
        this.leftArm.zRot = 0.0F;

        if (state == PirateBruteEntity.AXE_WINDUP) {
            float p = Mth.clamp(tick / 14.0F, 0.0F, 1.0F);

            this.body.yRot = Mth.lerp(p, 0.0F, 0.35F);

            this.rightArm.xRot = Mth.lerp(p, -0.3F, -2.55F);
            this.rightArm.yRot = Mth.lerp(p, -0.1F, -0.45F);
            this.rightArm.zRot = Mth.lerp(p, 0.0F, 0.35F);
        }

        if (state == PirateBruteEntity.AXE_CHOP) {
            this.body.xRot = -0.22F;
            this.body.yRot = -0.25F;
            this.body.zRot = 0.0F;

            this.rightArm.xRot = -0.35F;
            this.rightArm.yRot = -0.15F;
            this.rightArm.zRot = 0.05F;

            this.leftArm.xRot = 0.15F;
            this.leftArm.yRot = 0.5F;
            this.leftArm.zRot = 0.0F;
        }

        if (state == PirateBruteEntity.AXE_RECOVER) {
            this.body.xRot = -0.05F;
            this.body.yRot = 0.0F;
            this.body.zRot = 0.0F;

            this.rightArm.xRot = -0.85F;
            this.rightArm.yRot = -0.25F;
            this.rightArm.zRot = 0.1F;

            this.leftArm.xRot = -0.25F;
            this.leftArm.yRot = 0.25F;
            this.leftArm.zRot = 0.0F;
        }
    }
}