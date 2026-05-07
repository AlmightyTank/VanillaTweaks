package com.amightytank.vanillatweaks.item;

import com.amightytank.vanillatweaks.entity.MediumBoatEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.List;
import java.util.function.Predicate;

public class MediumBoatItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE =
            EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public MediumBoatItem(Properties properties) {
        super(properties);
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
            MediumBoatEntity boat = new MediumBoatEntity(
                    level,
                    hit.getLocation().x,
                    hit.getLocation().y,
                    hit.getLocation().z
            );

            boat.setYRot(player.getYRot());
            boat.setWoodType(SailBoatItem.getWoodType(stack));

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
}