package com.dragonslayer.dragonsbuildtools.api.event;

import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class MobSplitAbilityEvent extends Event {
    private final Mob child;

    @ApiStatus.Internal
    public MobSplitAbilityEvent(Mob child) {
        this.child = child;
    }

    public Mob getChild() {
        return this.child;
    }

}
