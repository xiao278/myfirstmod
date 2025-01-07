package kx.myfirstmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.systems.RenderSystem;
import kx.myfirstmod.entities.ModEntityTypes;
import kx.myfirstmod.utils.BlockGlowRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class RenderLayerMixin {
    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private void changeRenderLayer(LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderLayer> cir) {
        if (BlockGlowRenderer.getEntity() != null && entity.getUuid() == BlockGlowRenderer.getEntity().getUuid()) {
            cir.setReturnValue(BlockGlowRenderer.TEST_LAYER);
        }
//        cir.setReturnValue(RenderLayer.getOutline(BlockGlowRenderer.OUTLINE_TEXTURE));
    }

//    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
//    private void injectColor(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        RenderSystem.setShaderColor(1, 1, 1, 1);
//    }
}