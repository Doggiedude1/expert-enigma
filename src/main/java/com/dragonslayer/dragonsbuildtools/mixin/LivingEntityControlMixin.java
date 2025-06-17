package com.dragonslayer.dragonsbuildtools.mixin;

import com.dragonslayer.dragonsbuildtools.event.RandomMobInheritEvents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows a virtual controller to drive any living entity even when no passenger is riding it.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityControlMixin {
    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    private void dragonsbuildtools$getVirtualController(CallbackInfoReturnable<LivingEntity> cir) {
        LivingEntity controller = RandomMobInheritEvents.getController((LivingEntity) (Object) this);
        if (controller != null) {
            cir.setReturnValue(controller);
        }
    }

    @Inject(method = "canBeControlledByRider", at = @At("HEAD"), cancellable = true)
    private void dragonsbuildtools$allowControl(CallbackInfoReturnable<Boolean> cir) {
        if (RandomMobInheritEvents.hasController((LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
