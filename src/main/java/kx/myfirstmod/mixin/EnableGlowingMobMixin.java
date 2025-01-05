package kx.myfirstmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import kx.myfirstmod.entities.ModEntityTypes;
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
        if (entity.getType().equals(ModEntityTypes.ARROW_RAIN_ENTITY)) {
            cir.setReturnValue(true);
        }
    }
}