package com.amightytank.vanillatweaks.command;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.world.PiratePatrolFormation;
import com.amightytank.vanillatweaks.world.PiratePatrolSize;
import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = VanillaTweaks.MOD_ID)
public class ModCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("piratepatrol")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("spawn")
                                .executes(context -> spawnPatrol(
                                        context.getSource(),
                                        getRandomSize(context.getSource().getLevel())
                                ))
                                .then(Commands.literal("small")
                                        .executes(context -> spawnPatrol(
                                                context.getSource(),
                                                PiratePatrolSize.SMALL
                                        )))
                                .then(Commands.literal("medium")
                                        .executes(context -> spawnPatrol(
                                                context.getSource(),
                                                PiratePatrolSize.MEDIUM
                                        )))
                                .then(Commands.literal("large")
                                        .executes(context -> spawnPatrol(
                                                context.getSource(),
                                                PiratePatrolSize.LARGE
                                        )))
                        )
        );
    }

    private static int spawnPatrol(CommandSourceStack source, PiratePatrolSize size) {
        ServerPlayer player;

        try {
            player = source.getPlayerOrException();
        } catch (Exception exception) {
            source.sendFailure(Component.literal("This command must be run by a player."));
            return 0;
        }

        ServerLevel level = player.serverLevel();

        List<Mob> spawnedPirates = PiratePatrolFormation.spawnPatrol(
                level,
                player
        );

        if (spawnedPirates.isEmpty()) {
            source.sendFailure(Component.literal("Could not find nearby surface water for the pirate patrol."));
            return 0;
        }

        source.sendSuccess(
                () -> Component.literal(
                        "Spawned "
                                + size.name().toLowerCase()
                                + " pirate patrol with "
                                + spawnedPirates.size()
                                + " pirates."
                ),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static PiratePatrolSize getRandomSize(ServerLevel level) {
        int roll = level.random.nextInt(3);

        if (roll == 0) {
            return PiratePatrolSize.SMALL;
        }

        if (roll == 1) {
            return PiratePatrolSize.MEDIUM;
        }

        return PiratePatrolSize.LARGE;
    }
}