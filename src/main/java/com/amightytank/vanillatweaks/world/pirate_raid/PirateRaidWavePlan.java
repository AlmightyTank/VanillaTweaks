package com.amightytank.vanillatweaks.world.pirate_raid;

import java.util.List;

public class PirateRaidWavePlan {

    public static List<PirateShipSpawnEntry> getWave(PirateRaidSize raidSize, int wave) {
        return switch (raidSize) {
            case SMALL -> getSmallRaidWave(wave);
            case MEDIUM -> getMediumRaidWave(wave);
            case LARGE -> getLargeRaidWave(wave);
        };
    }

    private static List<PirateShipSpawnEntry> getSmallRaidWave(int wave) {
        return switch (wave) {
            case 1 -> List.of(
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL)
            );

            case 2 -> List.of(
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL)
            );

            case 3 -> List.of(
                    captainLoot(),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL)
            );

            default -> List.of();
        };
    }

    private static List<PirateShipSpawnEntry> getMediumRaidWave(int wave) {
        return switch (wave) {
            case 1 -> List.of(
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL)
            );

            case 2 -> List.of(
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL)
            );

            case 3 -> List.of(
                    captainLoot(),
                    combat(PirateShipSize.LARGE),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL)
            );

            default -> List.of();
        };
    }

    private static List<PirateShipSpawnEntry> getLargeRaidWave(int wave) {
        return switch (wave) {
            case 1 -> List.of(
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL)
            );

            case 2 -> List.of(
                    combat(PirateShipSize.LARGE),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.MEDIUM),
                    combat(PirateShipSize.SMALL),
                    combat(PirateShipSize.SMALL)
            );

            case 3 -> List.of(
                    captainLoot(),
                    combat(PirateShipSize.LARGE),
                    combat(PirateShipSize.LARGE),
                    combat(PirateShipSize.MEDIUM),
                    loot(PirateShipSize.MEDIUM)
            );

            default -> List.of();
        };
    }

    private static PirateShipSpawnEntry combat(PirateShipSize size) {
        return new PirateShipSpawnEntry(size, PirateShipRole.COMBAT);
    }

    private static PirateShipSpawnEntry loot(PirateShipSize size) {
        return new PirateShipSpawnEntry(size, PirateShipRole.LOOT);
    }

    private static PirateShipSpawnEntry captainLoot() {
        return new PirateShipSpawnEntry(PirateShipSize.LARGE, PirateShipRole.CAPTAIN_LOOT);
    }
}