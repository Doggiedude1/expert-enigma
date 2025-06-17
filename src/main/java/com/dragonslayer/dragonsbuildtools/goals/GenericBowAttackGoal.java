package com.dragonslayer.dragonsbuildtools.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class GenericBowAttackGoal extends Goal {
    private final PathfinderMob mob;
    private int attackCooldown;

    public GenericBowAttackGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.TARGET, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!mob.getPersistentData().getBoolean("dragonsbuildtools_shootArrowsLikeSkeleton")) return false;
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        attackCooldown = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (attackCooldown-- <= 0) {
            shootArrow(target);
            attackCooldown = 20;
        }
    }

    private void shootArrow(LivingEntity target) {
        Level level = mob.level();
        Arrow arrow = new Arrow(net.minecraft.world.entity.EntityType.ARROW, level);
        arrow.setPos(mob.getX(), mob.getEyeY(), mob.getZ());
        arrow.setOwner(mob);
        double dx = target.getX() - mob.getX();
        double dy = target.getY(0.333F) - arrow.getY();
        double dz = target.getZ() - mob.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dist * 0.2, dz, 1.6F, 0);
        level.addFreshEntity(arrow);
    }
}
