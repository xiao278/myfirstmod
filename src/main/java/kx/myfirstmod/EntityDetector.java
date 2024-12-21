package kx.myfirstmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class EntityDetector {
    public static boolean isLineOfSightClear(World world, Entity source, Entity target) {
        Vec3d sourcePos = source.getEyePos(); // Eye position of the source entity
        Vec3d targetPos = getCenter(target); // Eye position of the target entity

        // Perform a raycast to check for block obstructions
        BlockHitResult result = world.raycast(new RaycastContext(
                sourcePos,
                targetPos,
                RaycastContext.ShapeType.COLLIDER, // Check for blocks that collide
                RaycastContext.FluidHandling.NONE, // Ignore fluids
                source
        ));

        // If the raycast hits something before reaching the target, the line of sight is blocked
        return result.getType() == HitResult.Type.MISS;
    }

    public static double getLookAngle(World world, PlayerEntity source, Entity target) {
        Vec3d lookDir = source.getRotationVector();
        Vec3d toTarget = getCenter(target).subtract(source.getEyePos());
        Vec3d targetDir = toTarget.normalize();
        // return angle
        return Math.acos(lookDir.dotProduct(targetDir)) * 180 / Math.PI;
    }

    public static Vec3d getCenterOffset(Entity e) {
        return e.getBoundingBox().getCenter().subtract(e.getPos());
    }

    public static Vec3d getCenter(Entity e) {
        return e.getBoundingBox().getCenter();
    }
}
