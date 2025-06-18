package com.dragonslayer.dragonsbuildtools.network;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import com.dragonslayer.dragonsbuildtools.accessor.ScaleAccessor;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = BuildTools.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ScaleUpdatePayload.TYPE, ScaleUpdatePayload.STREAM_CODEC, NetworkHandler::handleScaleUpdate);
    }

    private static void handleScaleUpdate(ScaleUpdatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(payload.entityId());
            if (entity != null) {
                ((ScaleAccessor) entity).dragonsbuildtools$setScale(payload.scale());

                entity.refreshDimensions();
            }
        });
    }

    public static void sendScaleUpdate(Entity entity, float scale) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new ScaleUpdatePayload(entity.getId(), scale));
    }
}
