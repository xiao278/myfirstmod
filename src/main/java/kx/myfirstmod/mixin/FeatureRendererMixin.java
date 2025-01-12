package kx.myfirstmod.mixin;

import kx.myfirstmod.rendering.BeamWeaponFeatureRenderer;
import kx.myfirstmod.utils.BlockGlowRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class FeatureRendererMixin {


    @Invoker("addFeature")
    public abstract boolean invokeAddFeature(FeatureRenderer<?, ?> feature);

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(EntityRendererFactory.Context ctx, EntityModel<?> model, float shadowRadius, CallbackInfo info) {
        if (model instanceof PlayerEntityModel<?>) {
            FeatureRenderer<?,?> featureRenderer = new BeamWeaponFeatureRenderer<>((LivingEntityRenderer<?, ?>) (Object) this);
            this.invokeAddFeature(featureRenderer);
//        System.out.println((model.getClass().getName()));
        }
    }
}

//@Mixin(PlayerEntity.class)
//class PlayerEntityMixin {
//    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
//    private void isSpectatorInject(CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(true);
//    }
//}
