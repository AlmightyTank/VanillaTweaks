package com.amightytank.vanillatweaks.item.custom;

import com.amightytank.vanillatweaks.entity.custom.pirate.PirateDynamiteArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PirateDynamiteArrowItem extends ArrowItem {
    public PirateDynamiteArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        PirateDynamiteArrowEntity arrow = new PirateDynamiteArrowEntity(level, shooter);
        arrow.setBaseDamage(3.0D);
        return arrow;
    }
}