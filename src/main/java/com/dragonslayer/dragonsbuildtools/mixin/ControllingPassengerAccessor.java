package com.dragonslayer.dragonsbuildtools.mixin;

import net.minecraft.world.entity.LivingEntity;

/**
 * Simple accessor interface for setting a custom controlling passenger.
 */
public interface ControllingPassengerAccessor {
    /** Set the entity considered the controlling passenger. */
    void dragonsbuildtools$setControllingPassenger(LivingEntity passenger);

    /** Get the custom controlling passenger if present. */
    LivingEntity dragonsbuildtools$getControllingPassenger();
}
