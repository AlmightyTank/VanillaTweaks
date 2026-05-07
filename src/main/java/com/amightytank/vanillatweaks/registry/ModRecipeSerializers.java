package com.amightytank.vanillatweaks.registry;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.recipe.MediumBoatRecipe;
import com.amightytank.vanillatweaks.recipe.SailBoatRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, VanillaTweaks.MODID);

    public static final Supplier<RecipeSerializer<SailBoatRecipe>> SAIL_BOAT =
            RECIPE_SERIALIZERS.register("sail_boat", () ->
                    new SimpleCraftingRecipeSerializer<>(SailBoatRecipe::new)
            );

    public static final Supplier<RecipeSerializer<MediumBoatRecipe>> MEDIUM_SAIL_BOAT =
            RECIPE_SERIALIZERS.register("medium_sail_boat", () ->
                    new SimpleCraftingRecipeSerializer<>(MediumBoatRecipe::new)
            );
}