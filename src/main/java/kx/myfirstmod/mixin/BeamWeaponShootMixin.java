package kx.myfirstmod.mixin;

import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.items.ModItems;
import kx.myfirstmod.utils.ParticleSpawnPacket;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
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
        ItemStack stack = self.getStackInHand(Hand.MAIN_HAND);
        if (!world.isClient) {
            if (BeamWeapon.canShoot(self, self.getWorld())) {
                if (self instanceof PlayerEntity pe) {
                    BeamWeapon.shoot(world, pe, Hand.MAIN_HAND);
                    if (BeamWeapon.timeSinceFirstShot(self.getStackInHand(Hand.MAIN_HAND), self.getWorld()) == BeamWeapon.DAMAGE_TICKS) {
                        Vec3d origin = BeamWeapon.getShootOrigin(pe, Hand.MAIN_HAND);
                        Vec3d dir = self.getRotationVector();
                        double spacing = 0.4;
                        double lerp_progress = 0;
                        while (lerp_progress < BeamWeapon.BEAM_RANGE) {
                            ParticleSpawnPacket.send((ServerWorld) world, ParticleTypes.REVERSE_PORTAL, origin.add(dir.multiply(lerp_progress)), dir.multiply(0.015));
                            lerp_progress += spacing;
                        }
                    }
                }
            }
        }
        else {
            if (BeamWeapon.canShoot(self, self.getWorld())) {

            }
        }
    }
}
