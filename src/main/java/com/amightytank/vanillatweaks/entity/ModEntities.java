package com.amightytank.vanillatweaks.entity;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.boat.SailboatCollisionPartEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VanillaTweaks.MOD_ID);

    // Normal / small sailboat
    public static final RegistryObject<EntityType<ModBoatEntity>> MOD_BOAT =
            ENTITY_TYPES.register("mod_boat", () ->
                    EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.35F)
                            .clientTrackingRange(10)
                            .build("mod_boat"));

    // Medium sailboat
    public static final RegistryObject<EntityType<ModBoatEntity>> MEDIUM_MOD_BOAT =
            ENTITY_TYPES.register("medium_mod_boat", () ->
                    EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                            .sized(1.55F, 0.40F)
                            .clientTrackingRange(10)
                            .build("medium_mod_boat"));

    // Large sailboat
    public static final RegistryObject<EntityType<ModBoatEntity>> LARGE_MOD_BOAT =
            ENTITY_TYPES.register("large_mod_boat", () ->
                    EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                            .sized(1.85F, 0.45F)
                            .clientTrackingRange(10)
                            .build("large_mod_boat"));

    public static final RegistryObject<EntityType<SailboatCollisionPartEntity>> SAILBOAT_COLLISION_PART =
            ENTITY_TYPES.register("sailboat_collision_part", () ->
                    EntityType.Builder.<SailboatCollisionPartEntity>of(SailboatCollisionPartEntity::new, MobCategory.MISC)
                            .sized(1.0F, 0.5F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .noSave()
                            .noSummon()
                            .build("sailboat_collision_part"));

    public static final RegistryObject<EntityType<PirateCaptainEntity>> PIRATE_CAPTAIN =
            ENTITY_TYPES.register("pirate_captain",
                    () -> EntityType.Builder.of(PirateCaptainEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("pirate_captain"));

    public static final RegistryObject<EntityType<PirateParrotEntity>> PIRATE_PARROT =
            ENTITY_TYPES.register("pirate_parrot",
                    () -> EntityType.Builder.of(PirateParrotEntity::new, MobCategory.MONSTER)
                            .sized(0.5F, 0.9F)
                            .clientTrackingRange(8)
                            .build("pirate_parrot"));

    public static final RegistryObject<EntityType<KrakenTentacleEntity>> KRAKEN_TENTACLE =
            ENTITY_TYPES.register("kraken_tentacle",
                    () -> EntityType.Builder.of(KrakenTentacleEntity::new, MobCategory.MISC)
                            .sized(1.0F, 2.0F)
                            .clientTrackingRange(10)
                            .build("kraken_tentacle"));

    public static final RegistryObject<EntityType<PirateMarauderEntity>> PIRATE_MARAUDER =
            ENTITY_TYPES.register("pirate_marauder",
                    () -> EntityType.Builder.of(PirateMarauderEntity::new, MobCategory.MONSTER)
                            .sized(0.8F, 2.2F)
                            .build("pirate_marauder"));

    public static final RegistryObject<EntityType<PirateGunnerEntity>> PIRATE_GUNNER =
            ENTITY_TYPES.register("pirate_gunner",
                    () -> EntityType.Builder.of(PirateGunnerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("pirate_gunner"));

    public static final RegistryObject<EntityType<PirateThrownWeaponEntity>> PIRATE_THROWN_WEAPON =
            ENTITY_TYPES.register("pirate_thrown_weapon", () ->
                    EntityType.Builder.<PirateThrownWeaponEntity>of(PirateThrownWeaponEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(8)
                            .updateInterval(10)
                            .build("pirate_thrown_weapon")
            );

    public static final RegistryObject<EntityType<PirateDynamiteArrowEntity>> PIRATE_DYNAMITE_ARROW =
            ENTITY_TYPES.register("pirate_dynamite_arrow",
                    () -> EntityType.Builder.<PirateDynamiteArrowEntity>of(PirateDynamiteArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("pirate_dynamite_arrow"));

    public static final RegistryObject<EntityType<PirateDeckhandEntity>> PIRATE_DECKHAND =
            ENTITY_TYPES.register("pirate_deckhand",
                    () -> EntityType.Builder.of(PirateDeckhandEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("pirate_deckhand"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}