package kx.myfirstmod.entities;

import kx.myfirstmod.utils.TaskScheduler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ArrowRainEntity extends ArrowEntity {
    private int hitBlockSelfDestruct = 15;
    private boolean hasHitBlock = false;
    private Vec3d targetPos;
    private double speed = 7;
    private boolean guiding = true;

    public ArrowRainEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public ArrowRainEntity(EntityType<? extends ArrowEntity> entityType, World world, PlayerEntity owner, Vec3d pos, BlockPos targetBlock) {
        super(entityType, world);
        this.setOwner(owner);
        this.setPosition(pos);
        Random random = world.getRandom();
        Vec3d center = targetBlock.toCenterPos();
        double x = center.x + (random.nextDouble() - 0.5) * 2.5;
        double z = center.z + (random.nextDouble() - 0.5) * 2.5;
        this.pickupType = PickupPermission.DISALLOWED;
        targetPos = new Vec3d(x, targetBlock.getY(), z);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.hasHitBlock = true;
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasHitBlock) {
            this.hitBlockSelfDestruct -= 1;
        }
        if (this.getWorld().isClient) {
            if (this.hitBlockSelfDestruct <= 1) {
                spawnHitParticles(this.getPos());
            }
        }
        else {
            if (this.targetPos != null && this.guiding) {
                // final minus initial
                Vec3d toTarget = this.targetPos.subtract(this.getPos());
                double dist = targetPos.distanceTo(this.getPos());
                if (dist < 30) {
                    this.guiding = false;
                }
                this.setVelocity(this.targetPos.subtract(this.getPos()).normalize().multiply(this.speed));
            }
//            if (this.hitBlockSelfDestruct <= 1) {
//                Vec3d pos = this.getPos();
//                this.getWorld().createExplosion(
//                        this.getOwner(), // The entity that caused the explosion (can be null)
//                        pos.x,    // X-coordinate of the explosion
//                        pos.y,    // Y-coordinate of the explosion
//                        pos.z,    // Z-coordinate of the explosion
//                        10, // Explosion strength
//                        World.ExplosionSourceType.TNT // Prevent block destruction
//                );
//            }
        }
        if (this.hitBlockSelfDestruct <= 0) {
            this.discard();
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return super.asItemStack();
    }

    private void spawnHitParticles(Vec3d pos) {
        World world = this.getWorld();
        if (world.isClient) {
            for (int i = 0; i < 3; i++) {
                world.addParticle(
                        ParticleTypes.SMOKE,
                        pos.x + (world.random.nextDouble() - 0.5),
                        pos.y + (world.random.nextDouble() - 0.5),
                        pos.z + (world.random.nextDouble() - 0.5),
                        0.0,
                        0.15,
                        0.0
                );
            }
        }
    }
}
