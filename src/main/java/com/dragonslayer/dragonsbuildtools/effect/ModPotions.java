package com.dragonslayer.dragonsbuildtools.effect;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import com.dragonslayer.dragonsbuildtools.effect.ModEffects;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers custom potions for the mod. */
public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, BuildTools.MOD_ID);

    public static final DeferredHolder<Potion, Potion> INVERSE_SPEED = POTIONS.register(
            "inverse_speed",
            () -> new Potion(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModEffects.INVERSE_SPEED.get()), 3600))
    );
}
