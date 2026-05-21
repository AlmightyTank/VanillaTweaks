package com.amightytank.vanillatweaks.item.custom;

import com.amightytank.vanillatweaks.world.PiratePatrolSpawner;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class PiratePatrolSpawnEggItem extends ForgeSpawnEggItem {

    public PiratePatrolSpawnEggItem(
            Supplier<? extends EntityType<? extends Mob>> captainEntityType,
            int primaryColor,
            int secondaryColor,
            Properties properties
    ) {
        super(captainEntityType, primaryColor, secondaryColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos spawnPos = context.getClickedPos()
                .relative(context.getClickedFace())
                .above();

        Entity user = context.getPlayer();

        PiratePatrolSpawner.spawnTestPatrol(level, spawnPos, user);

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