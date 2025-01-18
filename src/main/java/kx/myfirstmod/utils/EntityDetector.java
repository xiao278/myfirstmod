package kx.myfirstmod.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class EntityDetector {
    public static boolean isLineOfSightClear(World world, Entity source, Entity target) {
        Vec3d sourcePos = source.getEyePos(); // Eye position of the source entity
        Vec3d targetPos = target.getEyePos(); // Eye position of the target entity

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

    public static double getLookAngle(World world, Entity source, Entity target) {
        Vec3d lookDir = source.getRotationVector();
        Vec3d toTarget = getCenter(target).subtract(source.getEyePos());
        Vec3d targetDir = toTarget.normalize();
        // return angle
        return Math.acos(lookDir.dotProduct(targetDir)) * 180 / Math.PI;
    }

    public static LivingEntity findClosestCrosshairEntity (World world, Entity source, double range, double maxAngle, boolean checkVisibility) {
        Vec3d uPos = source.getPos();
        Box searchBox = new Box(
                uPos.x - range, uPos.y - range, uPos.z - range,
                uPos.x + range, uPos.y + range, uPos.z + range
        );

        List<Entity> potentialTargets = world.getOtherEntities(source, searchBox, entity -> (entity instanceof PlayerEntity || entity instanceof MobEntity));

        LivingEntity minAngletarget = null;
        LivingEntity closestLookedAtTarget = null;
        double minAngle = Float.POSITIVE_INFINITY;
        double closestLookedAt = Float.POSITIVE_INFINITY;
        for (Entity e: potentialTargets) {
            if (checkVisibility) {
                if (!isLineOfSightClear(world, source, e)) {
                    continue;
                }
            }
            if (e.isAlive() && e.isLiving()) {
                double angle = EntityDetector.getLookAngle(world, source, e);
                if (angle < minAngle && e instanceof LivingEntity && e.distanceTo(source) < range && angle < maxAngle) {
                    minAngletarget = (LivingEntity) e;
                    minAngle = angle;
                }
                if (isLookingAt(world, source, e)) {
                    double dist = e.getPos().distanceTo(source.getPos());
                    if (dist < closestLookedAt) {
                        closestLookedAt = dist;
                        closestLookedAtTarget = (LivingEntity) e;
                    }
                }
            }
        }

        if (closestLookedAtTarget != null) return closestLookedAtTarget;
        else return minAngletarget;
    }

    public static LivingEntity findClosestCrosshairEntity(World world, Entity source, double range, double maxAngle) {
        return findClosestCrosshairEntity(world, source, range, maxAngle, false);
    }

    public static boolean isLookingAt(World world, Entity source, Entity target) {
        Vec3d sourceEyePos = source.getEyePos();
        Vec3d lookDir = source.getRotationVector();
        double distToTarget = sourceEyePos.distanceTo(target.getEyePos());
        Vec3d boxCenterPos = sourceEyePos.add(lookDir.multiply(distToTarget));
        double boxSize = 1;
        Box box = new Box(boxCenterPos.subtract(boxSize / 2,boxSize / 2,boxSize / 2),
                boxCenterPos.add(boxSize / 2,boxSize / 2,boxSize / 2)
        );
        return box.intersects(target.getBoundingBox());
    }

    public static Vec3d getCenterOffset(Entity e) {
        return e.getBoundingBox().getCenter().subtract(e.getPos());
    }

    public static Vec3d getCenter(Entity e) {
        return e.getBoundingBox().getCenter();
    }
}
