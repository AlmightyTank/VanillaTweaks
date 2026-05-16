package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class KrakenTentacleModel<T extends KrakenTentacleEntity> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart tentacle;
    private final ModelPart lower;
    private final ModelPart middle;
    private final ModelPart tip;

    private static final float SURFACE_Y = 24.0F;
    private static final float HIDDEN_Y = 56.0F;

    public KrakenTentacleModel(ModelPart root) {
        this.root = root;
        this.tentacle = root.getChild("tentacle");
        this.lower = this.tentacle.getChild("lower");
        this.middle = this.lower.getChild("middle");
        this.tip = this.middle.getChild("tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        PartDefinition tentacle = root.addOrReplaceChild("tentacle",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        tentacle.addOrReplaceChild("base",
                CubeListBuilder.create()
                        .texOffs(0, 40)
                        .addBox(-8.0F, -6.0F, -8.0F, 16.0F, 6.0F, 16.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition lower = tentacle.addOrReplaceChild("lower",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-6.0F, -18.0F, -6.0F, 12.0F, 18.0F, 12.0F),
                PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition middle = lower.addOrReplaceChild("middle",
                CubeListBuilder.create()
                        .texOffs(28, 0)
                        .addBox(-5.0F, -16.0F, -5.0F, 10.0F, 16.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, -17.0F, 0.0F, 0.0F, 0.0F, 0.18F));

        PartDefinition tip = middle.addOrReplaceChild("tip",
                CubeListBuilder.create()
                        .texOffs(36, 28)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F),
                PartPose.offsetAndRotation(1.5F, -15.0F, 0.0F, 0.0F, 0.0F, 0.45F));

        lower.addOrReplaceChild("sucker_1",
                CubeListBuilder.create()
                        .texOffs(0, 30)
                        .addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, -5.0F, -6.1F));

        lower.addOrReplaceChild("sucker_2",
                CubeListBuilder.create()
                        .texOffs(0, 30)
                        .addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, -12.0F, -6.1F));

        middle.addOrReplaceChild("sucker_3",
                CubeListBuilder.create()
                        .texOffs(0, 30)
                        .addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, -5.0F, -5.1F));

        middle.addOrReplaceChild("sucker_4",
                CubeListBuilder.create()
                        .texOffs(0, 30)
                        .addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, -11.0F, -5.1F));

        tip.addOrReplaceChild("sucker_5",
                CubeListBuilder.create()
                        .texOffs(0, 30)
                        .addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F),
                PartPose.offset(0.0F, -5.0F, -4.1F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float progress = entity.getLifeProgress();

        resetPose();

        if (!entity.isActivated()) {
            this.tentacle.y = 56.0F;
            return;
        }

        if (entity.isSmallChaseTentacle()) {
            animateSmallChase(progress, ageInTicks);
        } else {
            animateBigStrike(progress, ageInTicks);
        }
    }

    private void resetPose() {
        this.tentacle.y = 24.0F;
        this.tentacle.xRot = 0.0F;
        this.tentacle.yRot = 0.0F;
        this.tentacle.zRot = 0.0F;

        this.lower.xRot = 0.0F;
        this.lower.yRot = 0.0F;
        this.lower.zRot = 0.0F;

        this.middle.xRot = 0.0F;
        this.middle.yRot = 0.0F;
        this.middle.zRot = 0.18F;

        this.tip.xRot = 0.0F;
        this.tip.yRot = 0.0F;
        this.tip.zRot = 0.45F;
    }

    private void animateSmallChase(float progress, float ageInTicks) {
        /*
         * Small tentacles only pop slightly.
         * They should feel like warning ripples/chasing fingers.
         */
        if (progress < 0.22F) {
            float rise = progress / 0.22F;
            rise = Mth.clamp(rise, 0.0F, 1.0F);

            this.tentacle.y = Mth.lerp(rise, 48.0F, 32.0F);

            // Lean outward as it appears.
            this.tentacle.xRot = -0.25F * rise;
            this.lower.zRot = 0.15F * rise;
            this.middle.zRot = 0.18F + 0.35F * rise;
            this.tip.zRot = 0.45F + 0.45F * rise;
        } else if (progress < 0.55F) {
            float wiggle = Mth.sin(ageInTicks * 0.9F) * 0.08F;

            this.tentacle.y = 32.0F;
            this.tentacle.xRot = -0.25F;
            this.lower.zRot = 0.15F + wiggle;
            this.middle.zRot = 0.53F + wiggle;
            this.tip.zRot = 0.90F + wiggle;
        } else {
            float sink = (progress - 0.55F) / 0.45F;
            sink = Mth.clamp(sink, 0.0F, 1.0F);

            this.tentacle.y = Mth.lerp(sink, 32.0F, 52.0F);
            this.tentacle.xRot = Mth.lerp(sink, -0.25F, -0.05F);
            this.lower.zRot = Mth.lerp(sink, 0.15F, 0.0F);
            this.middle.zRot = Mth.lerp(sink, 0.53F, 0.18F);
            this.tip.zRot = Mth.lerp(sink, 0.90F, 0.45F);
        }
    }

    private void animateBigStrike(float progress, float ageInTicks) {
        /*
         * Big tentacles rise all the way up and whip outward.
         */
        if (progress < 0.20F) {
            float rise = progress / 0.20F;
            rise = Mth.clamp(rise, 0.0F, 1.0F);
            rise = easeOutBack(rise);

            this.tentacle.y = Mth.lerp(rise, 56.0F, 24.0F);

            // Start curled, then unfold outward.
            this.tentacle.xRot = Mth.lerp(rise, 0.25F, -0.25F);
            this.lower.zRot = Mth.lerp(rise, -0.35F, 0.10F);
            this.middle.zRot = Mth.lerp(rise, -0.20F, 0.35F);
            this.tip.zRot = Mth.lerp(rise, 0.0F, 0.75F);
        } else if (progress < 0.48F) {
            float strike = (progress - 0.20F) / 0.28F;
            strike = Mth.clamp(strike, 0.0F, 1.0F);

            float snap = Mth.sin(strike * Mth.PI);

            // Outward body lean.
            this.tentacle.xRot = -0.25F - snap * 0.45F;

            // Hard whip.
            this.lower.zRot = 0.10F + snap * 0.35F;
            this.middle.zRot = 0.35F + snap * 0.90F;
            this.tip.zRot = 0.75F + snap * 1.25F;
            this.tip.xRot = -snap * 0.85F;

            this.tentacle.y = 24.0F - snap * 4.0F;
        } else if (progress < 0.72F) {
            float wiggle = Mth.sin(ageInTicks * 0.7F) * 0.10F;

            this.tentacle.y = 24.0F;
            this.tentacle.xRot = -0.35F;
            this.lower.zRot = 0.15F + wiggle;
            this.middle.zRot = 0.45F + wiggle * 1.5F;
            this.tip.zRot = 0.85F + wiggle;
        } else {
            float sink = (progress - 0.72F) / 0.28F;
            sink = Mth.clamp(sink, 0.0F, 1.0F);
            sink = sink * sink;

            this.tentacle.y = Mth.lerp(sink, 24.0F, 56.0F);

            this.tentacle.xRot = Mth.lerp(sink, -0.35F, 0.0F);
            this.lower.zRot = Mth.lerp(sink, 0.15F, 0.0F);
            this.middle.zRot = Mth.lerp(sink, 0.45F, 0.18F);
            this.tip.zRot = Mth.lerp(sink, 0.85F, 0.45F);
            this.tip.xRot = Mth.lerp(sink, 0.0F, -0.25F);
        }
    }

    private float easeOutBack(float x) {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;

        return 1.0F + c3 * (float) Math.pow(x - 1.0F, 3.0D)
                + c1 * (float) Math.pow(x - 1.0F, 2.0D);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}