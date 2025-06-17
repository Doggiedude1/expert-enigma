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
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assigns a random mob's AI to every new mob that spawns.
 * The donor mob remains invisible and controls the host without riding it,
 * emulating passenger control without an actual passenger.
 */
@EventBusSubscriber(modid = BuildTools.MOD_ID)
public class RandomMobInheritEvents {
    private static final String SKIP_TAG = "dragonsbuildtools_skip_inherit";
    private static List<EntityType<? extends Mob>> MOB_TYPES;
    private static final Map<LivingEntity, Mob> CONTROLLERS = new HashMap<>();

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
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        Mob controller = CONTROLLERS.get(entity);
        if (controller == null) return;

        if (controller.isRemoved() || controller.level() != entity.level()) {
            CONTROLLERS.remove(entity);
            return;
        }

        entity.absMoveTo(controller.getX(), controller.getY(), controller.getZ(), controller.getYRot(), controller.getXRot());
        entity.setDeltaMovement(controller.getDeltaMovement());
        entity.setRemainingFireTicks(controller.getRemainingFireTicks());
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        Mob controller = CONTROLLERS.remove(entity);
        if (controller != null) controller.discard();
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        Mob controller = CONTROLLERS.remove(entity);
        if (controller != null) controller.discard();
    }

    public static LivingEntity getController(LivingEntity host) {
        return CONTROLLERS.get(host);
    }

    public static boolean hasController(LivingEntity host) {
        return CONTROLLERS.containsKey(host);
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
        donor.setInvisible(true);
        donor.setInvulnerable(true);
        donor.absMoveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
        event.getLevel().addFreshEntity(donor);

        copyAttributes(mob, donor);
        CONTROLLERS.put(mob, donor);
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
        var dstSet = (Iterable<Object>) goalsField.get(dst);
        ((java.util.Collection<?>) dstSet).clear();
        for (Object wrapped : srcSet) {
            Goal goal = (Goal) GoalSelector.class.getDeclaredField("goal").get(wrapped);
            retargetGoal(goal, newOwner, oldOwner);
            int priority = GoalSelector.class.getDeclaredField("priority").getInt(wrapped);
            Object newWrap = GoalSelector.class.getConstructor(int.class, Goal.class).newInstance(priority, goal);
            ((java.util.Collection<Object>) dstSet).add(newWrap);
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
                    f.set(goal, newOwner);
                }
            }
            cls = cls.getSuperclass();
        }
    }
}
