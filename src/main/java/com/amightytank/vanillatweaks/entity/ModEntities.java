package com.amightytank.vanillatweaks.entity;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.boat.ModChestBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.KrakenTentacleEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateCaptainEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateParrotEntity;
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

    public static final RegistryObject<EntityType<ModChestBoatEntity>> MOD_CHEST_BOAT =
            ENTITY_TYPES.register("mod_chest_boat", () ->
                    EntityType.Builder.<ModChestBoatEntity>of(ModChestBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.35F)
                            .clientTrackingRange(10)
                            .build("mod_chest_boat"));

    // Medium sailboat
    public static final RegistryObject<EntityType<ModBoatEntity>> MEDIUM_MOD_BOAT =
            ENTITY_TYPES.register("medium_mod_boat", () ->
                    EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                            .sized(1.55F, 0.40F)
                            .clientTrackingRange(10)
                            .build("medium_mod_boat"));

    public static final RegistryObject<EntityType<ModChestBoatEntity>> MEDIUM_MOD_CHEST_BOAT =
            ENTITY_TYPES.register("medium_mod_chest_boat", () ->
                    EntityType.Builder.<ModChestBoatEntity>of(ModChestBoatEntity::new, MobCategory.MISC)
                            .sized(1.55F, 0.40F)
                            .clientTrackingRange(10)
                            .build("medium_mod_chest_boat"));

    // Large sailboat
    public static final RegistryObject<EntityType<ModBoatEntity>> LARGE_MOD_BOAT =
            ENTITY_TYPES.register("large_mod_boat", () ->
                    EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                            .sized(1.85F, 0.45F)
                            .clientTrackingRange(10)
                            .build("large_mod_boat"));

    public static final RegistryObject<EntityType<ModChestBoatEntity>> LARGE_MOD_CHEST_BOAT =
            ENTITY_TYPES.register("large_mod_chest_boat", () ->
                    EntityType.Builder.<ModChestBoatEntity>of(ModChestBoatEntity::new, MobCategory.MISC)
                            .sized(1.85F, 0.45F)
                            .clientTrackingRange(10)
                            .build("large_mod_chest_boat"));

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
                    () -> EntityType.Builder.of(KrakenTentacleEntity::new, MobCategory.MONSTER)
                            .sized(1.0F, 2.0F)
                            .clientTrackingRange(8)
                            .build("kraken_tentacle"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}