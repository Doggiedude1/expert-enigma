package com.dragonslayer.dragonsbuildtools.network;

import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.PacketDistributor;

public class BuildToolsNetwork {
    public static void sendScaleSync(Mob mob, float scale) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, new ScaleSyncPayload(mob.getId(), scale));
    }
}
