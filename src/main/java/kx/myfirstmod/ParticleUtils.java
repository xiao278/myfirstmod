package kx.myfirstmod;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ParticleUtils {
    public static void spawnParticles(World world, int count, Vec3d pos, Vec3d vel) {
        for (int i = 0; i < count; i++) {
            world.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    pos.x,pos.y,pos.z,
                    vel.x,vel.y,vel.z
            );
        }
    }

    public static void spawnParticlesRandom(World world, int count, Vec3d pos, Vec3d vel, double pRand, double vRand) {
        Random r = world.getRandom();
        for (int i = 0; i < count; i++) {
            world.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    pos.x + r.nextDouble() * pRand, pos.y + r.nextDouble() * pRand, pos.z + r.nextDouble() * pRand,
                    vel.x + r.nextDouble() * vRand, vel.y + r.nextDouble() * vRand, vel.z + r.nextDouble() * vRand
            );
        }
    };
}
