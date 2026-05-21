package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.client.ModModelLayers;
import com.amightytank.vanillatweaks.entity.client.pirate.model.PirateCaptainModel;
import com.amightytank.vanillatweaks.entity.client.pirate.model.ShoulderPirateParrotModel;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PirateCaptainRenderer extends MobRenderer<PirateCaptainEntity, PirateCaptainModel<PirateCaptainEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/pirate_captain.png");

    public PirateCaptainRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new PirateCaptainModel<>(context.bakeLayer(ModModelLayers.PIRATE_CAPTAIN_LAYER)),
                0.5F
        );

        this.addLayer(new ShoulderPirateParrotLayer(
                this,
                new ShoulderPirateParrotModel<>(context.bakeLayer(ModModelLayers.SHOULDER_PIRATE_PARROT_LAYER))
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(PirateCaptainEntity entity) {
        return TEXTURE;
    }
}