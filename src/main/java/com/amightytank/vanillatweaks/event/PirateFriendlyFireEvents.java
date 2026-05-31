package com.amightytank.vanillatweaks.event;

import com.amightytank.vanillatweaks.VanillaTweaks;
import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateParrotEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VanillaTweaks.MOD_ID)
public final class PirateFriendlyFireEvents {
    private PirateFriendlyFireEvents() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity hurtEntity = event.getEntity();

        /*
         * Only block damage if the entity being hurt is also a pirate ally.
         * This means pirates can still damage players, villagers, normal mobs, etc.
         */
        if (!isPirateAlly(hurtEntity)) {
            return;
        }

        Entity attacker = event.getSource().getEntity();
        Entity directAttacker = event.getSource().getDirectEntity();

        /*
         * getEntity() catches the real owner of damage, like a pirate shooting an arrow.
         * getDirectEntity() catches direct damage entities if needed.
         */
        if (isPirateAlly(attacker) || isPirateAlly(directAttacker)) {
            event.setCanceled(true);
            event.setAmount(0.0F);
        }
    }

    public static boolean isPirateAlly(Entity entity) {
        if (entity == null) {
            return false;
        }

        if (entity instanceof AbstractPirateEntity) {
            return true;
        }

        if (entity instanceof PirateParrotEntity) {
            return true;
        }

        /*
         * These help if any raid pirates/boarders are being identified by tags.
         * Safe to keep even if most pirates already extend AbstractPirateEntity.
         */
        return entity.getTags().contains("PirateTreasureRaid")
                || entity.getTags().contains("PirateRaidBoarder");
    }
}