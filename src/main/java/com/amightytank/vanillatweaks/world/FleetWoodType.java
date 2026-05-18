package com.amightytank.vanillatweaks.world;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import net.minecraft.util.RandomSource;

public enum FleetWoodType {
    OAK(ModBoatEntity.WoodKind.OAK),
    SPRUCE(ModBoatEntity.WoodKind.SPRUCE),
    BIRCH(ModBoatEntity.WoodKind.BIRCH),
    JUNGLE(ModBoatEntity.WoodKind.JUNGLE),
    ACACIA(ModBoatEntity.WoodKind.ACACIA),
    DARK_OAK(ModBoatEntity.WoodKind.DARK_OAK),
    MANGROVE(ModBoatEntity.WoodKind.MANGROVE),
    CHERRY(ModBoatEntity.WoodKind.CHERRY),
    BAMBOO(ModBoatEntity.WoodKind.BAMBOO);

    private final ModBoatEntity.WoodKind woodKind;

    FleetWoodType(ModBoatEntity.WoodKind woodKind) {
        this.woodKind = woodKind;
    }

    public ModBoatEntity.WoodKind getWoodKind() {
        return this.woodKind;
    }

    public static FleetWoodType getRandom(RandomSource random) {
        FleetWoodType[] values = values();
        return values[random.nextInt(values.length)];
    }
}