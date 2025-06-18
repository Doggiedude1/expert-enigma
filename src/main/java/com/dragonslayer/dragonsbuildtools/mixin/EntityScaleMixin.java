package com.dragonslayer.dragonsbuildtools.mixin;

import com.dragonslayer.dragonsbuildtools.accessor.ScaleAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)  // You could target LivingEntity if you want to cover more types
public abstract class EntityScaleMixin implements ScaleAccessor {

    @Unique
    private float dragonsbuildtools$scale = 1.0F;

    @Override
    public float dragonsbuildtools$getScale() {
        return dragonsbuildtools$scale;
    }

    @Override
    public void dragonsbuildtools$setScale(float scale) {
        this.dragonsbuildtools$scale = scale;
        System.out.println("🔹 [Mixin] Scale set to: " + scale + " for entity: " + ((Entity)(Object)this).getId());
        ((Entity)(Object)this).refreshDimensions();
    }

    @ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
    private EntityDimensions dragonsbuildtools$modifyDimensions(EntityDimensions original) {
        return original.scale(this.dragonsbuildtools$scale);
    }
}
