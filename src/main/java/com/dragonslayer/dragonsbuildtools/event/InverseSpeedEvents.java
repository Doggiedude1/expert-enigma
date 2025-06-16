package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.attribute.ModAttributes;
import com.dragonslayer.dragonsbuildtools.effect.ModEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Handles events related to the inverse speed effect. */
public class InverseSpeedEvents {
    /** Inject our custom attribute into all living entities. */
    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(ModAttributes.INVERSE_SPEED.get()));
        }
    }

    /** Each tick, flip horizontal movement if the inverse speed effect is active. */
    @SubscribeEvent
    public static void onLivingTick(PlayerTickEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModEffects.INVERSE_SPEED.get()))) {
            return;
        }
        int amplifier = entity.getEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModEffects.INVERSE_SPEED.get())).getAmplifier();
        double scale = 1.0 + 0.2D * (amplifier + 1);
        Vec3 delta = entity.getDeltaMovement();
        entity.setDeltaMovement(-delta.x * scale, delta.y, -delta.z * scale);
    }
}
