package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.pirate.model.PirateBruteModel;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateBruteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class PirateBruteRenderer<T extends AbstractPirateBruteEntity> extends MobRenderer<T, PirateBruteModel<T>> {
    private final ResourceLocation texture;

    public PirateBruteRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context, new PirateBruteModel<>(context.bakeLayer(ModModelLayers.PIRATE_BRUTE_LAYER)), 0.65F);
        this.texture = texture;

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.15F, 1.15F, 1.15F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture;
    }
}