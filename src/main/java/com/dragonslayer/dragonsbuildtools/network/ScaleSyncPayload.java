package com.dragonslayer.dragonsbuildtools.network;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ScaleSyncPayload(int entityId, float scale) implements CustomPacketPayload {
    public static final Type<ScaleSyncPayload> TYPE = new Type<>(ResourceLocation.parse(BuildTools.MOD_ID + ":scale_sync"));
    public static final StreamCodec<FriendlyByteBuf, ScaleSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ScaleSyncPayload::entityId,
            ByteBufCodecs.FLOAT,
            ScaleSyncPayload::scale,
            ScaleSyncPayload::new);

    @Override
    public Type<ScaleSyncPayload> type() {
        return TYPE;
    }
}
