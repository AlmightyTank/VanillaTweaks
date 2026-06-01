package com.amightytank.vanillatweaks.entity.ai.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public final class PirateLookHelper {
    private static final float MAX_HEAD_YAW = 30.0F;
    private static final float MAX_HEAD_PITCH = 30.0F;

    private PirateLookHelper() {
    }

    public static void lookAtEntity(Mob pirate, Entity target) {
        if (pirate == null || target == null || !target.isAlive()) {
            return;
        }

        pirate.getLookControl().setLookAt(target, MAX_HEAD_YAW, MAX_HEAD_PITCH);
    }
}