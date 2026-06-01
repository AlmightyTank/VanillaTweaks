package com.amightytank.vanillatweaks.item.custom;

import com.amightytank.vanillatweaks.entity.custom.boat.ModBoatEntity;
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

    private final Boat.Type type;
    private final int bannerCount;
    private final int startingChestCount;
    private final Supplier<? extends EntityType<? extends Boat>> boatEntity;

    /**
     * Old constructor support.
     *
     * hasChest true now means:
     * Spawn a normal ModBoatEntity with 1 chest already attached.
     *
     * If you are deleting all chest boat items, use the constructor below with bannerCount instead.
     */
    public ModBoatItem(boolean hasChest,
                       Boat.Type type,
                       Supplier<? extends EntityType<? extends Boat>> boatEntity,
                       Properties properties) {
        this(hasChest, type, 1, boatEntity, properties);
    }

    /**
     * Main constructor.
     *
     * bannerCount:
     * 1 = small sailboat
     * 2 = medium sailboat
     * 3 = large sailboat
     */
    public ModBoatItem(boolean hasChest,
                       Boat.Type type,
                       int bannerCount,
                       Supplier<? extends EntityType<? extends Boat>> boatEntity,
                       Properties properties) {
        super(properties);
        this.type = type;
        this.bannerCount = Math.max(1, Math.min(3, bannerCount));
        this.startingChestCount = hasChest ? 1 : 0;
        this.boatEntity = boatEntity;
    }

    /**
     * Clean constructor for the new system.
     *
     * Use this for normal sailboat items.
     */
    public ModBoatItem(Boat.Type type,
                       int bannerCount,
                       Supplier<? extends EntityType<? extends Boat>> boatEntity,
                       Properties properties) {
        this(false, type, bannerCount, boatEntity, properties);
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

        Boat createdBoat = this.boatEntity.get().create(level);
        ModBoatEntity boat;

        if (createdBoat instanceof ModBoatEntity modBoat) {
            boat = modBoat;
        } else {
            boat = new ModBoatEntity(level, x, y, z);
        }

        boat.setModVariant(this.type);
        boat.setBannerCount(this.bannerCount);
        boat.setChestCount(this.startingChestCount);

        boat.moveTo(x, y, z, yaw, 0.0F);
        boat.setYRot(yaw);

        return boat;
    }
}