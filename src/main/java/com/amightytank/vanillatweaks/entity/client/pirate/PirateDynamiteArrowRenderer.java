package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateDynamiteArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PirateDynamiteArrowRenderer extends ArrowRenderer<PirateDynamiteArrowEntity> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/projectiles/pirate_dynamite_arrow.png");

    public PirateDynamiteArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(PirateDynamiteArrowEntity entity) {
        return TEXTURE;
    }
}