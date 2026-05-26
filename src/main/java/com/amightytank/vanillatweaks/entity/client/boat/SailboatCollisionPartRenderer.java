package com.amightytank.vanillatweaks.entity.client.boat;

import com.amightytank.vanillatweaks.entity.custom.boat.SailboatCollisionPartEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class SailboatCollisionPartRenderer extends EntityRenderer<SailboatCollisionPartEntity> {
    public SailboatCollisionPartRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SailboatCollisionPartEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}