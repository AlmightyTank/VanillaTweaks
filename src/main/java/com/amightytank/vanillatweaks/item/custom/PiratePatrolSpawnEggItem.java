package com.amightytank.vanillatweaks.item.custom;

import com.amightytank.vanillatweaks.world.PiratePatrolSpawner;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class PiratePatrolSpawnEggItem extends Item {

    public PiratePatrolSpawnEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos spawnPos = context.getClickedPos()
                .relative(context.getClickedFace())
                .above();

        PiratePatrolSpawner.spawnTestPatrol(level, spawnPos, context.getPlayer());

        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(
                    Component.literal("Spawned Pirate Patrol")
                            .withStyle(ChatFormatting.GOLD),
                    true
            );

            if (!context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.CONSUME;
    }
}