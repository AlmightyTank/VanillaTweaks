package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.pirate.model.PirateBruteAnimationModel;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateBruteEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class PirateBruteRenderer extends MobRenderer<PirateBruteEntity, PirateBruteAnimationModel<PirateBruteEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/pirate_brute.png");

    public PirateBruteRenderer(EntityRendererProvider.Context context) {
        super(context, new PirateBruteAnimationModel<>(context.bakeLayer(ModelLayers.PILLAGER)), 0.65F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(PirateBruteEntity entity) {
        return TEXTURE;
    }
}