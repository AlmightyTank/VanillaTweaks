package com.amightytank.vanillatweaks.world.pirate_raid;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class PirateTreasureRaidSavedData extends SavedData {
    private static final String DATA_NAME = "vanillatweaks_pirate_treasure_raids";

    private final Set<Long> triggeredTreasures = new HashSet<>();

    public static PirateTreasureRaidSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                PirateTreasureRaidSavedData::load,
                PirateTreasureRaidSavedData::new,
                DATA_NAME
        );
    }

    public static PirateTreasureRaidSavedData load(CompoundTag tag) {
        PirateTreasureRaidSavedData data = new PirateTreasureRaidSavedData();

        long[] positions = tag.getLongArray("TriggeredTreasures");

        for (long position : positions) {
            data.triggeredTreasures.add(position);
        }

        return data;
    }

    public boolean hasTriggered(BlockPos treasurePos) {
        return this.triggeredTreasures.contains(treasurePos.asLong());
    }

    public void markTriggered(BlockPos treasurePos) {
        this.triggeredTreasures.add(treasurePos.asLong());
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        long[] positions = this.triggeredTreasures.stream()
                .mapToLong(Long::longValue)
                .toArray();

        tag.putLongArray("TriggeredTreasures", positions);

        return tag;
    }
}