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

        if (entity instanceof ScaleAccessor scaleAccessor) {
            float scale = scaleAccessor.dragonsbuildtools$getScale();
            float finalScale = (scale != 0.0F) ? scale : 1.0F;
            poseStack.scale(finalScale, finalScale, finalScale);
        }



    }
}
