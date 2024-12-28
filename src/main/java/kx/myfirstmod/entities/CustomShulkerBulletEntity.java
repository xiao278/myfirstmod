package kx.myfirstmod.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CustomShulkerBulletEntity extends ShulkerBulletEntity {
    public CustomShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> entityType, World world) {
        super(entityType, world);
    }

    public CustomShulkerBulletEntity(World world, LivingEntity owner, Entity target, Direction.Axis axis, double aheadDist) {
        super(world, owner, target, axis);
        Vec3d eye_pos = owner.getEyePos();
        Vec3d offset = owner.getRotationVector().multiply(aheadDist);
        Vec3d sum = eye_pos.add(offset);
        this.refreshPositionAndAngles(sum.x, sum.y, sum.z, this.getYaw(), this.getPitch());
    }
}
