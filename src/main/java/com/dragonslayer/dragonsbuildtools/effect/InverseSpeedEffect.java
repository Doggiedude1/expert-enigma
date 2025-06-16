package com.dragonslayer.dragonsbuildtools.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Mob effect that reverses horizontal movement. Higher amplifier values
 * increase the speed multiplier while keeping the direction inverted.
 */
public class InverseSpeedEffect extends MobEffect {
    public InverseSpeedEffect() {
        super(MobEffectCategory.HARMFUL, 0x5E54ED);
    }

    // No special behavior needed; the attribute modifier scales with amplifier automatically.
}
