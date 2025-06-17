package com.dragonslayer.dragonsbuildtools.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.dragonslayer.dragonsbuildtools.accessor.ControllingPassengerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows setting a custom controlling passenger without needing a real rider.
 */
@Mixin(Entity.class)
public abstract class EntityControlMixin implements ControllingPassengerAccessor {
    @Unique private LivingEntity dragonsbuildtools$controller;

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    private void dragonsbuildtools$overrideController(CallbackInfoReturnable<LivingEntity> cir) {
        if (this.dragonsbuildtools$controller != null) {
            cir.setReturnValue(this.dragonsbuildtools$controller);
        }
    }

    @Override
    public void dragonsbuildtools$setControllingPassenger(LivingEntity passenger) {
        this.dragonsbuildtools$controller = passenger;
    }

    @Override
    public LivingEntity dragonsbuildtools$getControllingPassenger() {
        return this.dragonsbuildtools$controller;
    }
}
