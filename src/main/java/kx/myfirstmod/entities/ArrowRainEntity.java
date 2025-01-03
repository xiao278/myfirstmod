package kx.myfirstmod.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArrowRainEntity extends ProjectileEntity {
    private Vec3d velocity;
    private Vec3d position;
    public ArrowRainEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public ArrowRainEntity(EntityType<? extends ProjectileEntity> entityType, World world, PlayerEntity owner, Vec3d pos, Vec3d vel) {
        super(entityType, world);
        this.setOwner(owner);
        this.setPosition(pos);
        this.setVelocity(vel);
    }

    @Override
    protected void initDataTracker() {

    }
}
