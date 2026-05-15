package com.amightytank.vanillatweaks.item.custom;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
import com.amightytank.vanillatweaks.entity.custom.boat.ModChestBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModBoatItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE =
            EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    private final ModBoatEntity.Type type;
    private final boolean hasChest;
    private final Supplier<? extends EntityType<? extends Boat>> boatEntity;

    public ModBoatItem(boolean hasChest,
                       ModBoatEntity.Type type,
                       Supplier<? extends EntityType<? extends Boat>> boatEntity,
                       Properties properties) {
        super(properties);
        this.hasChest = hasChest;
        this.type = type;
        this.boatEntity = boatEntity;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        }

        Vec3 viewVector = player.getViewVector(1.0F);
        List<Entity> entities = level.getEntities(
                player,
                player.getBoundingBox().expandTowards(viewVector.scale(5.0D)).inflate(1.0D),
                ENTITY_PREDICATE
        );

        if (!entities.isEmpty()) {
            Vec3 eyePosition = player.getEyePosition();

            for (Entity entity : entities) {
                AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                if (aabb.contains(eyePosition)) {
                    return InteractionResultHolder.pass(itemStack);
                }
            }
        }

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        }

        Boat boat = this.getBoat(level, hitResult, player.getYRot());

        // Set rotation before variant so the custom rectangular hitbox builds in the right direction
        boat.setYRot(player.getYRot());

        if (boat instanceof ModChestBoatEntity chestBoat) {
            chestBoat.setVariant(this.type);
        } else if (boat instanceof ModBoatEntity modBoat) {
            modBoat.setVariant(this.type);
        }

        if (!level.noCollision(boat, boat.getBoundingBox())) {
            return InteractionResultHolder.fail(itemStack);
        }

        if (!level.isClientSide) {
            level.addFreshEntity(boat);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.getLocation());

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    private Boat getBoat(Level level, HitResult hitResult, float yaw) {
        double x = hitResult.getLocation().x;
        double y = hitResult.getLocation().y;
        double z = hitResult.getLocation().z;

        Boat boat = this.boatEntity.get().create(level);

        if (boat == null) {
            boat = this.hasChest
                    ? new ModChestBoatEntity(level, x, y, z)
                    : new ModBoatEntity(level, x, y, z);
        }

        boat.moveTo(x, y, z, yaw, 0.0F);
        return boat;
    }
}