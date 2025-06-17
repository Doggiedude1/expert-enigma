package com.dragonslayer.dragonsbuildtools.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Mob.class)
public abstract class MobBehaviorMixin {

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void copyEnvironmentalBehaviors(CallbackInfo ci) {
        Mob mob = (Mob)(Object)this;
        Level level = mob.level();

        // Example: Apply zombie burning logic
        if (shouldBurnInSunlight(mob)) {
            if (level.isDay() && !level.isClientSide() && level.canSeeSky(mob.blockPosition())) {
                if (!mob.isOnFire()) {
                    mob.setRemainingFireTicks(8 * 20);
                }
            }
        }
        else
        {
            if (mob.isOnFire()) {
                mob.clearFire();
            }

            // Optionally reset fire ticks if your mobs somehow get set on fire
            mob.setRemainingFireTicks(0);
        }
        if (mob.getPersistentData().getBoolean("dragonsbuildtools_needsWaterToBreathe")) {
            if (mob.isInWaterRainOrBubble()) {
                // They can breathe in water/rain/bubble
                mob.setAirSupply(mob.getMaxAirSupply());
            } else {
                // Outside water — they start to drown
                mob.setAirSupply(mob.getAirSupply() - 1);
                if (mob.getAirSupply() <= -20) {
                    mob.setAirSupply(0);
                    mob.hurt(mob.damageSources().drown(), 2.0F);
                }
            }
        }
        else {
            if (mob.isInWaterRainOrBubble()) {
                // They can breathe in water/rain/bubble
                // Outside water — they start to drown
                mob.setAirSupply(mob.getAirSupply() - 1);
                if (mob.getAirSupply() <= -20) {
                    mob.setAirSupply(0);
                    mob.hurt(mob.damageSources().drown(), 2.0F);
                }
            } else {
                mob.setAirSupply(mob.getMaxAirSupply());
            }
        }
        if (canTeleportLikeShulker(mob)) {
            // Example simple teleport logic: random small position offset
            if (mob.level().random.nextInt(200) == 0) { // once every ~10 seconds
                double dx = mob.getX() + (mob.level().random.nextDouble() - 0.5) * 4;
                double dy = mob.getY() + (mob.level().random.nextDouble() - 0.5) * 4;
                double dz = mob.getZ() + (mob.level().random.nextDouble() - 0.5) * 4;
                mob.teleportTo(dx, dy, dz);
                System.out.println("✨ Shulker-style teleport");
            }
        }

        if (canTeleportLikeEnderman(mob)) {
            if (mob.isInWaterRainOrBubble()) {
                mob.hurt(mob.damageSources().drown(), 1.0F);
                double dx = mob.getX() + (mob.getRandom().nextDouble() - 0.5) * 16.0;
                double dy = mob.getY() + mob.getRandom().nextInt(16) - 8;
                double dz = mob.getZ() + (mob.getRandom().nextDouble() - 0.5) * 16.0;
                teleport(mob, dx, dy, dz);
            }

            // Enderman-style teleport: teleport when hurt (simplified)
            if (mob.hurtTime > 0 && mob.hurtTime < 5 && mob.level().random.nextInt(4) == 0) {
                double dx = mob.getX() + (mob.level().random.nextDouble() - (double)0.5F) * 64.0F;
                double dy = mob.getY() + (mob.level().random.nextInt(64) - 32);
                double dz = mob.getZ() + (mob.level().random.nextDouble() - (double)0.5F) * 64.0F;
                teleport(mob, dx, dy, dz);
            }

            if (mob.getTarget() != null && mob.level().random.nextInt(40) == 0) {
                LivingEntity target = mob.getTarget();
                double dx = target.getX() + (mob.getRandom().nextDouble() - 0.5) * 8.0;
                double dy = target.getY();
                double dz = target.getZ() + (mob.getRandom().nextDouble() - 0.5) * 8.0;
                teleport(mob, dx, dy, dz);
            }
        }
        if (mob.getPersistentData().getBoolean("dragonsbuildtools_carryBlockLikeEnderman")) {
            if (mob.getPersistentData().getString("dragonsbuildtools_carriedBlock").isEmpty()) {
                // Assign random block (or pick up nearby block)
                BlockState randomBlock = Blocks.DIRT.defaultBlockState();  // Replace with logic for random block
                mob.getPersistentData().putString("dragonsbuildtools_carriedBlock",
                        BuiltInRegistries.BLOCK.getKey(randomBlock.getBlock()).toString());
                System.out.println("🧱 " + mob.getName().getString() + " picked up: " + randomBlock);
            }
        }

        if (canClimbWalls(mob)) {
            if (mob.horizontalCollision) {
                mob.setDeltaMovement(mob.getDeltaMovement().x, 0.2, mob.getDeltaMovement().z);
            }
        }
        if (canClimbWalls(mob) && level.isDay() && mob.getTarget() instanceof Player) {
            mob.setTarget(null);
        }
    }

