package kx.myfirstmod.utils;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
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

    public static double lerpSpawn(World world, ParticleEffect particleEffect, Vec3d prevPos, Vec3d curPos, Vec3d vel, double spacing, double prevOverflow) {
        Vec3d movementVector = curPos.subtract(prevPos);
        Vec3d movementDir = movementVector.normalize();
        double total_length = movementVector.length();
        double cur_lerp = prevOverflow;
        while (cur_lerp < total_length) {
            Vec3d pos = prevPos.add(movementDir.multiply(cur_lerp));
            world.addParticle(particleEffect,
                    pos.x, pos.y, pos.z,
                    vel.x, vel.y, vel.z
            );
            cur_lerp += spacing;
        }
        prevOverflow = cur_lerp - total_length;
        return prevOverflow;
    }
}
