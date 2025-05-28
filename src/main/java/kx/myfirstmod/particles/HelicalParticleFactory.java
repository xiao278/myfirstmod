package kx.myfirstmod.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class HelicalParticleFactory implements ParticleFactory<HelicalParticleEffect> {
    private final SpriteProvider spriteProvider;
    public HelicalParticleFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }
    @Override
    public Particle createParticle(HelicalParticleEffect parameters, ClientWorld world,
                                   double x, double y, double z,
                                   double dx, double dy, double dz) {
        return new HelicalParticle(world, spriteProvider, x, y, z, dx, dy, dz, parameters.radius, parameters.angularSpeed);
    }
}