    @Inject(method = "doHurtTarget", at = @At("HEAD"))
    private void dragonsbuildtools$applyHungerEffect(net.minecraft.world.entity.Entity target, CallbackInfoReturnable<Boolean> cir) {
        Mob mob = (Mob)(Object)this;
        if (shouldInflictHunger(mob) && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200));
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void dragonsbuildtools$projectileTeleport(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Mob mob = (Mob)(Object)this;
        if (canTeleportLikeEnderman(mob) && source.is(DamageTypeTags.IS_PROJECTILE)) {
            double dx = mob.getX() + (mob.getRandom().nextDouble() - 0.5) * 16.0;
            double dy = mob.getY() + mob.getRandom().nextInt(16) - 8;
            double dz = mob.getZ() + (mob.getRandom().nextDouble() - 0.5) * 16.0;
            if (teleport(mob, dx, dy, dz)) {
                cir.setReturnValue(false);
            }
        }
    }

    private boolean teleport(LivingEntity mob, double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while(blockpos$mutableblockpos.getY() > mob.level().getMinBuildHeight() && !mob.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = mob.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            EntityTeleportEvent.EnderEntity event = EventHooks.onEnderTeleport(mob, x, y, z);
            if (event.isCanceled()) {
                return false;
            } else {
                Vec3 vec3 = mob.position();
                boolean flag2 = mob.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
                if (flag2) {
                    mob.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(mob));
                    if (!mob.isSilent()) {
                        mob.level().playSound((Player)null, mob.xo, mob.yo, mob.zo, SoundEvents.ENDERMAN_TELEPORT, mob.getSoundSource(), 1.0F, 1.0F);
                        mob.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    }
                }

                return flag2;
            }
        } else {
            return false;
        }
    }
    private static final List<Block> VALID_CARRIABLE_BLOCKS = BuiltInRegistries.BLOCK.stream()
            .filter(block -> block != Blocks.AIR)
            .filter(block -> block != Blocks.BEDROCK)
            .filter(block -> block != Blocks.BARRIER)
            .filter(block -> block != Blocks.STRUCTURE_BLOCK)
            .filter(block -> block != Blocks.COMMAND_BLOCK)
            // Add any other exclusions as needed
            .toList();

    private boolean shouldBurnInSunlight(Mob mob) {
        return mob.getPersistentData().getBoolean("dragonsbuildtools_burnInSunlight");
    }

    private boolean shouldNeedWaterToBreathe(Mob mob) {
        return mob.getPersistentData().getBoolean("dragonsbuildtools_needsWaterToBreathe");
    }

    private boolean shouldInflictHunger(Mob mob) {
        return mob.getPersistentData().getBoolean("dragonsbuildtools_inflictsHunger");
    }

    private boolean canTeleportLikeEnderman(Mob mob){
        return mob.getPersistentData().getBoolean("dragonsbuildtools_teleportLikeEnderman");
    }

    private String carriedBlock(Mob mob){
            return mob.getPersistentData().getString("dragonsbuildtools_carriedBlock");
    }

    private boolean canTeleportLikeShulker(Mob mob){
        return mob.getPersistentData().getBoolean("dragonsbuildtools_teleportLikeShulker");
    }
    private boolean shootShulkerBullets(Mob mob){
        return mob.getPersistentData().getBoolean("dragonsbuildtools_shootShulkerBullets");
    }

    private boolean canShootArrows(Mob mob){
        return mob.getPersistentData().getBoolean("dragonsbuildtools_shootArrowsLikeSkeleton");
    }

    private boolean canClimbWalls(Mob mob){
        return mob.getPersistentData().getBoolean("dragonsbuildtools_climbWallsLikeSpider");
    }

}
