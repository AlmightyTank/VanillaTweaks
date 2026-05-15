package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class KrakenTentacleRenderer extends EntityRenderer<KrakenTentacleEntity> {

    public KrakenTentacleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.6F;
    }

    @Override
    public ResourceLocation getTextureLocation(KrakenTentacleEntity entity) {
        return null;
    }
}