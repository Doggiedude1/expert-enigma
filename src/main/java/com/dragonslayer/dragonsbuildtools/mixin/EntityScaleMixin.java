package com.dragonslayer.dragonsbuildtools.mixin;

import com.dragonslayer.dragonsbuildtools.accessor.ScaleAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Mob.class)
public abstract class EntityScaleMixin implements ScaleAccessor {
    @Unique
    public float dragonsbuildtools_scale = 1.0F;

    @Override
    public float dragonsbuildtools$getScale() {
        System.out.println("Got Scale Mixin scale: " + dragonsbuildtools_scale);
        return dragonsbuildtools_scale;
    }

    @Override
    public void dragonsbuildtools$setScale(float scale) {
        this.dragonsbuildtools_scale = scale;
        System.out.println("Set Scale Mixin scale to: " + scale);
        ((LivingEntity)(Object)this).refreshDimensions();
    }
}
