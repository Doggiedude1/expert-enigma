package com.dragonslayer.dragonsbuildtools.mixin;

import com.dragonslayer.dragonsbuildtools.accessor.ScaleAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityScaleMixin implements ScaleAccessor {
    @Unique
    private float dragonsbuildtools$scale = 1.0F;

    @ModifyReturnValue(method = "getScale", at = @At("RETURN"))
    private float dragonsbuildtools$modifyScale(float value) {
        return value * this.dragonsbuildtools$scale;
    }

    @Override
    public float dragonsbuildtools$getScale() {
        return dragonsbuildtools$scale;
    }

    @Override
    public void dragonsbuildtools$setScale(float scale) {
        this.dragonsbuildtools$scale = scale;
        ((Entity)(Object)this).refreshDimensions();
    }
}
