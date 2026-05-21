package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.pirate.model.PirateBruteModel;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateBruteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class PirateBruteRenderer extends MobRenderer<PirateBruteEntity, PirateBruteModel<PirateBruteEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/pirate_brute.png");

    public PirateBruteRenderer(EntityRendererProvider.Context context) {
        super(context, new PirateBruteModel<>(context.bakeLayer(ModModelLayers.PIRATE_BRUTE_LAYER)), 0.65F);

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    protected void scale(PirateBruteEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.12F, 1.12F, 1.12F);
    }

    @Override
    public ResourceLocation getTextureLocation(PirateBruteEntity entity) {
        return TEXTURE;
    }
}