package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class KrakenTentacleModel extends HierarchicalModel<KrakenTentacleEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(VanillaTweaks.MOD_ID, "kraken_tentacle"), "main");

    private final ModelPart root;
    private final ModelPart base;

    public KrakenTentacleModel(ModelPart root) {
        this.root = root;
        this.base = root.getChild("base");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -17.0F, -8.0F, 16.0F, 17.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition lower = base.addOrReplaceChild("lower", CubeListBuilder.create().texOffs(0, 33).addBox(-6.0F, -22.0F, -6.0F, 12.0F, 22.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, 0.0F));

        PartDefinition sucker_r1 = lower.addOrReplaceChild("sucker_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -0.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.0F, -2.0F, -0.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -12.0F, 5.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition sucker_r2 = lower.addOrReplaceChild("sucker_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.0F, -2.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 5.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition sucker_r3 = lower.addOrReplaceChild("sucker_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.0F, -2.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -17.0F, 5.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition middle = lower.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(48, 33).addBox(-4.0F, -18.75F, -4.0F, 8.0F, 19.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.25F, 0.0F));

        PartDefinition sucker_r4 = middle.addOrReplaceChild("sucker_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.75F, 3.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition sucker_r5 = middle.addOrReplaceChild("sucker_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.75F, 3.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition sucker_r6 = middle.addOrReplaceChild("sucker_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.75F, 3.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition tip = middle.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(48, 60).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.75F, 0.0F));

        PartDefinition sucker_r7 = tip.addOrReplaceChild("sucker_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition tip2 = tip.addOrReplaceChild("tip2", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition sucker_r8 = tip2.addOrReplaceChild("sucker_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, -1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(
            KrakenTentacleEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        if (entity.getAttackType() == KrakenTentacleEntity.TYPE_SMALL_CHASE) {
            this.animate(
                    entity.smallChaseAnimationState,
                    KrakenTentacleModelAnimation.animateSmallChase,
                    ageInTicks,
                    1.0F
            );
        } else {
            this.animate(
                    entity.bigStrikeAnimationState,
                    KrakenTentacleModelAnimation.animateBigStrike,
                    ageInTicks,
                    1.0F
            );
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        this.base.render(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );
    }
}