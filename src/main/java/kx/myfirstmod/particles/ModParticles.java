package kx.myfirstmod.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kx.myfirstmod.MyFirstMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static DefaultParticleType register(DefaultParticleType particle, String id) {
        Identifier particleID = Identifier.of(MyFirstMod.MOD_ID, id);
        DefaultParticleType registeredParticle = Registry.register(Registries.PARTICLE_TYPE, particleID, particle);
        return registeredParticle;
    }
    public static final ParticleType<HelicalParticleEffect> HELICAL_PARTICLE = Registry.register(
            Registries.PARTICLE_TYPE,
            Identifier.of(MyFirstMod.MOD_ID, "helical_particle"),
            new ParticleType<HelicalParticleEffect>(false, HelicalParticleEffect.FACTORY) {
                @Override
                public Codec<HelicalParticleEffect> getCodec() {
                    return HelicalParticleEffect.CODEC;
                }
            });
    public static void initialize(){};
}
