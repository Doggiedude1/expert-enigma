package com.dragonslayer.dragonsbuildtools.mixin;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Accessor mixin to modify the min value of a ranged attribute. */
@Mixin(RangedAttribute.class)
public interface RangedAttributeAccessor {
    @Mutable
    @Accessor("minValue")
    void dragonsbuildtools$setMinValue(double value);
}
