package kx.myfirstmod.mixin;

import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.items.ModItems;
import kx.myfirstmod.rendering.BeamWeaponFeatureRenderer;
import kx.myfirstmod.utils.BlockGlowRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class FeatureRendererMixin {


    @Invoker("addFeature")
    public abstract boolean invokeAddFeature(FeatureRenderer<?, ?> feature);

//    @Inject(at = @At("RETURN"), method = "<init>")
//    public void init(EntityRendererFactory.Context ctx, EntityModel<?> model, float shadowRadius, CallbackInfo info) {
//        if (model instanceof PlayerEntityModel<?>) {
//            FeatureRenderer<?,?> featureRenderer = new BeamWeaponFeatureRenderer<>((LivingEntityRenderer<?, ?>) (Object) this);
//            this.invokeAddFeature(featureRenderer);
////        System.out.println((model.getClass().getName()));
//        }
//    }
}

@Mixin(BipedEntityModel.class)
abstract class BipedEntityModelMixin<T extends LivingEntity> {
    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"), cancellable = true)
    private void modifyPose(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo info) {
        ItemStack stack = entity.getStackInHand(Hand.MAIN_HAND);
        BipedEntityModel<T> self = (BipedEntityModel<T>) (Object) this;
        if (entity instanceof PlayerEntity
                &&
                (
                        BeamWeapon.getIsCharged(stack) ||
                        BeamWeapon.isDischarging(entity)
                )
        ) {
            CrossbowPosing.hold(self.rightArm, self.leftArm, self.head, true);
        }
//        if (entity instanceof PlayerEntity player) {
//            // Change pose for specific conditions
//            if (player.getMainHandStack().isOf(MyModItems.MY_CUSTOM_ITEM)) {
//                this.rightArm.pitch = -2.0F; // Adjust right arm pitch
//                this.leftArm.pitch = -1.5F; // Adjust left arm pitch
//            }
//        }
    }

}

//@Mixin(PlayerEntity.class)
//class PlayerEntityMixin {
//    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
//    private void isSpectatorInject(CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(true);
//    }
//}
