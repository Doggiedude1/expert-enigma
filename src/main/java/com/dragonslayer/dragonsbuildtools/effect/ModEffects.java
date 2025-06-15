package com.dragonslayer.dragonsbuildtools.effect;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers custom mob effects for the mod. */
public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, BuildTools.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> INVERSE_SPEED =
            EFFECTS.register("inverse_speed", InverseSpeedEffect::new);
}
