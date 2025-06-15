package com.dragonslayer.dragonsbuildtools.effect;

import com.dragonslayer.dragonsbuildtools.attribute.ModAttributes;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Mob effect that reverses horizontal movement. Higher amplifier values
 * increase the speed multiplier while keeping the direction inverted.
 */
public class InverseSpeedEffect extends MobEffect {
    private static final UUID ATTRIBUTE_UUID = UUID.fromString("77b53770-2af8-4f07-bf62-f682baa4d38a");

    public InverseSpeedEffect() {
        super(MobEffectCategory.HARMFUL, 0x5E54ED);
        addAttributeModifier(ModAttributes.INVERSE_SPEED.get(), ATTRIBUTE_UUID.toString(), -1.0D, AttributeModifier.Operation.ADDITION);
    }
}
