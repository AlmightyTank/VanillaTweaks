package com.amightytank.vanillatweaks.entity.ai;

import com.amightytank.vanillatweaks.entity.custom.pirate.AbstractPirateEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateBruteEntity;
import com.amightytank.vanillatweaks.entity.custom.pirate.PirateThrownWeaponEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PirateBruteAttackGoal extends Goal {
    private static final int MODE_NONE = 0;
    private static final int MODE_LUNGE = 1;
    private static final int MODE_THROW = 2;

    private final PirateBruteEntity brute;

    private int attackMode = MODE_NONE;
    private int attackTick;

    private int lungeCooldown = 20;
    private int throwCooldown = 40;

    private boolean hasHit;
    private boolean hasThrown;

    public PirateBruteAttackGoal(PirateBruteEntity brute) {
        this.brute = brute;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.brute.getTarget();
        return AbstractPirateEntity.canPirateAttack(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.brute.getTarget();
        return AbstractPirateEntity.canPirateAttack(target);
    }

    @Override
    public void start() {
        this.attackMode = MODE_NONE;
        this.attackTick = 0;
        this.hasHit = false;
        this.hasThrown = false;

        this.brute.setAggressive(true);
        this.brute.setAttackState(PirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);

        if (this.brute.getMainHandItem().isEmpty()) {
            this.brute.equipBruteWeapon();
        }
    }

    @Override
    public void stop() {
        this.attackMode = MODE_NONE;
        this.attackTick = 0;
        this.hasHit = false;
        this.hasThrown = false;

        this.brute.setAggressive(false);
        this.brute.setAttackState(PirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
        this.brute.getNavigation().stop();

        if (this.brute.getMainHandItem().isEmpty()) {
            this.brute.equipBruteWeapon();
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.brute.getTarget();

        if (!AbstractPirateEntity.canPirateAttack(target)) {
            this.brute.setTarget(null);
            return;
        }

        this.brute.setAggressive(true);

        Settings settings = this.brute.isTridentBrute() ? Settings.trident() : Settings.axe();

        this.brute.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (this.brute.isInWater()) {
            this.swimTowardTarget(target);
        }

        if (this.attackMode == MODE_LUNGE) {
            this.tickLunge(target, settings);
            return;
        }

        if (this.attackMode == MODE_THROW) {
            this.tickThrow(target, settings);
            return;
        }

        if (this.lungeCooldown > 0) {
            this.lungeCooldown--;
        }

        if (this.throwCooldown > 0) {
            this.throwCooldown--;
        }

        double distanceSqr = this.brute.distanceToSqr(target);

        double lungeRangeSqr = settings.lungeRange * settings.lungeRange;
        double throwMinRangeSqr = settings.throwMinRange * settings.throwMinRange;
        double throwMaxRangeSqr = settings.throwMaxRange * settings.throwMaxRange;

        boolean canLunge = distanceSqr <= lungeRangeSqr && this.lungeCooldown <= 0;

        boolean canThrow = distanceSqr >= throwMinRangeSqr
                && distanceSqr <= throwMaxRangeSqr
                && this.throwCooldown <= 0
                && this.brute.hasLineOfSight(target);

        if (canLunge) {
            this.beginLunge(settings);
            return;
        }

        if (canThrow) {
            this.beginThrow();
            return;
        }

        this.brute.getNavigation().moveTo(target, this.brute.isInWater() ? 1.45D : 1.18D);
    }

    private void beginLunge(Settings settings) {
        this.attackMode = MODE_LUNGE;
        this.attackTick = 0;
        this.hasHit = false;

        this.brute.equipBruteWeapon();

        this.brute.setAttackState(settings.windupState);
        this.brute.setAttackTick(0);
        this.brute.swing(InteractionHand.MAIN_HAND);
        this.brute.getNavigation().stop();
    }

    private void tickLunge(LivingEntity target, Settings settings) {
        this.attackTick++;
        this.brute.setAttackTick(this.attackTick);

        double distanceSqr = this.brute.distanceToSqr(target);
        double attackRangeSqr = settings.lungeRange * settings.lungeRange;

        if (this.attackTick < settings.lungeTick) {
            this.brute.setAttackState(settings.windupState);
            this.brute.getNavigation().stop();
        } else if (this.attackTick == settings.lungeTick) {
            this.brute.setAttackState(settings.hitState);
            this.lungeAt(target, settings);
        } else if (this.attackTick > settings.lungeTick && this.attackTick <= settings.recoverStartTick) {
            this.brute.setAttackState(settings.hitState);

            if (!this.hasHit && distanceSqr <= attackRangeSqr + 1.5D) {
                this.hasHit = true;

                boolean hit = this.brute.doHurtTarget(target);

                if (hit && settings.lungeKnockback > 0.0D) {
                    Vec3 push = target.position().subtract(this.brute.position()).normalize().scale(settings.lungeKnockback);
                    target.push(push.x, 0.18D, push.z);
                    target.hurtMarked = true;
                }
            }
        } else if (this.attackTick < settings.totalLungeTicks) {
            this.brute.setAttackState(settings.recoverState);
        } else {
            this.endLunge(settings);
        }
    }

    private void endLunge(Settings settings) {
        this.attackMode = MODE_NONE;
        this.attackTick = 0;
        this.hasHit = false;

        this.lungeCooldown = settings.lungeCooldownTicks;

        this.brute.setAttackState(PirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
    }

    private void beginThrow() {
        this.attackMode = MODE_THROW;
        this.attackTick = 0;
        this.hasThrown = false;

        this.brute.equipBruteWeapon();

        this.brute.setAttackState(PirateBruteEntity.THROW_WINDUP);
        this.brute.setAttackTick(0);
        this.brute.swing(InteractionHand.MAIN_HAND);
        this.brute.getNavigation().stop();
    }

    private void tickThrow(LivingEntity target, Settings settings) {
        this.attackTick++;
        this.brute.setAttackTick(this.attackTick);

        this.brute.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.brute.getNavigation().stop();

        if (this.attackTick < settings.throwReleaseTick) {
            this.brute.setAttackState(PirateBruteEntity.THROW_WINDUP);
        } else if (this.attackTick == settings.throwReleaseTick) {
            this.brute.setAttackState(PirateBruteEntity.THROW_RELEASE);

            if (!this.hasThrown) {
                this.hasThrown = true;

                this.fireThrownWeapon(target, settings);

                // Empty hand for recover frames so it looks like the weapon left his hand.
                this.brute.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
        } else if (this.attackTick < settings.totalThrowTicks) {
            this.brute.setAttackState(PirateBruteEntity.THROW_RECOVER);
        } else {
            this.endThrow(settings);
        }
    }

    private void endThrow(Settings settings) {
        this.attackMode = MODE_NONE;
        this.attackTick = 0;
        this.hasThrown = false;

        this.throwCooldown = settings.throwCooldownTicks;

        this.brute.equipBruteWeapon();

        this.brute.setAttackState(PirateBruteEntity.ATTACK_NONE);
        this.brute.setAttackTick(0);
    }

    private void fireThrownWeapon(LivingEntity target, Settings settings) {
        if (this.brute.level().isClientSide) {
            return;
        }

        PirateThrownWeaponEntity projectile = new PirateThrownWeaponEntity(this.brute.level(), this.brute);

        projectile.setWeaponType(this.brute.getBruteWeaponType());
        projectile.setThrownDamage(settings.throwDamage);
        projectile.setThrownKnockback(settings.throwKnockback);

        projectile.setPos(
                this.brute.getX(),
                this.brute.getEyeY() - 0.1D,
                this.brute.getZ()
        );

        double x = target.getX() - this.brute.getX();
        double y = target.getY(0.3333333333333333D) - projectile.getY();
        double z = target.getZ() - this.brute.getZ();

        double horizontalDistance = Math.sqrt(x * x + z * z);

        projectile.shoot(
                x,
                y + horizontalDistance * 0.2D,
                z,
                settings.throwVelocity,
                settings.throwInaccuracy
        );

        this.brute.level().addFreshEntity(projectile);
    }

    private void lungeAt(LivingEntity target, Settings settings) {
        Vec3 direction = target.position().subtract(this.brute.position());

        if (direction.lengthSqr() > 0.001D) {
            direction = direction.normalize();

            double x = direction.x * settings.lungePower;
            double z = direction.z * settings.lungePower;

            this.brute.setDeltaMovement(
                    this.brute.getDeltaMovement().add(x, this.brute.onGround() ? 0.12D : 0.04D, z)
            );

            this.brute.hurtMarked = true;
        }
    }

    private void swimTowardTarget(LivingEntity target) {
        Vec3 direction = target.position().subtract(this.brute.position());

        if (direction.lengthSqr() > 0.001D) {
            direction = direction.normalize();

            double x = direction.x * 0.035D;
            double y = Mth.clamp(direction.y * 0.035D, -0.02D, 0.04D);
            double z = direction.z * 0.035D;

            this.brute.setDeltaMovement(this.brute.getDeltaMovement().add(x, y, z));
        }
    }

    private record Settings(
            int windupState,
            int hitState,
            int recoverState,

            int lungeTick,
            int recoverStartTick,
            int totalLungeTicks,
            int lungeCooldownTicks,

            double lungeRange,
            double lungePower,
            double lungeKnockback,

            double throwMinRange,
            double throwMaxRange,
            int throwReleaseTick,
            int totalThrowTicks,
            int throwCooldownTicks,
            float throwDamage,
            float throwKnockback,
            float throwVelocity,
            float throwInaccuracy
    ) {
        private static Settings trident() {
            return new Settings(
                    PirateBruteEntity.SPEAR_WINDUP,
                    PirateBruteEntity.SPEAR_LUNGE,
                    PirateBruteEntity.SPEAR_RECOVER,

                    10,
                    14,
                    26,
                    30,

                    3.4D,
                    0.72D,
                    1.15D,

                    4.5D,
                    15.0D,
                    14,
                    28,
                    60,
                    8.0F,
                    0.7F,
                    1.65F,
                    3.0F
            );
        }

        private static Settings axe() {
            return new Settings(
                    PirateBruteEntity.AXE_WINDUP,
                    PirateBruteEntity.AXE_CHOP,
                    PirateBruteEntity.AXE_RECOVER,

                    14,
                    18,
                    36,
                    42,

                    2.7D,
                    0.55D,
                    1.65D,

                    4.0D,
                    12.0D,
                    16,
                    32,
                    75,
                    9.0F,
                    1.1F,
                    1.35F,
                    5.0F
            );
        }
    }
}