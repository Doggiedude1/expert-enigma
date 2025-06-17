package com.dragonslayer.dragonsbuildtools.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumSet;

public class GenericTakeBlockGoal extends Goal {
    private final Mob mob;

    public GenericTakeBlockGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!mob.getPersistentData().getBoolean("dragonsbuildtools_carryBlockLikeEnderman")) return false;
        return mob.getPersistentData().getString("dragonsbuildtools_carriedBlock").isEmpty();
    }

    @Override
    public void tick() {
        Level level = mob.level();
        BlockPos pos = mob.blockPosition().below();  // Example: look at block below
        BlockState state = level.getBlockState(pos);

        if (!state.isAir() && state.getBlock() != Blocks.BEDROCK) {
            mob.getPersistentData().putString(
                    "dragonsbuildtools_carriedBlock",
                    BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString()
            );
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            System.out.println("🧱 Took block: " + BuiltInRegistries.BLOCK.getKey(state.getBlock()));
        }
    }
}
