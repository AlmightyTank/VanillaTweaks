package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class KrakenTentacleModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(VanillaTweaks.MOD_ID, "kraken_tentacle"), "main");

    private final ModelPart base;
    private final ModelPart lower;
    private final ModelPart middle;
    private final ModelPart tip;
    private final ModelPart tip2;

    public KrakenTentacleModel(ModelPart root) {
        this.base = root.getChild("base");
        this.lower = this.base.getChild("lower");
        this.middle = this.lower.getChild("middle");
        this.tip = this.middle.getChild("tip");
        this.tip2 = this.tip.getChild("tip2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        /*
         * IMPORTANT:
         * Each cube is local to its own pivot.
         * The pivot is at the bottom of that segment.
         */

        PartDefinition base = root.addOrReplaceChild("base",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-8.0F, -17.0F, -8.0F, 16.0F, 17.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        PartDefinition lower = base.addOrReplaceChild("lower",
                CubeListBuilder.create()
                        .texOffs(0, 34)
                        .addBox(-6.0F, -22.0F, -6.0F, 12.0F, 22.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -17.0F, 0.0F)
        );

        PartDefinition middle = lower.addOrReplaceChild("middle",
                CubeListBuilder.create()
                        .texOffs(48, 34)
                        .addBox(-4.0F, -19.0F, -4.0F, 8.0F, 19.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -22.0F, 0.0F)
        );

        PartDefinition tip = middle.addOrReplaceChild("tip",
                CubeListBuilder.create()
                        .texOffs(80, 34)
                        .addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -19.0F, 0.0F)
        );

        PartDefinition tip2 = tip.addOrReplaceChild("tip2",
                CubeListBuilder.create()
                        .texOffs(104, 34)
                        .addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -8.0F, 0.0F)
        );

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        resetPose();

        float idle = Mth.sin(ageInTicks * 0.08F);
        float idle2 = Mth.sin(ageInTicks * 0.08F + 1.2F);

        this.lower.zRot = idle * 0.04F;
        this.middle.zRot = idle2 * 0.08F;
        this.tip.zRot = idle * 0.12F;
        this.tip2.zRot = idle2 * 0.16F;

        this.lower.xRot = Mth.sin(ageInTicks * 0.06F) * 0.03F;
        this.middle.xRot = Mth.sin(ageInTicks * 0.06F + 0.8F) * 0.06F;
        this.tip.xRot = Mth.sin(ageInTicks * 0.06F + 1.4F) * 0.10F;
        this.tip2.xRot = Mth.sin(ageInTicks * 0.06F + 2.0F) * 0.14F;

        if (entity instanceof KrakenTentacleEntity tentacle) {
            float progress = tentacle.getLifeProgress();

            if (progress > 0.0F) {
                if (tentacle.isBigStrikeTentacle()) {
                    animateBigStrike(progress);
                } else {
                    animateSmallChase(progress, ageInTicks);
                }
            }
        }
    }

    private void animateBigStrike(float progress) {
        float rise = Mth.clamp(progress / 0.20F, 0.0F, 1.0F);
        float windup = Mth.sin(Mth.clamp((progress - 0.12F) / 0.25F, 0.0F, 1.0F) * Mth.PI);
        float slam = Mth.sin(Mth.clamp((progress - 0.32F) / 0.22F, 0.0F, 1.0F) * Mth.PI);
        float sink = Mth.clamp((progress - 0.75F) / 0.25F, 0.0F, 1.0F);

        // Rise out of water, then sink back down.
        this.base.y += 26.0F * (1.0F - rise);
        this.base.y += 24.0F * sink;

        /*
         * Windup: pulls backward.
         * Since the model is now properly centered, xRot is the main attack axis.
         */
        this.lower.xRot += -0.18F * windup;
        this.middle.xRot += -0.40F * windup;
        this.tip.xRot += -0.65F * windup;
        this.tip2.xRot += -0.85F * windup;

        // Slam forward/down.
        this.lower.xRot += 0.35F * slam;
        this.middle.xRot += 0.85F * slam;
        this.tip.xRot += 1.30F * slam;
        this.tip2.xRot += 1.65F * slam;

        // Whip sideways during impact.
        this.middle.zRot += 0.18F * slam;
        this.tip.zRot += 0.32F * slam;
        this.tip2.zRot += 0.46F * slam;

        // Small twist so it does not look robotic.
        this.middle.yRot += 0.08F * slam;
        this.tip.yRot += 0.15F * slam;
        this.tip2.yRot += 0.22F * slam;
    }

    private void animateSmallChase(float progress, float ageInTicks) {
        float rise = Mth.clamp(progress / 0.18F, 0.0F, 1.0F);
        float sink = Mth.clamp((progress - 0.78F) / 0.22F, 0.0F, 1.0F);

        this.base.y += 18.0F * (1.0F - rise);
        this.base.y += 18.0F * sink;

        float wiggle = Mth.sin(ageInTicks * 0.40F);
        float wiggle2 = Mth.sin(ageInTicks * 0.40F + 1.2F);
        float wiggle3 = Mth.sin(ageInTicks * 0.40F + 2.1F);

        this.lower.zRot += wiggle * 0.10F;
        this.middle.zRot += wiggle2 * 0.18F;
        this.tip.zRot += wiggle3 * 0.28F;
        this.tip2.zRot += wiggle * 0.36F;

        this.middle.xRot += wiggle3 * 0.10F;
        this.tip.xRot += wiggle * 0.16F;
        this.tip2.xRot += wiggle2 * 0.22F;

        this.middle.yRot += wiggle * 0.07F;
        this.tip.yRot += wiggle2 * 0.12F;
        this.tip2.yRot += wiggle3 * 0.18F;
    }

    private void resetPose() {
        this.base.xRot = 0.0F;
        this.base.yRot = 0.0F;
        this.base.zRot = 0.0F;
        this.base.x = 0.0F;
        this.base.y = 24.0F;
        this.base.z = 0.0F;

        this.lower.xRot = 0.0F;
        this.lower.yRot = 0.0F;
        this.lower.zRot = 0.0F;
        this.lower.x = 0.0F;
        this.lower.y = -17.0F;
        this.lower.z = 0.0F;

        this.middle.xRot = 0.0F;
        this.middle.yRot = 0.0F;
        this.middle.zRot = 0.0F;
        this.middle.x = 0.0F;
        this.middle.y = -22.0F;
        this.middle.z = 0.0F;

        this.tip.xRot = 0.0F;
        this.tip.yRot = 0.0F;
        this.tip.zRot = 0.0F;
        this.tip.x = 0.0F;
        this.tip.y = -19.0F;
        this.tip.z = 0.0F;

        this.tip2.xRot = 0.0F;
        this.tip2.yRot = 0.0F;
        this.tip2.zRot = 0.0F;
        this.tip2.x = 0.0F;
        this.tip2.y = -8.0F;
        this.tip2.z = 0.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}