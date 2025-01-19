package kx.myfirstmod.utils;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;



public class BoundingBox {
    public static BoxHitResult rayIntersection(Box box, Vec3d dir, Vec3d origin) {
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

        return new BoxHitResult(tmin, tmax);
    }

    public static class BoxHitResult {
        public final double tmin;
        public final double tmax;
        public final boolean intersectsBox;
        public BoxHitResult(double tmin, double tmax) {
            this.tmin = tmin;
            this.tmax = tmax;
            intersectsBox = isIntersectsBox(tmin, tmax);
        }

        private boolean isIntersectsBox(double tmin, double tmax) {
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
    }
}