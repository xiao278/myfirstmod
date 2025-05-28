package kx.myfirstmod.particles;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class HelicalParticle extends SpriteBillboardParticle {
    private final double radius;
    private final double angularSpeed;
    private final Vec3d center;
    private final Vec3d direction;
    private final double phaseShift;
    private final double linearSpeed;
    private final Vec3d U;
    private final Vec3d V;
    private final boolean fading = true;
    public HelicalParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z,
                           double velocityX, double velocityY, double velocityZ, double radius, double angularVelocity) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.setSprite(spriteProvider.getSprite(this.random));
        this.setVelocity(0,0,0);
        this.setAlpha(0);
        Vec3d velocity = new Vec3d(velocityX, velocityY, velocityZ);
        this.linearSpeed = velocity.length();
        this.direction = velocity.multiply(linearSpeed > 1E-4 ? 1 / linearSpeed : 0);
        this.center = new Vec3d(x, y, z);
        this.phaseShift = world.random.nextDouble() * 2 * Math.PI;
        this.radius = radius;
        this.angularSpeed = angularVelocity;
        this.maxAge = 30;
        this.collidesWithWorld = false;
        this.scale = 0.08f;
        this.setColor(1,0,0);

        Vec3d A = Math.abs(direction.y) < 0.95 ? new Vec3d(0, 1, 0) : new Vec3d(1, 0, 0);
        this.U = direction.crossProduct(A).normalize();
        this.V = direction.crossProduct(U);

        Vec3d initialPos = parametricPoint(this.age);
        this.setPos(initialPos.x, initialPos.y, initialPos.z);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (fading) {
            //https://www.desmos.com/calculator/oxin02ejal
//            this.setAlpha(1 - Math.abs(
//                    age / (this.getMaxAge() / 2f) - (1 + 1 / (float) this.getMaxAge())
//            ));
            this.setAlpha(1 - this.age / (float) this.getMaxAge());
        }
        else {
            this.setAlpha(1);
        }
        Vec3d curPos = new Vec3d(this.x, this.y, this.z);
        Vec3d nexPos = parametricPoint(this.age);
        Vec3d v = nexPos.subtract(curPos).multiply(1/20d);
        this.setVelocity(v.x, v.y, v.z);
        this.setPos(nexPos.x, nexPos.y, nexPos.z);
    }

    protected Vec3d parametricPoint(int age) {
        double theta = 2 * Math.PI * angularSpeed * (age / 20d) + phaseShift;
        Vec3d radial_component = U.multiply(radius * Math.sin(theta)).add(V.multiply(radius * Math.cos(theta)));
        Vec3d positional_component = center.add(direction.multiply(age * linearSpeed));
        return radial_component.add(positional_component);
    }
}
