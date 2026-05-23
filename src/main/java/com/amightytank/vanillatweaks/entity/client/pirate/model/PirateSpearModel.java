package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateSpearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class PirateSpearModel extends EntityModel<PirateSpearEntity> {
    private final ModelPart body;
    private final ModelPart base;
    private final ModelPart middle;
    private final ModelPart middle_spike;

    public PirateSpearModel(ModelPart root) {
        this.body = root.getChild("body");
        this.base = root.getChild("base");
        this.middle = root.getChild("middle");
        this.middle_spike = root.getChild("middle_spike");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 23).addBox(-0.5F, 18.0F, -1.0F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(6, 14).addBox(-2.5F, -2.5F, 0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 14).addBox(-2.5F, -2.5F, -1.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -1.0F, 0.0F));

        PartDefinition middle = partdefinition.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(5, 18).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -1.0F));

        PartDefinition middle_spike = partdefinition.addOrReplaceChild("middle_spike", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(PirateSpearEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        middle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        middle_spike.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}