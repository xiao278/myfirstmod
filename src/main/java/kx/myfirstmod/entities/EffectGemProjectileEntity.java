package kx.myfirstmod.entities;

import kx.myfirstmod.items.EffectGem;
import kx.myfirstmod.items.ModItems;
import kx.myfirstmod.utils.ParticleSpawnPacket;
import kx.myfirstmod.utils.ParticleUtils;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EffectGemProjectileEntity extends PotionEntity {
    private static final int maxTickAge = 40;
    private boolean reachedMaxSpeed = false;
    private double maxSpeed = 5;
    private double acceleration = 0.25;
    public EffectGemProjectileEntity(EntityType<? extends PotionEntity> entityType, World world) {
        super(entityType, world);
    }

    public EffectGemProjectileEntity(World world, LivingEntity owner) {
        this(ModEntityTypes.EFFECT_GEM_PROJECTILE_ENTITY, world);
        this.setPosition(owner.getX(), owner.getEyeY() - (double)0.1F, owner.getZ());
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();
        World world = this.getWorld();
        if (!world.isClient) {
            if (!reachedMaxSpeed) {
                Vec3d velocity = this.getVelocity();
                double speed = velocity.length();
                Vec3d dir = velocity.multiply(1 / speed);
                double newSpeed = speed + acceleration;
                if (newSpeed > maxSpeed) {
                    reachedMaxSpeed = true;
                    setVelocity(dir.multiply(maxSpeed));
                }
                else {
                    setVelocity(dir.multiply(newSpeed));
                }
            }
            if (this.age >= maxTickAge) {
                this.discard();
            }
        }
        else {
            spawnParticles(world, this.getPos());
        }
    }

    private void spawnParticles(World world, Vec3d pos) {
        int color = PotionUtil.getColor(getStack());
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        world.addParticle(ParticleTypes.ENTITY_EFFECT,
                pos.x, pos.y, pos.z,
                red,green,blue
        );
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.EFFECT_GEM;
    }

    @Override
    public ItemStack getStack() {
        ItemStack stack = super.getStack();
        if (this.getWorld().isClient) {
            EffectGem.storeIsProjectile(stack, true);
        }
        return stack;
    }
}
