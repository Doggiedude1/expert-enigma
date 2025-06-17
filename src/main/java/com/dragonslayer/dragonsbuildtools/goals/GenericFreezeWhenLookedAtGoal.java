package com.dragonslayer.dragonsbuildtools.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GenericFreezeWhenLookedAtGoal extends Goal {
    private final Mob mob;
    private Player observedPlayer;

    public GenericFreezeWhenLookedAtGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!mob.getPersistentData().getBoolean("dragonsbuildtools_freezeWhenLookedAt")) return false;

        for (Player player : mob.level().getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(16.0))) {
            if (player.hasLineOfSight(mob) && isPlayerLookingAtMob(player, mob)) {
                observedPlayer = player;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        if (observedPlayer != null) {
            mob.setTarget(observedPlayer);
        }
    }

    @Override
    public void tick() {
        if (observedPlayer != null) {
            mob.getLookControl().setLookAt(observedPlayer, 10.0F, mob.getMaxHeadXRot());
            mob.setTarget(observedPlayer);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return observedPlayer != null &&
                observedPlayer.isAlive() &&
                mob.distanceToSqr(observedPlayer) < 256.0 &&
                isPlayerLookingAtMob(observedPlayer, mob);
    }

    @Override
    public void stop() {
        observedPlayer = null;
    }

    private boolean isPlayerLookingAtMob(Player player, Mob mob) {
        Vec3 playerLook = player.getViewVector(1.0F).normalize();
        Vec3 toMob = mob.position().subtract(player.position()).normalize();
        double dot = playerLook.dot(toMob);
        return dot > 0.95;  // Adjust threshold to control sensitivity
    }
}
