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
        // Let vanilla IllagerModel handle:
        // - head movement
        // - walking legs
        // - NEUTRAL arm pose
        // - normal idle/walk arm swing
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        int state = entity.getAttackState();
        int tick = entity.getAttackTick();

        // No custom attack state.
        // If neutral, vanilla NEUTRAL pose handles it.
        // If aggressive but not attacking yet, use a custom chase pose.
        if (state == PirateBruteEntity.ATTACK_NONE) {
            if (entity.isAggressive()) {
                this.showWeaponArms();

                if (entity.isSpearBrute()) {
                    this.animateTridentChase(limbSwing, limbSwingAmount);
                } else {
                    this.animateAxeChase(limbSwing, limbSwingAmount);
                }
            }

            return;
        }

        this.showWeaponArms();

        if (state == PirateBruteEntity.SPEAR_WINDUP ||
                state == PirateBruteEntity.SPEAR_LUNGE ||
                state == PirateBruteEntity.SPEAR_RECOVER) {
            this.animateTridentLunge(state, tick);
            return;
        }

        if (state == PirateBruteEntity.AXE_WINDUP ||
                state == PirateBruteEntity.AXE_CHOP ||
                state == PirateBruteEntity.AXE_RECOVER) {
            this.animateAxeLunge(state, tick);
            return;
        }

        if (state == PirateBruteEntity.THROW_WINDUP ||
                state == PirateBruteEntity.THROW_RELEASE ||
                state == PirateBruteEntity.THROW_RECOVER) {
            this.animateThrowAttack(entity, state, tick);
        }
    }

    private void showWeaponArms() {
        this.arms.visible = false;
        this.rightArm.visible = true;
        this.leftArm.visible = true;
    }

    private void animateTridentChase(float limbSwing, float limbSwingAmount) {
        float walk = Mth.cos(limbSwing * 0.6662F) * 0.25F * limbSwingAmount;

        this.body.xRot = -0.05F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        this.rightArm.xRot = -1.15F + walk;
        this.rightArm.yRot = -0.12F;
        this.rightArm.zRot = 0.0F;

        this.leftArm.xRot = -0.35F - walk;
        this.leftArm.yRot = 0.25F;
        this.leftArm.zRot = 0.0F;
    }

    private void animateAxeChase(float limbSwing, float limbSwingAmount) {
        float walk = Mth.cos(limbSwing * 0.6662F) * 0.25F * limbSwingAmount;

        this.body.xRot = -0.04F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        this.rightArm.xRot = -0.75F + walk;
        this.rightArm.yRot = -0.25F;
        this.rightArm.zRot = 0.1F;

        this.leftArm.xRot = -0.25F - walk;
        this.leftArm.yRot = 0.25F;
        this.leftArm.zRot = 0.0F;
    }

    private void animateTridentLunge(int state, int tick) {
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

    private void animateAxeLunge(int state, int tick) {
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

    private void animateThrowAttack(T entity, int state, int tick) {
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        this.leftArm.xRot = -0.25F;
        this.leftArm.yRot = 0.25F;
        this.leftArm.zRot = 0.0F;

        boolean axe = entity.isAxeBrute();

        if (state == PirateBruteEntity.THROW_WINDUP) {
            float maxTick = axe ? 16.0F : 14.0F;
            float p = Mth.clamp(tick / maxTick, 0.0F, 1.0F);

            this.body.yRot = Mth.lerp(p, 0.0F, 0.35F);

            if (axe) {
                this.rightArm.xRot = Mth.lerp(p, -0.55F, -2.45F);
                this.rightArm.yRot = Mth.lerp(p, -0.15F, -0.55F);
                this.rightArm.zRot = Mth.lerp(p, 0.05F, 0.45F);
            } else {
                this.rightArm.xRot = Mth.lerp(p, -0.85F, -2.15F);
                this.rightArm.yRot = Mth.lerp(p, -0.18F, -0.45F);
                this.rightArm.zRot = Mth.lerp(p, 0.05F, 0.25F);
            }
        }

        if (state == PirateBruteEntity.THROW_RELEASE) {
            this.body.xRot = -0.18F;
            this.body.yRot = -0.25F;
            this.body.zRot = 0.0F;

            this.rightArm.xRot = -1.05F;
            this.rightArm.yRot = 0.05F;
            this.rightArm.zRot = 0.0F;

            this.leftArm.xRot = 0.15F;
            this.leftArm.yRot = 0.45F;
            this.leftArm.zRot = 0.0F;
        }

        if (state == PirateBruteEntity.THROW_RECOVER) {
            this.body.xRot = -0.05F;
            this.body.yRot = 0.0F;
            this.body.zRot = 0.0F;

            this.rightArm.xRot = -0.65F;
            this.rightArm.yRot = -0.2F;
            this.rightArm.zRot = 0.05F;

            this.leftArm.xRot = -0.15F;
            this.leftArm.yRot = 0.25F;
            this.leftArm.zRot = 0.0F;
        }
    }
}