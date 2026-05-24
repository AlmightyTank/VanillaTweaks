package com.amightytank.vanillatweaks.world.pirate_raid;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class PirateTreasureRaidCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("piratetreasure")
                        .requires(source -> source.hasPermission(2))

                        .then(Commands.literal("start")
                                .then(Commands.argument("size", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ServerLevel level = player.serverLevel();

                                            String sizeName = StringArgumentType.getString(context, "size");
                                            PirateRaidSize raidSize = parseRaidSize(sizeName);

                                            if (raidSize == null) {
                                                context.getSource().sendFailure(
                                                        Component.literal("Invalid raid size. Use small, medium, or large.")
                                                );
                                                return 0;
                                            }

                                            BlockPos raidPos = player.blockPosition();

                                            PirateTreasureRaidManager.startRaid(
                                                    level,
                                                    player,
                                                    raidPos,
                                                    raidSize
                                            );

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("Started " + sizeName + " pirate treasure raid."),
                                                    true
                                            );

                                            return 1;
                                        })
                                )
                        )
        );
    }

    private static PirateRaidSize parseRaidSize(String name) {
        return switch (name.toLowerCase()) {
            case "small" -> PirateRaidSize.SMALL;
            case "medium" -> PirateRaidSize.MEDIUM;
            case "large" -> PirateRaidSize.LARGE;
            default -> null;
        };
    }
}