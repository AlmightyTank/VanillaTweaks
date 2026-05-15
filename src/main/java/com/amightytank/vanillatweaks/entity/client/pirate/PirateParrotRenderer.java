package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot;

public class PirateParrotRenderer extends ParrotRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/pirate_parrot.png");

    public PirateParrotRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Parrot parrot) {
        return TEXTURE;
    }
}