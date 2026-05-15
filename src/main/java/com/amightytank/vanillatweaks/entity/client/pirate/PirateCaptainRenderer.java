package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PirateCaptainRenderer extends MobRenderer<PirateCaptainEntity, IllagerModel<PirateCaptainEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/pirate_captain.png");

    public PirateCaptainRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerModel<>(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(PirateCaptainEntity entity) {
        return TEXTURE;
    }
}