package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.nbt.CompoundTag;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Randomly assigns the goals and attributes of another mob whenever a mob joins the world.
 */
@EventBusSubscriber(modid = BuildTools.MOD_ID)
public class RandomMobInheritEvents {
    private static List<EntityType<? extends Mob>> MOB_TYPES;

    private static final String SKIP_TAG = "dragonsbuildtools_skip_inherit";


    private static void initMobTypes(Level level) {
        if (MOB_TYPES != null) return;
        List<EntityType<? extends Mob>> list = new ArrayList<>();
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            if (type == EntityType.PLAYER) continue;
            var entity = type.create(level);
            if (entity instanceof Mob m) {
                list.add((EntityType<? extends Mob>) m.getType());
            }
        }
        MOB_TYPES = list;
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (event.getLevel().isClientSide()) return;
        if (mob.getPersistentData().getBoolean(SKIP_TAG)) return;

        initMobTypes(event.getLevel());

        RandomSource random = mob.getRandom();
        EntityType<? extends Mob> randomType = MOB_TYPES.get(random.nextInt(MOB_TYPES.size()));
        Mob randomMob = randomType.create(event.getLevel());
        if (randomMob == null) return;

        randomMob.setInvisible(true);
        randomMob.setInvulnerable(true);
        randomMob.setNoGravity(true);
        randomMob.setSilent(true);
        randomMob.moveTo(mob.position());
        randomMob.getPersistentData().putBoolean(SKIP_TAG, true);
        event.getLevel().addFreshEntity(randomMob);

        copyAttributes(mob, randomMob);
        copyGoalSelector(mob, randomMob.goalSelector, randomMob);
        copyGoalSelector(mob, randomMob.targetSelector, randomMob);

        randomMob.discard();
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

    private static void copyGoalSelector(Mob newOwner, GoalSelector sourceSelector, Mob oldOwner) {
        Predicate<Goal> any = g -> true;
        GoalSelector target = sourceSelector == oldOwner.goalSelector ? newOwner.goalSelector : newOwner.targetSelector;
        target.removeAllGoals(any);
        for (WrappedGoal g : sourceSelector.getAvailableGoals()) {
            Goal goal = g.getGoal();
            retargetGoal(goal, oldOwner, newOwner);
            int priority = g.getPriority();
            target.addGoal(priority, goal);
        }
    }

    private static void retargetGoal(Goal goal, Mob oldOwner, Mob newOwner) {
        Class<?> cls = goal.getClass();
        while (cls != null) {
            for (Field f : cls.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                Class<?> type = f.getType();
                if (!Mob.class.isAssignableFrom(type)) continue;
                f.setAccessible(true);
                try {
                    Object value = f.get(goal);
                    if (value == oldOwner) {
                        if (Modifier.isFinal(f.getModifiers())) {
                            Field mods = Field.class.getDeclaredField("modifiers");
                            mods.setAccessible(true);
                            mods.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                        }
                        f.set(goal, newOwner);
                    }
                } catch (Exception ignored) {
                }
            }
            cls = cls.getSuperclass();
        }
    }
}