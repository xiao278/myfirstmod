package kx.myfirstmod.items;

import kx.myfirstmod.entities.BeamWeaponEntity;
import kx.myfirstmod.entities.ModEntityTypes;
import kx.myfirstmod.misc.GuardianLaserDamageSource;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.WardenFeatureRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeamWeapon extends Item {
    public static final boolean DEBUG_MODE = false;
    public static final double BEAM_RANGE = 32;
    public static final double BEAM_WIDTH = 0.7;
    private static final float BASE_DAMAGE = 25F;
    private static final int CHARGE_TICKS = 100;
    public static final int DAMAGE_TICKS = 1;
    public static final float BASE_MAGIC_DAMAGE_PROPORTION = 0.2F;
    private static final String TIME_KEY = "BeamWeaponLastUsedTime";
    private static final String CHARGED_KEY = "BeamWeaponCharged";

    public BeamWeapon(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
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
//                user.disableShield();
                user.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            }
            else {
                // fire
                world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 2.0F, 0.5F);
//                world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1.0F, 0.5F);
//                world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.0F, 0.2F);
                storeLastUsedTime(stack, world.getTime());
                world.spawnEntity(new BeamWeaponEntity(ModEntityTypes.BEAM_WEAPON_ENTITY, world, user.getPitch(), user.getYaw(), getShootOrigin(user, Hand.MAIN_HAND)));
                if (!DEBUG_MODE) storeIsCharged(stack,false);
                shoot(world, user, hand);
                return TypedActionResult.consume(stack);
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
        if (!world.isClient) {
            if (ticksUsed(user, stack) >= CHARGE_TICKS) {
                storeIsCharged(stack, true);
            }
        }
    }

    public static int ticksUsed(LivingEntity user, ItemStack stack) {
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

    public static long getShootTicksLeft(LivingEntity livingEntity, World world) {
        ItemStack stack = livingEntity.getStackInHand(Hand.MAIN_HAND);
        long shootTime = timeSinceFirstShot(stack, world);
        return Math.max(DAMAGE_TICKS - shootTime, 0);
    }

    public static boolean canShoot(ItemStack stack, World world) {
        return timeSinceFirstShot(stack, world) <= DAMAGE_TICKS;
    }

    public static boolean canShoot(LivingEntity livingEntity, World world) {
        ItemStack stack = livingEntity.getStackInHand(Hand.MAIN_HAND);
        if (!(stack.getItem() instanceof BeamWeapon)) return false;
        return canShoot(stack, world);
    }

    public static float getPullProgress(LivingEntity user, ItemStack stack) {
        return Math.min((float) ticksUsed(user, stack) / CHARGE_TICKS, 1);
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
            ItemStack stack = user.getStackInHand(Hand.MAIN_HAND);
            RegistryEntry<DamageType> dtypeNonPierce = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.PLAYER_ATTACK);
            RegistryEntry<DamageType> dtypePierce = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MAGIC);
            for (LivingEntity e : hitEntities) {
                Vec3d prev_vel = e.getVelocity();
                e.damage(new GuardianLaserDamageSource(dtypeNonPierce, user), getTickDamage(stack) * (1 - getMagicDamageProportion(stack)));
                e.timeUntilRegen = 0;
                e.damage(new GuardianLaserDamageSource(dtypePierce, user), getTickDamage(stack) * getMagicDamageProportion(stack));
                e.timeUntilRegen = 0;
                e.setVelocity(prev_vel);
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

    public static Vec3d getOffset(LivingEntity user, Hand hand) {
        return new Vec3d(0, user.getHeight() * 0.7, 0).add(user.getRotationVector().multiply(-0.3));
    }

    public static long timeSinceFirstShot(ItemStack stack, World world) {
        return world.getTime() - getLastUsedTime(stack);
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

    private static float getDamage(ItemStack stack) {
        return BASE_DAMAGE * (1 + EnchantmentHelper.getLevel(Enchantments.POWER, stack) / 4.0F);
    }

    private static float getTickDamage(ItemStack stack) {
        return getDamage(stack) / DAMAGE_TICKS;
    }

    private static float getMagicDamageProportion(ItemStack stack) {
        int pierce_level = EnchantmentHelper.getLevel(Enchantments.PIERCING, stack);
        return BASE_MAGIC_DAMAGE_PROPORTION + (1 - BASE_MAGIC_DAMAGE_PROPORTION) * Math.min(1, pierce_level * BASE_MAGIC_DAMAGE_PROPORTION);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("When in Main Hand").formatted(Formatting.GRAY));
        tooltip.add(Text.literal(" " + String.format("%.1f", getDamage(stack)) + " Attack Damage").formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.literal(" " + (int) (getMagicDamageProportion(stack) * 100) + "% Armor Ignore" ).formatted(Formatting.DARK_GREEN));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
