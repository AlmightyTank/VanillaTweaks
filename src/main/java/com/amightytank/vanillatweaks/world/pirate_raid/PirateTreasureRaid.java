package com.amightytank.vanillatweaks.world.pirate_raid;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PirateTreasureRaid {
    private static final int MAX_WAVES = 3;
    private static final int FIRST_WAVE_DELAY = 20;
    private static final int NEXT_WAVE_DELAY = 20 * 8;

    private final UUID raidId = UUID.randomUUID();
    private final ResourceKey<Level> dimension;
    private final UUID playerId;
    private final BlockPos treasurePos;
    private final PirateRaidSize raidSize;

    private final List<UUID> activePirates = new ArrayList<>();

    private int currentWave = 0;
    private int waveCooldown = FIRST_WAVE_DELAY;
    private boolean finished = false;
    private boolean captainLootShipSpawned = false;

    public PirateTreasureRaid(
            ResourceKey<Level> dimension,
            UUID playerId,
            BlockPos treasurePos,
            PirateRaidSize raidSize
    ) {
        this.dimension = dimension;
        this.playerId = playerId;
        this.treasurePos = treasurePos.immutable();
        this.raidSize = raidSize;
    }

    public void tick(ServerLevel level) {
        if (this.finished) {
            return;
        }

        cleanupDeadPirates(level);

        if (!this.activePirates.isEmpty()) {
            return;
        }

        if (this.currentWave >= MAX_WAVES) {
            finish(level);
            return;
        }

        if (this.waveCooldown > 0) {
            this.waveCooldown--;
            return;
        }

        spawnNextWave(level);
    }

    private void spawnNextWave(ServerLevel level) {
        this.currentWave++;

        ServerPlayer player = level.getServer().getPlayerList().getPlayer(this.playerId);

        if (player == null || !player.isAlive()) {
            this.finished = true;
            return;
        }

        List<PirateShipSpawnEntry> wavePlan = PirateRaidWavePlan.getWave(this.raidSize, this.currentWave);

        if (containsCaptainLootShip(wavePlan)) {
            if (this.captainLootShipSpawned) {
                wavePlan = removeCaptainLootShip(wavePlan);
            } else {
                this.captainLootShipSpawned = true;
            }
        }

        List<Mob> spawnedPirates = PirateShipSpawner.spawnWave(
                level,
                player,
                this.treasurePos,
                this.currentWave,
                this.raidId,
                wavePlan
        );

        for (Mob pirate : spawnedPirates) {
            this.activePirates.add(pirate.getUUID());
        }

        player.displayClientMessage(
                Component.literal("Pirate raid wave " + this.currentWave + " has arrived!"),
                true
        );

        this.waveCooldown = NEXT_WAVE_DELAY;
    }

    private boolean containsCaptainLootShip(List<PirateShipSpawnEntry> wavePlan) {
        for (PirateShipSpawnEntry entry : wavePlan) {
            if (entry.role() == PirateShipRole.CAPTAIN_LOOT) {
                return true;
            }
        }

        return false;
    }

    private List<PirateShipSpawnEntry> removeCaptainLootShip(List<PirateShipSpawnEntry> wavePlan) {
        List<PirateShipSpawnEntry> cleanedPlan = new ArrayList<>();

        for (PirateShipSpawnEntry entry : wavePlan) {
            if (entry.role() != PirateShipRole.CAPTAIN_LOOT) {
                cleanedPlan.add(entry);
            }
        }

        return cleanedPlan;
    }

    private void cleanupDeadPirates(ServerLevel level) {
        this.activePirates.removeIf(uuid -> {
            Entity entity = level.getEntity(uuid);

            if (!(entity instanceof Mob mob)) {
                return true;
            }

            return !mob.isAlive();
        });
    }

    private void finish(ServerLevel level) {
        this.finished = true;

        ServerPlayer player = level.getServer().getPlayerList().getPlayer(this.playerId);

        if (player != null) {
            player.displayClientMessage(
                    Component.literal("The pirate raid has been defeated!"),
                    true
            );
        }
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public boolean isFinished() {
        return this.finished;
    }
}