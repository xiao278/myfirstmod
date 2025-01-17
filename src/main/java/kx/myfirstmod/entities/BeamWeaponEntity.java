package kx.myfirstmod.entities;

import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.misc.GuardianLaserDamageSource;
import kx.myfirstmod.utils.ParticleUtils;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeamWeaponEntity extends ProjectileEntity {
    public final int LIVING_TICKS = 10;

    public BeamWeaponEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public BeamWeaponEntity(EntityType<? extends ProjectileEntity> entityType, World world, float pitch, float yaw, Vec3d pos) {
        super(entityType, world);
        this.setVelocity(getRotationVector(pitch, yaw));
        this.setPosition(pos);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
//            if (this.age == 1) {
//                Vec3d startPos = this.getPos();
//                Vec3d dir = this.getVelocity();
//                Vec3d endPos = this.getPos().add(dir.multiply(BeamWeapon.BEAM_RANGE));
//                ParticleUtils.lerpSpawn(this.getWorld(), ParticleTypes.REVERSE_PORTAL,
//                        startPos, endPos, dir.multiply(0.0005), 0.325, 0
//                );
//            }
        }
        else {
            if (this.age > LIVING_TICKS) this.discard();
        }
    }
}
