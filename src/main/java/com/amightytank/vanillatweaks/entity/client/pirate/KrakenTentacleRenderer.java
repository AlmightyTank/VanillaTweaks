package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.pirate.model.KrakenTentacleModel;
import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class KrakenTentacleRenderer extends MobRenderer<KrakenTentacleEntity, KrakenTentacleModel<KrakenTentacleEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/kraken_tentacle.png");

    public KrakenTentacleRenderer(EntityRendererProvider.Context context) {
        super(context, new KrakenTentacleModel<>(context.bakeLayer(ModModelLayers.KRAKEN_TENTACLE_LAYER)), 0.0F);
    }

    @Override
    protected void setupRotations(KrakenTentacleEntity entity, PoseStack poseStack,
                                  float ageInTicks, float rotationYaw, float partialTicks) {
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
    }

    @Override
    public ResourceLocation getTextureLocation(KrakenTentacleEntity entity) {
        return TEXTURE;
    }
}