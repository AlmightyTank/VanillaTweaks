package com.amightytank.vanillatweaks.entity.client.pirate.model;

import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class KrakenTentacleModel<T extends KrakenTentacleEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart lower;
    private final ModelPart middle;
    private final ModelPart tip;
    private final ModelPart tip2;

    public KrakenTentacleModel(ModelPart root) {
        this.root = root;
        this.base = root.getChild("base");
        this.lower = this.base.getChild("lower");
        this.middle = this.lower.getChild("middle");
        this.tip = this.middle.getChild("tip");
        this.tip2 = this.tip.getChild("tip2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -17.0F, -8.0F, 16.0F, 17.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition lower = base.addOrReplaceChild("lower", CubeListBuilder.create().texOffs(0, 33).addBox(-6.0F, -22.0F, -6.0F, 12.0F, 22.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, 0.0F));

        PartDefinition middle = lower.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(48, 33).addBox(-4.0F, -18.75F, -4.0F, 8.0F, 19.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.25F, 0.0F));

        PartDefinition tip = middle.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(48, 60).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.75F, 0.0F));

        PartDefinition tip2 = tip.addOrReplaceChild("tip2", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        /*
         * No root turning.
         * No animation turning.
         * The tentacle uses its default Blockbench direction.
         */

        if (entity.isSmallChaseTentacle()) {
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
}