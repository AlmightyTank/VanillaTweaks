package com.amightytank.vanillatweaks.recipe;

import com.amightytank.vanillatweaks.item.SailBoatItem;
import com.amightytank.vanillatweaks.registry.ModItems;
import com.amightytank.vanillatweaks.registry.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MediumBoatRecipe extends CustomRecipe {
    public MediumBoatRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int sailBoats = 0;
        int templates = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ModItems.SAIL_BOAT.get())) {
                sailBoats++;
            } else if (stack.is(ModItems.SAIL_BOAT_UPGRADE_TEMPLATE.get())) {
                templates++;
            } else {
                return false;
            }
        }

        return sailBoats == 1 && templates == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack sailBoatStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.is(ModItems.SAIL_BOAT.get())) {
                sailBoatStack = stack;
                break;
            }
        }

        ItemStack result = new ItemStack(ModItems.MEDIUM_BOAT.get());

        if (sailBoatStack.hasTag()) {
            CompoundTag oldTag = sailBoatStack.getTag();

            if (oldTag.contains(SailBoatItem.BANNER_STACK_TAG)) {
                result.getOrCreateTag().put(
                        SailBoatItem.BANNER_STACK_TAG,
                        oldTag.getCompound(SailBoatItem.BANNER_STACK_TAG).copy()
                );
            }

            if (oldTag.contains(SailBoatItem.WOOD_TYPE_TAG)) {
                result.getOrCreateTag().putString(
                        SailBoatItem.WOOD_TYPE_TAG,
                        oldTag.getString(SailBoatItem.WOOD_TYPE_TAG)
                );
            }
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MEDIUM_SAIL_BOAT.get();
    }
}