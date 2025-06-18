package com.dragonslayer.dragonsbuildtools.event;

import com.dragonslayer.dragonsbuildtools.BuildTools;
import com.dragonslayer.dragonsbuildtools.api.event.MobSplitAbilityEvent;
import com.dragonslayer.dragonsbuildtools.goals.GenericFreezeWhenLookedAtGoal;
import com.dragonslayer.dragonsbuildtools.goals.GenericLeaveBlockGoal;
import com.dragonslayer.dragonsbuildtools.goals.GenericShulkerBulletGoal;
import com.dragonslayer.dragonsbuildtools.goals.GenericTakeBlockGoal;
import com.dragonslayer.dragonsbuildtools.goals.GenericBowAttackGoal;
import com.dragonslayer.dragonsbuildtools.network.NetworkHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

@EventBusSubscriber(modid = BuildTools.MOD_ID)
public class RandomMobInheritEvents {
    private static final Map<EntityType<?>, Set<String>> ABILITY_MAP = new HashMap<>();
    private static final Map<EntityType<?>, GoalBuilder> GOAL_MAP = new HashMap<>();
    static {
        GOAL_MAP.put(EntityType.ZOMBIE, (mob) -> {
            mob.goalSelector.addGoal(1, new FloatGoal(mob));
            mob.goalSelector.addGoal(2, new MeleeAttackGoal((PathfinderMob) mob, 1.2, false));
            mob.goalSelector.addGoal(3, new RandomStrollGoal((PathfinderMob) mob, 1.0));
            mob.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) mob));
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, net.minecraft.world.entity.player.Player.class, true));
        });

        GOAL_MAP.put(EntityType.HUSK, GOAL_MAP.get(EntityType.ZOMBIE));

        GOAL_MAP.put(EntityType.DROWNED, (mob) -> {
            mob.goalSelector.addGoal(1, new FloatGoal(mob));
            mob.goalSelector.addGoal(2, new MeleeAttackGoal((PathfinderMob) mob, 1.0, false));
            mob.goalSelector.addGoal(3, new RandomStrollGoal((PathfinderMob) mob, 1.0));
            mob.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) mob));
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, Player.class, true));
        });

        GOAL_MAP.put(EntityType.ENDERMAN, (mob) -> {
            mob.goalSelector.addGoal(0, new FloatGoal(mob));
            mob.goalSelector.addGoal(1, new GenericFreezeWhenLookedAtGoal(mob));
            mob.goalSelector.addGoal(2, new MeleeAttackGoal((PathfinderMob) mob, 1.0, false));
            mob.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal((PathfinderMob) mob, 1.0));
            mob.goalSelector.addGoal(8, new LookAtPlayerGoal(mob, Player.class, 8.0F));
            mob.goalSelector.addGoal(8, new RandomLookAroundGoal(mob));
            mob.goalSelector.addGoal(10, new GenericLeaveBlockGoal(mob));
            mob.goalSelector.addGoal(11, new GenericTakeBlockGoal(mob));
            mob.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) mob));
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, Endermite.class, true, false));
        });

        GOAL_MAP.put(EntityType.SHULKER, (mob) -> {
            mob.goalSelector.addGoal(1, new LookAtPlayerGoal(mob, Player.class, 8.0F, 0.02F, true));
            mob.goalSelector.addGoal(4, new GenericShulkerBulletGoal(mob));
            mob.goalSelector.addGoal(5, new LookAtPlayerGoal(mob, Player.class, 8.0F));
            mob.goalSelector.addGoal(6, new RandomLookAroundGoal(mob));
            mob.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) mob));
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, Player.class, true));
        });

        GOAL_MAP.put(EntityType.SKELETON, (mob) -> {
            mob.goalSelector.addGoal(1, new FloatGoal(mob));
            mob.goalSelector.addGoal(2, new GenericBowAttackGoal((PathfinderMob) mob));
            mob.goalSelector.addGoal(3, new RandomStrollGoal((PathfinderMob) mob, 1.0));
            mob.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) mob));
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, Player.class, true));
        });

        GOAL_MAP.put(EntityType.SPIDER, (mob) -> {
            mob.goalSelector.addGoal(1, new FloatGoal(mob));
            mob.goalSelector.addGoal(2, new MeleeAttackGoal((PathfinderMob) mob, 1.0, false));
            mob.goalSelector.addGoal(3, new RandomStrollGoal((PathfinderMob) mob, 1.0));
            mob.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) mob));
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                    mob,
                    Player.class,
                    10,
                    true,
                    false,
                    living -> !mob.level().isDay()
            ));
        });

        // Add other entity types...
    }
    static {
        addAbility(EntityType.ZOMBIE, new String[]{"dragonsbuildtools_burnInSunlight"});
        addAbility(EntityType.DROWNED, new String[]{"dragonsbuildtools_burnInSunlight","dragonsbuildtools_needsWaterToBreathe"});
        addAbility(EntityType.HUSK, new String[]{"dragonsbuildtools_inflictsHunger"});
        addAbility(EntityType.ENDERMAN, new String[]{"dragonsbuildtools_teleportLikeEnderman","dragonsbuildtools_freezeWhenLookedAt","dragonsbuildtools_carryBlockLikeEnderman"});
        addAbility(EntityType.SHULKER, new String[]{"dragonsbuildtools_teleportLikeShulker", "dragonsbuildtools_shootShulkerBullets"});
        addAbility(EntityType.SKELETON, new String[]{"dragonsbuildtools_burnInSunlight","dragonsbuildtools_shootArrowsLikeSkeleton"});
        addAbility(EntityType.SPIDER, new String[]{"dragonsbuildtools_climbWallsLikeSpider"});
        addAbility(EntityType.SLIME, new String[]{"dragonsbuildtools_slime_split"});
        // Extend for more entities as you see fit!
    }
    private static void addAbility(EntityType<?> type, String[] abilities) {
        for(String ability : abilities)
            ABILITY_MAP.computeIfAbsent(type, t -> new HashSet<>()).add(ability);
    }
    @FunctionalInterface
    private interface GoalBuilder {
        void apply(Mob mob);
    }
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof PathfinderMob mob)) return;
        if (!(event.getEntity() instanceof Mob realMob)) return;
        if(realMob.getPersistentData().getBoolean("dragonsbuildtools_slime_split") && !realMob.getPersistentData().getBoolean("dragonsbuildtools_slime_skip_split")) {
            return;
        }
        if (realMob.getPersistentData().getBoolean("dragonsbuildtools_slime_skip_split")) {
            realMob.getPersistentData().putBoolean("dragonsbuildtools_slime_skip_split", false);
            realMob.getPersistentData().putBoolean("dragonsbuildtools_slime_split", false);
            return;
        }
        if (event.getLevel().isClientSide()) return;
        try {
            wipeGoals(mob);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Clear all ability flags
        mob.getPersistentData().putBoolean("dragonsbuildtools_burnInSunlight", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_needsWaterToBreathe", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_inflictsHunger", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_teleportLikeEnderman", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_teleportLikeShulker", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_freezeWhenLookedAt", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_carryBlockLikeEnderman", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_shootShulkerBullets", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_shootArrowsLikeSkeleton", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_climbWallsLikeSpider", false);
        mob.getPersistentData().putBoolean("dragonsbuildtools_slime_split", false);
        mob.getPersistentData().putFloat("dragonsbuildtools_scale", 1f);
        NetworkHandler.sendScaleUpdate(mob, 1f);
        mob.refreshDimensions();
        // Randomly pick a source type from the ability map
        EntityType<?>[] sourceTypes = ABILITY_MAP.keySet().toArray(new EntityType[0]); //May have hostile abilites while a passive ai
        EntityType<?> sourceType = sourceTypes[mob.getRandom().nextInt(sourceTypes.length)];
        System.out.println("🎲 Chose source type: " + sourceType.toShortString());
        GoalBuilder builder = GOAL_MAP.get(sourceType);
        if (builder != null) {
            builder.apply(mob);
            System.out.println("⚡ Applied goals for: " + sourceType.toShortString());
        }
        // Apply abilities
        Set<String> abilities = ABILITY_MAP.get(sourceType);
        if (abilities != null) {
            for (String ability : abilities) {
                mob.getPersistentData().putBoolean(ability, true);
                System.out.println("✅ Applied ability: " + ability);
            }
            if (abilities.contains("dragonsbuildtools_slime_split")) {
                mob.getPersistentData().putFloat("dragonsbuildtools_scale", 1.0F);
                NetworkHandler.sendScaleUpdate(mob, 1.0F);
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
    @SubscribeEvent
    public static void OnEntityDeath(LivingDeathEvent event){
        // Drop carried block
        LivingEntity entity = event.getEntity();
        String blockId = entity.getPersistentData().getString("dragonsbuildtools_carriedBlock");
        if (!blockId.isEmpty()) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
            entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(block)));
            entity.getPersistentData().putString("dragonsbuildtools_carriedBlock", "");
        }

        if (!(entity instanceof Mob mob)) return;
        if (!mob.getPersistentData().getBoolean("dragonsbuildtools_slime_split")) return;

        float scale = mob.getPersistentData().getFloat("dragonsbuildtools_scale");
        if (scale <= 0.25F) return;

        Level level = mob.level();
        for (int i = 0; i < 2; i++) {
            Mob child = (Mob) mob.getType().create(level);
            if (child == null) continue;
            child.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
            child.getPersistentData().putBoolean("dragonsbuildtools_slime_split", true);
            child.getPersistentData().putFloat("dragonsbuildtools_scale", scale / 2f);
            child.getPersistentData().putBoolean("dragonsbuildtools_update_scale", true);
            if(child.getPersistentData().getFloat("dragonsbuildtools_scale") <= 0.25f) child.getPersistentData().putBoolean("dragonsbuildtools_slime_skip_split", true);
            level.addFreshEntity(child);
            NeoForge.EVENT_BUS.post(new MobSplitAbilityEvent(child));
        }

    }
    /*@SubscribeEvent
    public static void OnEntityTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Mob entity)) return;
        if (entity.level().isClientSide()) return;  // ✅ only run on server

        if (entity.getPersistentData().getBoolean("dragonsbuildtools_update_scale")) {
            entity.getPersistentData().putBoolean("dragonsbuildtools_update_scale", false);

            float scale = entity.getPersistentData().getFloat("dragonsbuildtools_scale");
            NetworkHandler.sendScaleUpdate(entity, scale);
            entity.refreshDimensions();

            System.out.println("✅ Sent scale update for entity ID " + entity.getId() + ": " + scale);
        }
    }*/
    @SubscribeEvent
    public static void OnEntityConstructing(MobSplitAbilityEvent event){
        if (!(event.getChild() instanceof Mob entity)) return;
        if (entity.level().isClientSide()) return;  // ✅ only run on server


        float scale = entity.getPersistentData().getFloat("dragonsbuildtools_scale");
        NetworkHandler.sendScaleUpdate(entity, scale);
        entity.refreshDimensions();

        System.out.println("✅ Sent scale update for entity ID " + entity.getId() + ": " + scale);
    }

}
