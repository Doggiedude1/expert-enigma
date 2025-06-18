package com.dragonslayer.dragonsbuildtools.mixin.client;

import com.dragonslayer.dragonsbuildtools.accessor.ScaleAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MobRendererScaleMixin<T extends LivingEntity> {

    @Inject(method = "render", at = @At("HEAD"))
    private void dragonsbuildtools$applyRenderScale(T entity, float entityYaw, float partialTicks, PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        float scale = entity.getPersistentData().getFloat("dragonsbuildtools_scale");
        if (scale != 0.0F) {
            System.out.println("🎨 Rendering " + entity.getName().getString() + " with scale: " + scale);
            poseStack.scale(scale, scale, scale);
        } else {
            System.out.println("🎨 Rendering " + entity.getName().getString() + " with default scale: 1.0");
        }
    }
}
