package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Randomly assigns the goals and attributes of another mob whenever a mob joins the world.
 */
@EventBusSubscriber(modid = BuildTools.MOD_ID)
public class RandomMobInheritEvents {
    private static final List<EntityType<? extends Mob>> MOB_TYPES;

    static {
        MOB_TYPES = BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(type -> type != EntityType.PLAYER)
                .filter(type -> {
                    try {
                        return type.create(null) instanceof Mob;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(type -> (EntityType<? extends Mob>) type)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (event.getLevel().isClientSide()) return;

        RandomSource random = mob.getRandom();
        EntityType<? extends Mob> randomType = MOB_TYPES.get(random.nextInt(MOB_TYPES.size()));
        Mob randomMob = randomType.create(event.getLevel());
        if (randomMob == null) return;

        copyAttributes(mob, randomMob);
        copyGoalSelector(mob.goalSelector, randomMob.goalSelector);
        copyGoalSelector(mob.targetSelector, randomMob.targetSelector);
    }

    private static void copyAttributes(Mob target, Mob source) {
        for (AttributeInstance src : source.getAttributes().getSyncableAttributes()) {
            Attribute attribute = src.getAttribute().value();
            AttributeInstance dst = target.getAttribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute));
            if (dst != null) {
                dst.setBaseValue(src.getBaseValue());
            }
        }
    }

    private static void copyGoalSelector(GoalSelector target, GoalSelector source) {
        Predicate<Goal> any = g -> true;
        target.removeAllGoals(any);
        for (WrappedGoal g : source.getAvailableGoals()) {
            Goal goal = g.getGoal();
            int priority = g.getPriority();
            target.addGoal(priority, goal);
        }
    }
}
