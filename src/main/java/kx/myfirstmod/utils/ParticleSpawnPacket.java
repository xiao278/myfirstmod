package kx.myfirstmod.utils;

import kx.myfirstmod.MyFirstMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleSpawnPacket {
    public static final Identifier PACKET_ID = new Identifier(MyFirstMod.MOD_ID, "spawn_particles");

    public static void send(ServerWorld world, ParticleEffect particle, Vec3d pos, Vec3d vel) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(Registries.PARTICLE_TYPE.getId(particle.getType()));
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeDouble(vel.x);
        buf.writeDouble(vel.y);
        buf.writeDouble(vel.z);
//        ParticleTypes.POOF.asString();

        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, PACKET_ID, buf);
        }
    }

    public static void registerClientListener() {
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID, (client, handler, buf, responseSender) -> {
            Identifier particleId = buf.readIdentifier();
            ParticleEffect particle = (ParticleEffect) Registries.PARTICLE_TYPE.get(particleId);
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            double vel_x = buf.readDouble();
            double vel_y = buf.readDouble();
            double vel_z = buf.readDouble();

            // Run particle spawning on the client thread
            client.execute(() -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                World world = mc.world;
                if (world != null) {
                    world.addParticle(particle, x, y, z, vel_x, vel_y, vel_z);
                }
            });
        });
    }
}
