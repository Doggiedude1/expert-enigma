package com.dragonslayer.dragonsbuildtools.mixin;

import com.dragonslayer.dragonsbuildtools.attribute.ModAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects the inverse speed attribute into the base living entity attribute builder
 * so every entity type, including players, has it available.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void dragonsbuildtools$addInverseSpeed(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(ModAttributes.INVERSE_SPEED.get()), 0.0D);
        cir.setReturnValue(builder);
    }
}
