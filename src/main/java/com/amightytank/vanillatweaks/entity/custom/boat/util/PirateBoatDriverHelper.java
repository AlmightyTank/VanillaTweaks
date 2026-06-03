package com.amightytank.vanillatweaks.entity.custom.boat.util;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.ArrayList;
import java.util.List;

public final class PirateBoatDriverHelper {
    private static final String RAID_PIRATE_TAG = "PirateTreasureRaid";

    private PirateBoatDriverHelper() {
    }

    public static void tickDriverPromotion(Boat boat) {
        if (boat.level().isClientSide) {
            return;
        }

        if (!boat.isVehicle()) {
            return;
        }

        List<Entity> passengers = boat.getPassengers();

        if (passengers.isEmpty()) {
            return;
        }

        Entity currentDriver = passengers.get(0);

        /*
         * If the front seat already has a living pirate, nothing needs to change.
         */
        if (isLivingPirate(currentDriver)) {
            return;
        }

        /*
         * Do not steal control from a living player or other living non-pirate.
         * This only fixes dead/removed/invalid pirate drivers.
         */
        if (currentDriver != null && currentDriver.isAlive() && !currentDriver.isRemoved()) {
            return;
        }

        Entity replacementDriver = null;

        for (int i = 1; i < passengers.size(); i++) {
            Entity passenger = passengers.get(i);

            if (isLivingPirate(passenger)) {
                replacementDriver = passenger;
                break;
            }
        }

        if (replacementDriver == null) {
            return;
        }

        promoteToFrontSeat(boat, replacementDriver);
    }

    private static void promoteToFrontSeat(Boat boat, Entity replacementDriver) {
        List<Entity> oldPassengers = new ArrayList<>(boat.getPassengers());

        for (Entity passenger : oldPassengers) {
            passenger.stopRiding();
        }

        /*
         * First passenger becomes the controlling/front-seat passenger.
         */
        replacementDriver.startRiding(boat, true);

        for (Entity passenger : oldPassengers) {
            if (passenger == replacementDriver) {
                continue;
            }

            if (!passenger.isAlive() || passenger.isRemoved()) {
                continue;
            }

            passenger.startRiding(boat, true);
        }
    }

    private static boolean isLivingPirate(Entity entity) {
        if (entity == null) {
            return false;
        }

        if (!entity.isAlive() || entity.isRemoved()) {
            return false;
        }

        return entity instanceof AbstractPirateEntity || entity.getTags().contains(RAID_PIRATE_TAG);
    }
}