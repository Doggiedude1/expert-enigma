package com.dragonslayer.dragonsbuildtools.network;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ScaleUpdatePayload(int entityId, float scale) implements CustomPacketPayload {
    public static final Type<ScaleUpdatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BuildTools.MOD_ID, "scale_update"));
    public static final StreamCodec<FriendlyByteBuf, ScaleUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ScaleUpdatePayload::entityId,
            ByteBufCodecs.FLOAT, ScaleUpdatePayload::scale,
            ScaleUpdatePayload::new
    );

    @Override
    public Type<ScaleUpdatePayload> type() {
        return TYPE;
    }
}
