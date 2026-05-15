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

        // Reset pose every frame.
        this.tentacle.y = HIDDEN_Y;
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

        if (!entity.isActivated()) {
            this.tentacle.y = HIDDEN_Y;
            return;
        }

        if (progress < 0.20F) {
            animateFastRise(progress);
        } else if (progress < 0.45F) {
            animateStrike(progress);
        } else if (progress < 0.70F) {
            animateAfterHit(ageInTicks);
        } else {
            animateSink(progress);
        }
    }

    private void animateFastRise(float progress) {
        float riseProgress = progress / 0.20F;
        riseProgress = Mth.clamp(riseProgress, 0.0F, 1.0F);
        riseProgress = easeOutBack(riseProgress);

        this.tentacle.y = Mth.lerp(riseProgress, HIDDEN_Y, SURFACE_Y);

        this.lower.zRot = Mth.lerp(riseProgress, -0.45F, 0.0F);
        this.middle.zRot = Mth.lerp(riseProgress, -0.25F, 0.18F);
        this.tip.zRot = Mth.lerp(riseProgress, -0.10F, 0.45F);
    }

    private void animateStrike(float progress) {
        float strikeProgress = (progress - 0.20F) / 0.25F;
        strikeProgress = Mth.clamp(strikeProgress, 0.0F, 1.0F);

        float snap = Mth.sin(strikeProgress * Mth.PI);

        this.tentacle.xRot = -snap * 0.35F;

        this.lower.zRot = snap * 0.35F;
        this.middle.zRot = 0.18F + snap * 0.85F;
        this.tip.zRot = 0.45F + snap * 1.25F;
        this.tip.xRot = -snap * 0.75F;

        this.tentacle.y = SURFACE_Y - snap * 3.0F;
    }

    private void animateAfterHit(float ageInTicks) {
        float sway = Mth.sin(ageInTicks * 0.55F) * 0.12F;
        float tipSway = Mth.sin(ageInTicks * 0.85F) * 0.10F;

        this.tentacle.y = SURFACE_Y;
        this.lower.zRot = sway;
        this.middle.zRot = 0.18F + sway * 1.5F;
        this.tip.zRot = 0.45F + sway + tipSway;
    }

    private void animateSink(float progress) {
        float sinkProgress = (progress - 0.70F) / 0.30F;
        sinkProgress = Mth.clamp(sinkProgress, 0.0F, 1.0F);
        sinkProgress = sinkProgress * sinkProgress;

        this.tentacle.y = Mth.lerp(sinkProgress, SURFACE_Y, HIDDEN_Y);

        this.lower.zRot = Mth.lerp(sinkProgress, 0.0F, 0.35F);
        this.middle.zRot = Mth.lerp(sinkProgress, 0.18F, 0.65F);
        this.tip.zRot = Mth.lerp(sinkProgress, 0.45F, 1.15F);
        this.tip.xRot = Mth.lerp(sinkProgress, 0.0F, -0.45F);
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