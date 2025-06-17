package com.dragonslayer.dragonsbuildtools.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import java.util.EnumSet;

public class GenericShulkerBulletGoal extends Goal {
    private final Mob mob;
    private int attackCooldown;

    public GenericShulkerBulletGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.TARGET, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!mob.getPersistentData().getBoolean("dragonsbuildtools_shootShulkerBullets")) return false;
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        mob.getLookControl().setLookAt(target, 10.0F, mob.getMaxHeadXRot());

        if (attackCooldown <= 0) {
            shootBullet(target);
            attackCooldown = 40 + mob.getRandom().nextInt(20);  // Cooldown 2-3 seconds
        } else {
            attackCooldown--;
        }
    }

    private void shootBullet(LivingEntity target) {
        Level level = mob.level();
        ShulkerBullet bullet = new ShulkerBullet(level, mob, target, mob.getDirection().getAxis());
        bullet.setPos(
                mob.getX(), mob.getEyeY() - 0.5, mob.getZ()
        );
        level.addFreshEntity(bullet);
        System.out.println("💥 " + mob.getName().getString() + " fired a Shulker Bullet at " + target.getName().getString());
    }
}
