package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import com.dragonslayer.dragonsbuildtools.attribute.ModAttributes;
import com.mojang.datafixers.kinds.IdF;
import net.minecraft.core.registries.BuiltInRegistries;
import com.dragonslayer.dragonsbuildtools.effect.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Objects;
import java.util.UUID;

/** Applies the inverse speed logic each tick. */
@EventBusSubscriber(modid = BuildTools.MOD_ID)
public class InverseSpeedEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        ResourceLocation MULT_ID = ResourceLocation.fromNamespaceAndPath(BuildTools.MOD_ID,"inverse_speed_mult");
        ResourceLocation FLAT_ID = ResourceLocation.fromNamespaceAndPath(BuildTools.MOD_ID, "inverse_speed_flat");
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        double defaultValue = entity.getAttributeValue(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(Attributes.MOVEMENT_SPEED.value()));
        if (entity.hasEffect(ModEffects.INVERSE_SPEED)) {
            var effectInstance = entity.getEffect(ModEffects.INVERSE_SPEED);
            if (effectInstance != null) {
                int amp = effectInstance.getAmplifier();
                var attrInstance = (entity.getAttribute(Attributes.MOVEMENT_SPEED));
                if (attrInstance != null) {
                    float inc = -0.1f;
                    float speedAmp = (float) (inc * amp);
                    float invertAmount = (float )(attrInstance.getBaseValue() * -2f);
                    if (attrInstance.hasModifier(MULT_ID)) {
                        attrInstance.removeModifier(MULT_ID);
                    }
                    if (attrInstance.hasModifier(FLAT_ID)) {
                        attrInstance.removeModifier(FLAT_ID);
                    }
                    AttributeModifier speedModifier = new AttributeModifier(
                            MULT_ID,
                            speedAmp,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    );
                    AttributeModifier speedInvert = new AttributeModifier(
                            FLAT_ID,
                            invertAmount,
                            AttributeModifier.Operation.ADD_VALUE
                    );
                    attrInstance.addTransientModifier(speedModifier);
                    attrInstance.addTransientModifier(speedInvert);
                    System.out.println("Amplifier: " + amp + ", Speed amplifier: " + speedAmp);
                }
            }
        }else{
            // Remove modifier if no longer affected
            var attrInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attrInstance != null && attrInstance.hasModifier(MULT_ID)) {
                attrInstance.removeModifier(MULT_ID);
            }
            if (attrInstance != null && attrInstance.hasModifier(FLAT_ID)) {
                attrInstance.removeModifier(FLAT_ID);
            }
        }

    }
}