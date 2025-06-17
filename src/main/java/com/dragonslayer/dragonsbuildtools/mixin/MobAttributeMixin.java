package com.dragonslayer.dragonsbuildtools.mixin;

import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Strider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
class EnderManScreamMixin{
    @Inject(method = "isSensitiveToWater", at = @At("RETURN"), cancellable = true)
    public boolean isSensitiveWater(){
        return false;
    }
}

@Mixin(Chicken.class)
class ChickenAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}

@Mixin(Cow.class)
class CowAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}

@Mixin(Sheep.class)
class SheepAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Pig.class)
class PigAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Rabbit.class)
class RabbitAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Horse.class)
class HorseAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(ZombieHorse.class)
class ZombieHorseAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(SkeletonHorse.class)
class SkeletonHorseAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Donkey.class)
class DonkeyAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Mule.class)
class MuleAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Llama.class)
class LlamaAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Camel.class)
class CamelAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Goat.class)
class GoatAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Turtle.class)
class TurtleAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Sniffer.class)
class SnifferAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Axolotl.class)
class AxolotlAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Parrot.class)
class ParrotAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Bee.class)
class BeeAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(GlowSquid.class)
class GlowSquidAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Squid.class)
class SquidAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Strider.class)
class StriderAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Bat.class)
class BatAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Cod.class)
class CodAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Salmon.class)
class SalmonAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Pufferfish.class)
class PufferfishAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(TropicalFish.class)
class TropicalFishAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Frog.class)
class FrogAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
@Mixin(Tadpole.class)
class TadpoleAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttackDamage(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.ATTACK_DAMAGE, 2.0D);
        cir.setReturnValue(builder);
    }
}
