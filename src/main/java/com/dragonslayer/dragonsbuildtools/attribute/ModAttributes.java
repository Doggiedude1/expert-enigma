package com.dragonslayer.dragonsbuildtools.attribute;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.dragonslayer.dragonsbuildtools.BuildTools.MOD_ID;

/** Registers custom attributes for the mod. */
public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, BuildTools.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> INVERSE_SPEED = ATTRIBUTES.register(
            "inverse_speed", () -> (new PercentageAttribute("attribute.name.movement_speed", 0.7, (double)-1024F, (double)1024.0F, (double)1000.0F)).setSyncable(true));


}
