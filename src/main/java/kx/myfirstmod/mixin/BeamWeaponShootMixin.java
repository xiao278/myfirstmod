package kx.myfirstmod.mixin;

import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.items.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class BeamWeaponShootMixin {
    @Shadow public abstract Hand getActiveHand();

    @Inject(at = @At("TAIL"), method = "tick")
    private void shootingWeapon (CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        World world = self.getWorld();
        if (!world.isClient) {
            if (BeamWeapon.canShoot(self, self.getWorld())) {
                if (self instanceof PlayerEntity pe) {
                    BeamWeapon.shoot(world, pe, Hand.MAIN_HAND);
                }
            }
        }
    }
}
