package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;
import java.util.ArrayList;
import java.util.List;

/**
 * Assigns a random mob's AI to every new mob that spawns.
 * A temporary donor mob supplies its goals and attributes to every spawned mob.
 * The donor is discarded immediately after copying data.
 */
@EventBusSubscriber(modid = BuildTools.MOD_ID)
public class RandomMobInheritEvents {
    private static final String SKIP_TAG = "dragonsbuildtools_skip_inherit";
    private static List<EntityType<? extends Mob>> MOB_TYPES;
    private static final Unsafe UNSAFE;

    static {
        Unsafe u = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            u = (Unsafe) f.get(null);
        } catch (Exception ignored) {
        }
        UNSAFE = u;
    }
    // No persistent controller map is needed now that the donor is discarded
    // immediately after copying its AI data.

    private static void initMobTypes(Level level) {
        if (MOB_TYPES != null && !MOB_TYPES.isEmpty()) return;
        List<EntityType<? extends Mob>> list = new ArrayList<>();
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            if (type == EntityType.PLAYER) continue;
            var entity = type.create(level);
            if (entity instanceof Mob) {
                @SuppressWarnings("unchecked")
                EntityType<? extends Mob> mobType = (EntityType<? extends Mob>) type;
                list.add(mobType);
            }
        }
        MOB_TYPES = list;
    }



    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (event.getLevel().isClientSide()) return;
        CompoundTag tag = mob.getPersistentData();
        if (tag.getBoolean(SKIP_TAG)) return;

        initMobTypes(event.getLevel());
        if (MOB_TYPES.isEmpty()) return;

        RandomSource random = mob.getRandom();
        EntityType<? extends Mob> type = MOB_TYPES.get(random.nextInt(MOB_TYPES.size()));
        Mob donor = type.create(event.getLevel());
        if (donor == null) return;

        donor.getPersistentData().putBoolean(SKIP_TAG, true);
        // Spawned solely for data transfer; it never enters the world
        donor.setInvisible(true);
        donor.setInvulnerable(true);
        donor.absMoveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());

        copyAttributes(mob, donor);
        copyGoals(mob, donor);
        donor.kill();
        donor.discard();
    }

    private static void copyAttributes(Mob target, Mob source) {
        for (AttributeInstance src : source.getAttributes().getSyncableAttributes()) {
            Attribute attr = src.getAttribute().value();
            AttributeInstance dst = target.getAttribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attr));
            if (dst != null) {
                dst.setBaseValue(src.getBaseValue());
            }
        }
    }

    private static void copyGoals(Mob target, Mob source) {
        try {
            Field goalField = Mob.class.getDeclaredField("goalSelector");
            Field targetField = Mob.class.getDeclaredField("targetSelector");
            goalField.setAccessible(true);
            targetField.setAccessible(true);

            GoalSelector srcGoals = (GoalSelector) goalField.get(source);
            GoalSelector srcTargets = (GoalSelector) targetField.get(source);
            GoalSelector dstGoals = (GoalSelector) goalField.get(target);
            GoalSelector dstTargets = (GoalSelector) targetField.get(target);

            copyGoalSelector(dstGoals, srcGoals, target, source);
            copyGoalSelector(dstTargets, srcTargets, target, source);
        } catch (Exception e) {
            // If reflection fails, fallback to wiping goals so at least the mob works
            e.printStackTrace();
        }
    }

    private static void copyGoalSelector(GoalSelector dst, GoalSelector src, Mob newOwner, Mob oldOwner) throws Exception {
        Field goalsField = GoalSelector.class.getDeclaredField("availableGoals");
        goalsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var srcSet = (Iterable<Object>) goalsField.get(src);
        @SuppressWarnings("unchecked")
        var dstSet = (java.util.Collection<?>) goalsField.get(dst);
        dstSet.clear();

        Method addGoal = GoalSelector.class.getDeclaredMethod("addGoal", int.class, Goal.class);
        addGoal.setAccessible(true);

        for (Object wrapped : srcSet) {
            Class<?> wrapCls = wrapped.getClass();
            Field goalField = wrapCls.getDeclaredField("goal");
            Field prioField = wrapCls.getDeclaredField("priority");
            goalField.setAccessible(true);
            prioField.setAccessible(true);
            Goal goal = (Goal) goalField.get(wrapped);
            retargetGoal(goal, newOwner, oldOwner);
            int priority = prioField.getInt(wrapped);
            addGoal.invoke(dst, priority, goal);
        }
    }

    private static void retargetGoal(Goal goal, Mob newOwner, Mob oldOwner) throws IllegalAccessException {
        Class<?> cls = goal.getClass();
        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                if (!Mob.class.isAssignableFrom(f.getType())) continue;
                f.setAccessible(true);
                Object value = f.get(goal);
                if (value == oldOwner) {
                    setFieldValue(f, goal, newOwner);
                }
            }
            cls = cls.getSuperclass();
        }
    }

    private static void setFieldValue(Field field, Object obj, Object value) throws IllegalAccessException {
        if (Modifier.isFinal(field.getModifiers()) && UNSAFE != null) {
            long offset = UNSAFE.objectFieldOffset(field);
            UNSAFE.putObject(obj, offset, value);
        } else {
            field.set(obj, value);
        }
    }
}