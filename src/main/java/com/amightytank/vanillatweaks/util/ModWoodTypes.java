package com.amightytank.vanillatweaks.util;

import com.amightytank.vanillatweaks.VanillaTweaks;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWoodTypes {
    public static final WoodType PINE = WoodType.register(new WoodType(VanillaTweaks.MOD_ID + ":pine", BlockSetType.OAK));
}
