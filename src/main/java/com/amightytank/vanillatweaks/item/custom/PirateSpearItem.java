package com.amightytank.vanillatweaks.item.custom;

import com.amightytank.vanillatweaks.entity.client.pirate.PirateSpearItemRenderer;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateSpearEntity;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PirateSpearItem extends TridentItem {

    public PirateSpearItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(stack);
        }

        int riptideLevel = EnchantmentHelper.getRiptide(stack);

        if (riptideLevel > 0 && !player.isInWaterOrRain()) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft) {
        if (!(user instanceof Player player)) {
            return;
        }

        int chargeTime = this.getUseDuration(stack) - timeLeft;

        if (chargeTime < 10) {
            return;
        }

        int riptideLevel = EnchantmentHelper.getRiptide(stack);

        if (riptideLevel > 0 && !player.isInWaterOrRain()) {
            return;
        }

        if (!level.isClientSide) {
            stack.hurtAndBreak(1, player, brokenPlayer ->
                    brokenPlayer.broadcastBreakEvent(user.getUsedItemHand())
            );

            if (riptideLevel == 0) {
                PirateSpearEntity spear = new PirateSpearEntity(level, player, stack);

                spear.shootFromRotation(
                        player,
                        player.getXRot(),
                        player.getYRot(),
                        0.0F,
                        2.5F + riptideLevel * 0.5F,
                        1.0F
                );

                if (player.getAbilities().instabuild) {
                    spear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }

                level.addFreshEntity(spear);
                level.playSound(null, spear, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

                if (!player.getAbilities().instabuild) {
                    player.getInventory().removeItem(stack);
                }
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        if (riptideLevel > 0) {
            float yRot = player.getYRot();
            float xRot = player.getXRot();

            float x = -Mth.sin(yRot * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
            float y = -Mth.sin(xRot * ((float) Math.PI / 180F));
            float z = Mth.cos(yRot * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));

            float length = Mth.sqrt(x * x + y * y + z * z);
            float power = 3.0F * ((1.0F + (float) riptideLevel) / 4.0F);

            x *= power / length;
            y *= power / length;
            z *= power / length;

            player.push(x, y, z);
            player.startAutoSpinAttack(20);

            if (player.onGround()) {
                player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999284744263D, 0.0D));
            }

            SoundEvent soundEvent;

            if (riptideLevel >= 3) {
                soundEvent = SoundEvents.TRIDENT_RIPTIDE_3;
            } else if (riptideLevel == 2) {
                soundEvent = SoundEvents.TRIDENT_RIPTIDE_2;
            } else {
                soundEvent = SoundEvents.TRIDENT_RIPTIDE_1;
            }

            level.playSound(null, player, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private com.amightytank.vanillatweaks.entity.client.pirate.PirateSpearItemRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.amightytank.vanillatweaks.entity.client.pirate.PirateSpearItemRenderer();
                }

                return this.renderer;
            }
        });
    }
}