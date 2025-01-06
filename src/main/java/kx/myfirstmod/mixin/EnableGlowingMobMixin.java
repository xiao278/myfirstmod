package kx.myfirstmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import kx.myfirstmod.entities.ModEntityTypes;
import kx.myfirstmod.utils.BlockGlowRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class EnableGlowingMobMixin {
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void outlineMob(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (BlockGlowRenderer.getEntity() != null && entity.getUuid() == BlockGlowRenderer.getEntity().getUuid()) {
            cir.setReturnValue(true);
        }
    }
}