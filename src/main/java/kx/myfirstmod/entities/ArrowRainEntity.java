package kx.myfirstmod.entities;

import kx.myfirstmod.utils.NormalDistribution;
import kx.myfirstmod.utils.ParticleUtils;
import kx.myfirstmod.utils.TaskScheduler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ArrowRainEntity extends ArrowEntity {
    private int hitBlockSelfDestruct = 20;
    private boolean hasHitBlock = false;
    private Vec3d targetPos;
    private LivingEntity target;
    private Vec3d offset = new Vec3d(0,0,0);
    private double speed = 3;
    private boolean guiding = true;
    private double guidingRange = 25;
    private Vec3d prevPos;
    private double prevOverflow = 0;

    public ArrowRainEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public ArrowRainEntity(EntityType<? extends ArrowEntity> entityType, World world, PlayerEntity owner, Vec3d pos, BlockPos targetBlock) {
        super(entityType, world);
        this.setOwner(owner);
        this.setPosition(pos);
        Vec3d center = targetBlock.toCenterPos();
        double x = NormalDistribution.nextValue(0, 1);
        double z = NormalDistribution.nextValue(0, 1);
        this.pickupType = PickupPermission.DISALLOWED;
        this.setDamage(5);
        targetPos = center;
        offset = new Vec3d(x,0,z);
    }

    public ArrowRainEntity(EntityType<? extends ArrowEntity> entityType, World world, PlayerEntity owner, Vec3d pos, LivingEntity target) {
        this(entityType, world, owner, pos, target.getBlockPos());
        this.target = target;
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.setGlowing(true);
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
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
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
            // spawn particles along path
            if (!this.hasHitBlock && prevPos != null) {
                prevOverflow =  ParticleUtils.lerpSpawn(getWorld(), ParticleTypes.ELECTRIC_SPARK, this.prevPos, this.getPos(), this.getRotationVector().multiply(speed * 0.125), 0.375, prevOverflow);
            }
            prevPos = this.getPos();
        }
        else {
            if (target != null && target.isAlive() && !target.isRemoved()) {
                targetPos = target.getPos();
            }
            if (this.targetPos != null && this.guiding && !this.hasHitBlock) {
                // final minus initial
                Vec3d toTarget = this.targetPos.add(offset).subtract(this.getPos());
                double dist = targetPos.add(offset).distanceTo(this.getPos());
                if (dist < this.guidingRange) {
                    this.guiding = false;
                }
                this.setVelocity(toTarget.normalize().multiply(this.speed));
            }
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
