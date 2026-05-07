package com.amightytank.vanillatweaks.item;

import com.amightytank.vanillatweaks.entity.SailBoatEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SailBoatItem extends Item {
    public static final String BANNER_STACK_TAG = "BannerStack";
    public static final String WOOD_TYPE_TAG = "WoodType";

    private static final Predicate<Entity> ENTITY_PREDICATE =
            EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public SailBoatItem(Properties properties) {
        super(properties);
    }

    public static void setBannerStack(ItemStack sailBoatStack, ItemStack bannerStack) {
        if (bannerStack.isEmpty() || !(bannerStack.getItem() instanceof BannerItem)) {
            return;
        }

        CompoundTag tag = new CompoundTag();
        sailBoatStack.getOrCreateTag().put(BANNER_STACK_TAG, bannerStack.save(tag));
    }

    public static ItemStack getBannerStack(ItemStack sailBoatStack) {
        if (!sailBoatStack.hasTag() || !sailBoatStack.getTag().contains(BANNER_STACK_TAG)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.of(sailBoatStack.getTag().getCompound(BANNER_STACK_TAG));
    }

    public static void setWoodType(ItemStack stack, String woodType) {
        stack.getOrCreateTag().putString(WOOD_TYPE_TAG, woodType);
    }

    public static String getWoodType(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(WOOD_TYPE_TAG)) {
            return stack.getTag().getString(WOOD_TYPE_TAG);
        }

        return "oak";
    }

    public static boolean hasBanner(ItemStack stack) {
        return !getBannerStack(stack).isEmpty();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        }

        Vec3 view = player.getViewVector(1.0F);
        List<Entity> entities = level.getEntities(
                player,
                player.getBoundingBox().expandTowards(view.scale(5.0D)).inflate(1.0D),
                ENTITY_PREDICATE
        );

        if (!entities.isEmpty()) {
            Vec3 eyePos = player.getEyePosition();

            for (Entity entity : entities) {
                AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
                if (box.contains(eyePos)) {
                    return InteractionResultHolder.pass(stack);
                }
            }
        }

        if (hit.getType() == HitResult.Type.BLOCK) {
            SailBoatEntity boat = new SailBoatEntity(
                    level,
                    hit.getLocation().x,
                    hit.getLocation().y,
                    hit.getLocation().z
            );

            boat.setYRot(player.getYRot());
            boat.setWoodType(getWoodType(stack));

            ItemStack banner = SailBoatItem.getBannerStack(stack);
            if (!banner.isEmpty()) {
                boat.setBannerStack(banner);
            }

            if (!level.noCollision(boat, boat.getBoundingBox())) {
                return InteractionResultHolder.fail(stack);
            }

            if (!level.isClientSide) {
                level.addFreshEntity(boat);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        ItemStack banner = getBannerStack(stack);

        if (!banner.isEmpty()) {
            return Component.translatable("item.vanillatweaks.sail_boat.with_banner", banner.getHoverName());
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String woodType = getWoodType(stack);
        tooltip.add(Component.literal("Wood: " + formatWoodName(woodType)).withStyle(ChatFormatting.GRAY));

        ItemStack banner = getBannerStack(stack);
        if (!banner.isEmpty()) {
            tooltip.add(Component.literal("Sail: ").withStyle(ChatFormatting.GRAY)
                    .append(banner.getHoverName().copy().withStyle(ChatFormatting.WHITE)));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }

    private static String formatWoodName(String woodType) {
        return switch (woodType) {
            case "dark_oak" -> "Dark Oak";
            default -> {
                String[] parts = woodType.split("_");
                StringBuilder name = new StringBuilder();
                for (String part : parts) {
                    if (!name.isEmpty()) {
                        name.append(" ");
                    }
                    name.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
                }
                yield name.toString();
            }
        };
    }
}