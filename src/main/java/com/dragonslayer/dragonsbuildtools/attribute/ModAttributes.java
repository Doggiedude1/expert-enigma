package com.dragonslayer.dragonsbuildtools.attribute;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers custom attributes for the mod. */
public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, BuildTools.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> INVERSE_SPEED = ATTRIBUTES.register(
            "inverse_speed",
            () -> new RangedAttribute("attribute.name." + BuildTools.MOD_ID + ".inverse_speed",
                    0.0D, -4.0D, 4.0D).setSyncable(true)
    );
}
