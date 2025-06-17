package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * Assigns a random mob's AI to every new mob that spawns.
 * A temporary donor mob is created to copy goals and attributes from and then
 * immediately discarded so the spawned mob inherits the donor's behaviour.
 */
@EventBusSubscriber(modid = BuildTools.MOD_ID)
public class RandomMobInheritEvents {
    private static final String SKIP_TAG = "dragonsbuildtools_skip_inherit";
    private static final String SPLIT_TAG = "dragonsbuildtools_slime_split";
    private static final String SCALE_TAG = "dragonsbuildtools_scale";
    private static List<EntityType<? extends Mob>> MOB_TYPES;

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

        if (type == EntityType.SLIME) {
            tag.putBoolean(SPLIT_TAG, true);
            tag.putFloat(SCALE_TAG, 1.0F);
        }

        donor.getPersistentData().putBoolean(SKIP_TAG, true);
        donor.setInvisible(true);
        donor.setInvulnerable(true);
        donor.absMoveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
        event.getLevel().addFreshEntity(donor);

        copyAttributes(mob, donor);
        try {
            copyGoals(mob, donor);
        } catch (Exception ignored) {
        }

        donor.discard();
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (mob.level().isClientSide()) return;

        CompoundTag tag = mob.getPersistentData();
        if (!tag.getBoolean(SPLIT_TAG)) return;

        float scale = tag.contains(SCALE_TAG) ? tag.getFloat(SCALE_TAG) : 1.0F;
        if (scale <= 0.25F) return;

        Level level = mob.level();
        for (int i = 0; i < 2; i++) {
            Mob child = mob.getType().create(level);
            if (child == null) continue;
            child.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
            child.getPersistentData().putBoolean(SPLIT_TAG, true);
            child.getPersistentData().putFloat(SCALE_TAG, scale / 2f);
            copyAttributes(child, mob);
            try {
                copyGoals(child, mob);
            } catch (Exception ignored) {
            }
            level.addFreshEntity(child);
            if (child instanceof LivingEntity) {
                ((com.dragonslayer.dragonsbuildtools.accessor.ScaleAccessor) child).dragonsbuildtools$setScale(scale / 2f);
            }
        }
    }

    private static void copyAttributes(Mob target, Mob source) {
        for (AttributeInstance src : source.getAttributes().getSyncableAttributes()) {
            var holder = src.getAttribute();
            AttributeInstance dst = target.getAttribute(holder);
            if (dst != null) {
                dst.setBaseValue(src.getBaseValue());
            }
        }
    }

    private static void wipeGoals(Mob mob) throws Exception {
        Field goalField = Mob.class.getDeclaredField("goalSelector");
        Field targetField = Mob.class.getDeclaredField("targetSelector");
        goalField.setAccessible(true);
        targetField.setAccessible(true);

        GoalSelector goals = (GoalSelector) goalField.get(mob);
        GoalSelector targets = (GoalSelector) targetField.get(mob);

        Field setField = GoalSelector.class.getDeclaredField("availableGoals");
        setField.setAccessible(true);
        ((java.util.Collection<?>) setField.get(goals)).clear();
        ((java.util.Collection<?>) setField.get(targets)).clear();
    }

    private static void copyGoals(Mob target, Mob source) throws Exception {
        Field goalField = Mob.class.getDeclaredField("goalSelector");
        Field targetField = Mob.class.getDeclaredField("targetSelector");
        goalField.setAccessible(true);
        targetField.setAccessible(true);

        GoalSelector targetGoals = (GoalSelector) goalField.get(target);
        GoalSelector targetTargets = (GoalSelector) targetField.get(target);
        GoalSelector sourceGoals = (GoalSelector) goalField.get(source);
        GoalSelector sourceTargets = (GoalSelector) targetField.get(source);

        wipeGoals(target);

        copyGoalSelector(targetGoals, sourceGoals, target, source);
        copyGoalSelector(targetTargets, sourceTargets, target, source);
    }

    private static void copyGoalSelector(GoalSelector dst, GoalSelector src, Mob newOwner, Mob oldOwner) throws Exception {
        Field setField = GoalSelector.class.getDeclaredField("availableGoals");
        setField.setAccessible(true);
        java.util.Collection<?> goals = (java.util.Collection<?>) setField.get(src);

        Class<?> wrappedGoalCls = Class.forName("net.minecraft.world.entity.ai.goal.WrappedGoal");
        Field priorityField = wrappedGoalCls.getDeclaredField("priority");
        Field goalField = wrappedGoalCls.getDeclaredField("goal");
        priorityField.setAccessible(true);
        goalField.setAccessible(true);

        for (Object wrapped : goals) {
            int priority = priorityField.getInt(wrapped);
            net.minecraft.world.entity.ai.goal.Goal g = (net.minecraft.world.entity.ai.goal.Goal) goalField.get(wrapped);
            try {
                retargetGoal(g, oldOwner, newOwner);
                dst.addGoal(priority, g);
            } catch (Exception ignored) {
                // skip incompatible goals
            }
        }
    }

    private static void setField(Object obj, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(obj, value);
    }

    private static void retargetGoal(Object goal, Mob oldOwner, Mob newOwner) throws IllegalAccessException {
        Class<?> cls = goal.getClass();
        while (cls != null) {
            for (Field f : cls.getDeclaredFields()) {
                if (Mob.class.isAssignableFrom(f.getType())) {
                    if (!f.getType().isInstance(newOwner)) {
                        continue;
                    }
                    f.setAccessible(true);
                    Object value = f.get(goal);
                    if (value == oldOwner) {
                        setField(goal, f, newOwner);
                    }
                }
            }
            cls = cls.getSuperclass();
        }
    }
}
