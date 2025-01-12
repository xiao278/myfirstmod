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
import net.minecraft.nbt.NbtCompound;
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
    private static final float BASE_DAMAGE = 20F;
    private static final int CHARGE_TICKS = 30;
    public static final int DAMAGE_TICKS = 5;
    private static final String TIME_KEY = "BeamWeaponLastUsedTime";
    private static final String CHARGED_KEY = "BeamWeaponCharged";

    public BeamWeapon(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            // clientside logic
//            shoot(world, user, hand);
//            BeaconBlockEntityRenderer;
//            WardenFeatureRenderer
            return TypedActionResult.pass(stack);
        } else {
            // serverside logic
//            shoot(world, user, hand);
            if (hand != Hand.MAIN_HAND) return TypedActionResult.fail(stack);
            if (!getIsCharged(stack)) {
                // charge
                user.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            }
            else {
                // fire
                storeLastUsedTime(stack, world.getTime());
                storeIsCharged(stack,false);
                return TypedActionResult.success(stack);
            }
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        if (!world.isClient) {
            if (ticksUsed(user, stack) >= CHARGE_TICKS) {
                storeIsCharged(stack, true);
            }
        }
    }

    public int ticksUsed(LivingEntity user, ItemStack stack) {
        return stack.getMaxUseTime() - user.getItemUseTimeLeft();
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        storeIsCharged(stack, true);
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public static boolean canShoot(ItemStack stack, World world) {
        return (world.getTime() - getLastUsedTime(stack)) <= DAMAGE_TICKS;
    }

    public static boolean canShoot(LivingEntity livingEntity, World world) {
        ItemStack stack = livingEntity.getStackInHand(Hand.MAIN_HAND);
        if (!(stack.getItem() instanceof BeamWeapon)) return false;
        return canShoot(stack, world);
    }

    public static void shoot(World world, PlayerEntity user, Hand hand) {
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
                e.damage(new GuardianLaserDamageSource(dtype, user), BASE_DAMAGE / DAMAGE_TICKS);
                e.timeUntilRegen = 0;
            }
        }
    }

//    @Override
//    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
//        super.onStoppedUsing(stack, world, user, remainingUseTicks);
//        storeLastUsedTime(stack, world.getTime());
//    }

    public static Vec3d getShootOrigin(PlayerEntity user, Hand hand) {
        return user.getPos().add(getOffset(user, hand));
    }

    public static Vec3d getOffset(PlayerEntity user, Hand hand) {
        return new Vec3d(0, user.getHeight() / 2, 0).add(user.getHandPosOffset(ModItems.BEAM_WEAPON).multiply(0.5));
    }

    public static void storeLastUsedTime (ItemStack stack, long time) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putLong(TIME_KEY, time);
    }

    public static long getLastUsedTime(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains(TIME_KEY)) {
            return nbt.getLong(TIME_KEY);
        }

        return 0;
    }

    public static void storeIsCharged (ItemStack stack, boolean isCharged) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(CHARGED_KEY, isCharged);
    }

    public static boolean getIsCharged (ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains(CHARGED_KEY)) {
            return nbt.getBoolean(CHARGED_KEY);
        }

        return false;
    }
}
