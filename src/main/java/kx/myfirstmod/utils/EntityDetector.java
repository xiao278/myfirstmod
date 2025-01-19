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
                if (isLookingAt(source, e)) {
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

    public static boolean isLookingAt_old_version(World world, Entity source, Entity target) {
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

    public static boolean isLookingAt(Entity source, Entity target) {
        Vec3d origin = source.getEyePos();
        Vec3d dir = source.getRotationVector();
        Box box = target.getBoundingBox();
        // r.dir is unit direction vector of ray
        Vec3d dirfrac = new Vec3d(1 / dir.x, 1 / dir.y, 1 / dir.z);
        // lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
        // r.org is origin of ray
        double t1 = (box.minX - origin.x) * dirfrac.x;
        double t2 = (box.maxX - origin.x) * dirfrac.x;
        double t3 = (box.minY - origin.y) * dirfrac.y;
        double t4 = (box.maxY - origin.y) * dirfrac.y;
        double t5 = (box.minZ - origin.z) * dirfrac.z;
        double t6 = (box.maxZ - origin.z) * dirfrac.z;

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us
        if (tmax < 0)
        {
//            double t = tmax;
            return false;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax)
        {
//            double t = tmax;
            return false;
        }

//        double t = tmin;
        return true;
    }

    public static Vec3d getCenterOffset(Entity e) {
        return e.getBoundingBox().getCenter().subtract(e.getPos());
    }

    public static Vec3d getCenter(Entity e) {
        return e.getBoundingBox().getCenter();
    }
}
