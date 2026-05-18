package com.amightytank.vanillatweaks.entity.client.pirate;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateAxeBruteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PirateAxeBruteRenderer extends PirateBruteRenderer<PirateAxeBruteEntity> {
    public PirateAxeBruteRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceLocation(VanillaTweaks.MOD_ID, "textures/entity/pirate/pirate_axe_brute.png"));
    }
}