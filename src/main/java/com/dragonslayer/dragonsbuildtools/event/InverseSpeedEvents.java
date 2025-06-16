package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.attribute.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import com.dragonslayer.dragonsbuildtools.effect.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Objects;

/** Applies the inverse speed logic each tick. */
@EventBusSubscriber(modid = com.dragonslayer.dragonsbuildtools.BuildTools.MOD_ID)
public class InverseSpeedEvents {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        double modifier = entity.getAttributeValue(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(Attributes.MOVEMENT_SPEED.value()));
        if (entity.hasEffect(ModEffects.INVERSE_SPEED)) {
            var effectInstance = entity.getEffect(ModEffects.INVERSE_SPEED);
            if (effectInstance != null) {
                int amp = effectInstance.getAmplifier();
                Holder<net.minecraft.world.entity.ai.attributes.Attribute> attributeHolder;
                attributeHolder = Attributes.MOVEMENT_SPEED;
                Objects.requireNonNull(entity.getAttribute(attributeHolder)).addTransientModifier(new AttributeModifier(ResourceLocation.parse("generic.movement_speed"), -(1.0D - 0.2D * amp), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                // Example debug
                System.out.println("Inverse Speed amplifier: " + amp);
            }
        }
    }
}
