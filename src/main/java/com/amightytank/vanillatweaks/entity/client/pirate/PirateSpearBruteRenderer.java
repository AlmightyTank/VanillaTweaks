package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateSpearBruteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PirateSpearBruteRenderer extends PirateBruteRenderer<PirateSpearBruteEntity> {
    public PirateSpearBruteRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/pirate_spear_brute.png"));
    }
}