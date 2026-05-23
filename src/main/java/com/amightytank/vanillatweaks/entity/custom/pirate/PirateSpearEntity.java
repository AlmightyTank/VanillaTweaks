package com.amightytank.vanillatweaks.entity.custom.pirate;

import com.amightytank.vanillatweaks.entity.ModEntities;
import com.amightytank.vanillatweaks.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class PirateSpearEntity extends ThrownTrident {
    private static final EntityDataAccessor<Byte> DATA_LOYALTY =
            SynchedEntityData.defineId(PirateSpearEntity.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Boolean> DATA_FOIL =
            SynchedEntityData.defineId(PirateSpearEntity.class, EntityDataSerializers.BOOLEAN);

    private ItemStack spearItem = new ItemStack(ModItems.PIRATE_SPEAR.get());
    private boolean dealtDamage;
    public int clientSideReturnSpearTickCount;

    public PirateSpearEntity(EntityType<? extends ThrownTrident> entityType, Level level) {
        super(entityType, level);
    }

    public PirateSpearEntity(Level level, LivingEntity owner, ItemStack stack) {
        super(ModEntities.PIRATE_SPEAR.get(), level);

        this.spearItem = stack.copy();
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY(), owner.getZ());

        this.entityData.set(DATA_LOYALTY, (byte) EnchantmentHelper.getLoyalty(stack));
        this.entityData.set(DATA_FOIL, stack.hasFoil());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LOYALTY, (byte) 0);
        this.entityData.define(DATA_FOIL, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity owner = this.getOwner();
        int loyalty = this.entityData.get(DATA_LOYALTY);

        if (loyalty > 0 && (this.dealtDamage || this.isNoPhysics()) && owner != null) {
            if (!this.isAcceptableReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
                return;
            }

            this.setNoPhysics(true);

            Vec3 returnVector = owner.getEyePosition().subtract(this.position());
            this.setPosRaw(
                    this.getX(),
                    this.getY() + returnVector.y * 0.015D * loyalty,
                    this.getZ()
            );

            if (this.level().isClientSide) {
                this.yOld = this.getY();
            }

            double speed = 0.05D * loyalty;

            this.setDeltaMovement(
                    this.getDeltaMovement()
                            .scale(0.95D)
                            .add(returnVector.normalize().scale(speed))
            );

            if (this.clientSideReturnSpearTickCount == 0) {
                this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
            }

            ++this.clientSideReturnSpearTickCount;
        }

        super.tick();
    }

    private boolean isAcceptableReturnOwner() {
        Entity owner = this.getOwner();

        if (owner == null || !owner.isAlive()) {
            return false;
        }

        return !(owner instanceof ServerPlayer serverPlayer) || !serverPlayer.isSpectator();
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.spearItem.copy();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity hitEntity = hitResult.getEntity();

        float damage = 8.0F;

        if (hitEntity instanceof LivingEntity livingTarget) {
            damage += EnchantmentHelper.getDamageBonus(this.spearItem, livingTarget.getMobType());
        }

        Entity owner = this.getOwner();
        Entity damageOwner = owner == null ? this : owner;

        this.dealtDamage = true;

        SoundEvent soundEvent = SoundEvents.TRIDENT_HIT;

        if (hitEntity.hurt(this.damageSources().trident(this, damageOwner), damage)) {
            if (hitEntity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (hitEntity instanceof LivingEntity livingTarget) {
                if (owner instanceof LivingEntity livingOwner) {
                    EnchantmentHelper.doPostHurtEffects(livingTarget, livingOwner);
                    EnchantmentHelper.doPostDamageEffects(livingOwner, livingTarget);
                }

                this.doPostHurtEffects(livingTarget);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));

        if (this.level() instanceof ServerLevel serverLevel
                && this.level().isThundering()
                && EnchantmentHelper.hasChanneling(this.spearItem)) {
            var blockPos = hitEntity.blockPosition();

            if (this.level().canSeeSky(blockPos)) {
                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(this.level());

                if (lightningBolt != null) {
                    lightningBolt.moveTo(
                            blockPos.getX() + 0.5D,
                            blockPos.getY(),
                            blockPos.getZ() + 0.5D
                    );

                    lightningBolt.setCause(owner instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                    serverLevel.addFreshEntity(lightningBolt);

                    soundEvent = SoundEvents.TRIDENT_THUNDER;
                }
            }
        }

        this.playSound(soundEvent, soundEvent == SoundEvents.TRIDENT_THUNDER ? 5.0F : 1.0F, 1.0F);
    }

    @Override
    public void playerTouch(Player player) {
        Entity owner = this.getOwner();

        if (owner == null || owner.getUUID() == player.getUUID()) {
            super.playerTouch(player);
        }
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player)
                || this.isNoPhysics()
                && this.ownedBy(player)
                && player.getInventory().add(this.getPickupItem());
    }

    @Override
    public void tickDespawn() {
        int loyalty = this.entityData.get(DATA_LOYALTY);

        if (this.pickup != AbstractArrow.Pickup.ALLOWED || loyalty <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        super.lerpMotion(x, y, z);
        this.clientSideReturnSpearTickCount = 0;
    }

    @Override
    public boolean isFoil() {
        return this.entityData.get(DATA_FOIL);
    }

    public ItemStack getSpearItem() {
        return this.spearItem;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("PirateSpear", this.spearItem.save(new CompoundTag()));
        tag.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("PirateSpear", 10)) {
            this.spearItem = ItemStack.of(tag.getCompound("PirateSpear"));
        }

        if (this.spearItem.isEmpty()) {
            this.spearItem = new ItemStack(ModItems.PIRATE_SPEAR.get());
        }

        this.dealtDamage = tag.getBoolean("DealtDamage");
        this.entityData.set(DATA_LOYALTY, (byte) EnchantmentHelper.getLoyalty(this.spearItem));
        this.entityData.set(DATA_FOIL, this.spearItem.hasFoil());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}