package com.dragonslayer.dragonsbuildtools.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class GenericLeaveBlockGoal extends Goal {
    private final Mob mob;

    public GenericLeaveBlockGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!mob.getPersistentData().getBoolean("dragonsbuildtools_carryBlockLikeEnderman")) return false;
        return !mob.getPersistentData().getString("dragonsbuildtools_carriedBlock").isEmpty();
    }

    @Override
    public void tick() {
        Level level = mob.level();
        BlockPos pos = mob.blockPosition().below();

        if (level.getBlockState(pos).isAir()) {
            String blockId = mob.getPersistentData().getString("dragonsbuildtools_carriedBlock");
            if (!blockId.isEmpty()) {
                BlockState state = BuiltInRegistries.BLOCK
                        .getOptional(ResourceLocation.parse(blockId))
                        .orElse(Blocks.DIRT)  // Fallback
                        .defaultBlockState();

                level.setBlock(pos, state, 3);
                mob.getPersistentData().putString("dragonsbuildtools_carriedBlock", "");
                System.out.println("🧱 Placed block: " + blockId);
            }
        }
    }
}

