package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.attribute.ModAttributes;
import com.dragonslayer.dragonsbuildtools.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TickEvent;

/** Applies the inverse speed logic each tick. */
@EventBusSubscriber(modid = com.dragonslayer.dragonsbuildtools.BuildTools.MOD_ID,
        bus = EventBusSubscriber.Bus.FORGE)
public class InverseSpeedEvents {
    @SubscribeEvent
    public static void onLivingTick(TickEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (event.phase == TickEvent.Phase.END) {
            double modifier = entity.getAttributeValue(ModAttributes.INVERSE_SPEED.get());
            if (modifier != 0.0D) {
                Vec3 delta = entity.getDeltaMovement();
                double horizontalSpeed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
                if (horizontalSpeed > 0.0D) {
                    Vec3 dir = new Vec3(delta.x, 0.0D, delta.z).normalize();
                    double newSpeed = horizontalSpeed * Math.abs(modifier);
                    if (modifier < 0) {
                        entity.setDeltaMovement(-dir.x * newSpeed, delta.y, -dir.z * newSpeed);
                    } else {
                        entity.setDeltaMovement(dir.x * newSpeed, delta.y, dir.z * newSpeed);
                    }
                }
=======
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "dragonsbuildtools")
public class InverseSpeedEvents {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            MobEffectInstance inst = entity.getEffect(ModEffects.INVERSE_SPEED);
            if (inst == null) return;

            int amplifier = inst.getAmplifier();
            Vec3 delta = entity.getDeltaMovement();
            double horizontalSpeed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
            if (horizontalSpeed > 0.0D) {
                Vec3 dir = new Vec3(delta.x, 0.0D, delta.z).normalize();
                double newSpeed = horizontalSpeed * (amplifier + 1);
                entity.setDeltaMovement(-dir.x * newSpeed, delta.y, -dir.z * newSpeed);
            }
        }
    }
}
