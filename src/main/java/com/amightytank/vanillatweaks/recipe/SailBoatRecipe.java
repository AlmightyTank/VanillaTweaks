package com.amightytank.vanillatweaks.recipe;

import com.amightytank.vanillatweaks.item.SailBoatItem;
import com.amightytank.vanillatweaks.registry.ModItems;
import com.amightytank.vanillatweaks.registry.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SailBoatRecipe extends CustomRecipe {
    public SailBoatRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int banners = 0;
        int boats = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ItemTags.BANNERS)) {
                banners++;
            } else if (stack.getItem() instanceof BoatItem) {
                boats++;
            } else {
                return false;
            }
        }

        return banners == 1 && boats == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack bannerStack = ItemStack.EMPTY;
        ItemStack boatStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.is(ItemTags.BANNERS)) {
                bannerStack = stack;
            } else if (stack.getItem() instanceof BoatItem) {
                boatStack = stack;
            }
        }

        ItemStack result = new ItemStack(ModItems.SAIL_BOAT.get());

        if (!bannerStack.isEmpty()) {
            SailBoatItem.setBannerStack(result, bannerStack);
        }

        SailBoatItem.setWoodType(result, getWoodTypeFromBoat(boatStack));

        return result;
    }

    private String getWoodTypeFromBoat(ItemStack stack) {
        if (stack.is(Items.OAK_BOAT)) return "oak";
        if (stack.is(Items.SPRUCE_BOAT)) return "spruce";
        if (stack.is(Items.BIRCH_BOAT)) return "birch";
        if (stack.is(Items.JUNGLE_BOAT)) return "jungle";
        if (stack.is(Items.ACACIA_BOAT)) return "acacia";
        if (stack.is(Items.DARK_OAK_BOAT)) return "dark_oak";
        if (stack.is(Items.MANGROVE_BOAT)) return "mangrove";
        if (stack.is(Items.CHERRY_BOAT)) return "cherry";
        if (stack.is(Items.BAMBOO_RAFT)) return "bamboo";

        return "oak";
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SAIL_BOAT.get();
    }
}