package kx.myfirstmod.items;

import kx.myfirstmod.misc.GuardianLaserDamageSource;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.WardenFeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeamWeapon extends Item {
    public static final double BEAM_RANGE = 32;
    public static final double BEAM_WIDTH = 0.9;
    private static final float BASE_DAMAGE = 10F;

    public BeamWeapon(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            // clientside logic
            shoot(world, user, hand);
//            BeaconBlockEntityRenderer;
//            WardenFeatureRenderer
            return TypedActionResult.pass(stack);
        } else {
            // serverside logic
            shoot(world, user, hand);
            user.setCurrentHand(hand);
            return TypedActionResult.success(stack);
        }
    }

    private void shoot(World world, PlayerEntity user, Hand hand) {
        double lerp_progress = 0;
        Vec3d origin = getShootOrigin(user, hand);
        Vec3d dir = user.getRotationVector();
        Set<LivingEntity> hitEntities = new HashSet<>();
        while (lerp_progress < BEAM_RANGE) {
            Vec3d lerp_pos = origin.add(dir.multiply(lerp_progress));
            if (world.isClient) {
                // clientside debug
                world.addParticle(ParticleTypes.ELECTRIC_SPARK,
                        lerp_pos.x, lerp_pos.y, lerp_pos.z,
                        0, 0, 0
                );
            } else {
                Box searchBox = new Box(lerp_pos.subtract(BEAM_WIDTH, BEAM_WIDTH, BEAM_WIDTH), lerp_pos.add(BEAM_WIDTH, BEAM_WIDTH, BEAM_WIDTH));
                List<Entity> potentialTargets = world.getOtherEntities(user, searchBox, entity -> (entity instanceof PlayerEntity || entity instanceof MobEntity));
                for (Entity e : potentialTargets) {
                    if (e instanceof LivingEntity livingEntity) {
                        hitEntities.add(livingEntity);
                    }
                }
            }
            lerp_progress += BEAM_WIDTH;
        }

        if (world.isClient) {
            //
        } else {
            for (LivingEntity e : hitEntities) {
                RegistryEntry<DamageType> dtype = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MAGIC);
                e.damage(new GuardianLaserDamageSource(dtype, user), BASE_DAMAGE);
            }
        }
    }

    public static Vec3d getShootOrigin(PlayerEntity user, Hand hand) {
        return user.getPos().add(getOffset(user, hand));
    }

    public static Vec3d getOffset(PlayerEntity user, Hand hand) {
        return new Vec3d(0, user.getHeight() / 2, 0).add(user.getHandPosOffset(ModItems.BEAM_WEAPON).multiply(0.5));
    }
}
