package kx.myfirstmod.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

import java.util.Locale;

public class HelicalParticleEffect implements ParticleEffect {
    public final double radius;
    public final double angularSpeed;
    public static final Codec<HelicalParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("radius").forGetter(effect -> effect.radius),
            Codec.DOUBLE.fieldOf("angularVelocity").forGetter(effect -> effect.angularSpeed)
    ).apply(instance, HelicalParticleEffect::new));

    public HelicalParticleEffect(double radius, double angularSpeed) {
        this.radius = radius;
        this.angularSpeed = angularSpeed;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.HELICAL_PARTICLE;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeDouble(radius);
        buf.writeDouble(angularSpeed);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "helical %.3f %.3f", radius, angularSpeed);
    }

    public static final Factory<HelicalParticleEffect> FACTORY = new Factory<>() {
        @Override
        public HelicalParticleEffect read(ParticleType<HelicalParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double r = reader.readDouble();
            reader.expect(' ');
            double w = reader.readDouble();
            return new HelicalParticleEffect(r, w);
        }

        @Override
        public HelicalParticleEffect read(ParticleType<HelicalParticleEffect> type, PacketByteBuf buf) {
            double r = buf.readDouble();
            double w = buf.readDouble();
            return new HelicalParticleEffect(r, w);
        }
    };
}
