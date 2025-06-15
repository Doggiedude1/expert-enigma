package com.dragonslayer.dragonsbuildtools.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Mob effect that reverses horizontal movement. Higher amplifier values
 * increase the speed multiplier while keeping the direction inverted.
 */
public class InverseSpeedEffect extends MobEffect {
    public InverseSpeedEffect() {
        super(MobEffectCategory.HARMFUL, 0x5E54ED);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 delta = entity.getDeltaMovement();
        double horizontalSpeed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        if (horizontalSpeed > 0.0D) {
            Vec3 dir = new Vec3(delta.x, 0.0D, delta.z).normalize();
            double newSpeed = horizontalSpeed * (amplifier + 1);
            entity.setDeltaMovement(-dir.x * newSpeed, delta.y, -dir.z * newSpeed);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
