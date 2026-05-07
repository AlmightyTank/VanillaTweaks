package com.amightytank.vanillatweaks.registry;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.MediumBoatEntity;
import com.amightytank.vanillatweaks.entity.SailBoatEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, VanillaTweaks.MODID);

    public static final Supplier<EntityType<MediumBoatEntity>> MEDIUM_BOAT =
            ENTITY_TYPES.register("medium_boat", () ->
                    EntityType.Builder.<MediumBoatEntity>of(MediumBoatEntity::new, MobCategory.MISC)
                            .sized(1.8F, 0.7F)
                            .clientTrackingRange(10)
                            .build("medium_boat")
            );

    public static final Supplier<EntityType<SailBoatEntity>> SAIL_BOAT =
            ENTITY_TYPES.register("sail_boat", () ->
                    EntityType.Builder.<SailBoatEntity>of(SailBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .build("sail_boat")
            );
}