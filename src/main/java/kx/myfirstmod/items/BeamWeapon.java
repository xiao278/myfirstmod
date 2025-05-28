package kx.myfirstmod.items;

import kx.myfirstmod.ModSounds;
import kx.myfirstmod.enchantments.ModEnchantments;
import kx.myfirstmod.entities.BeamWeaponEntity;
import kx.myfirstmod.entities.ModEntityTypes;
import kx.myfirstmod.particles.HelicalParticleEffect;
import kx.myfirstmod.particles.ModParticles;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class BeamWeapon extends Item {
    public static final boolean DEBUG_MODE = false;
    public static final float BEAM_RANGE = 32;
    public static final double BEAM_WIDTH = 0.7;
    private static final float BASE_DAMAGE = 20F ; // 15f
    private static final int CHARGE_TICKS = 80;
    public static final int DAMAGE_TICKS = 1;
    public static final float BASE_MAGIC_DAMAGE_PROPORTION = 0.2F;
    private static final String TIME_KEY = "BeamWeaponLastUsedTime";
    private static final String CHARGED_KEY = "BeamWeaponCharged";
    public static final float LONGSHOT_DMG_CONVERSION_RATIO = 2.5F;
    public static final float LONGSHOT_DMG_CONVERSION_PROPORTION = 0.1F;

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
                // fire sound
                if (EnchantmentHelper.getLevel(ModEnchantments.LONG_SHOT, stack) > 0) {
//                    world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 4F, 0.5F);
                    world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), ModSounds.WEAPON_BEAM_FIRE_LONGSHOT, SoundCategory.PLAYERS, 6F, 0.8F);
                }
                else {
                    world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), ModSounds.WEAPON_BEAM_FIRE, SoundCategory.PLAYERS, 4F, 0.9F);
                }
                storeLastUsedTime(stack, world.getTime());
                Vec3d shootOrigin = getShootOrigin(user, Hand.MAIN_HAND);
                Vec3d shootDir = user.getRotationVector();
                float range = calcBeamLength(world, user, shootOrigin, shootDir, getMaxRange(stack));
                BeamWeaponEntity projectile = new BeamWeaponEntity(ModEntityTypes.BEAM_WEAPON_ENTITY, world, stack, range);
                projectile.setVelocity(shootDir);
                projectile.setPosition(shootOrigin);
                projectile.setOwner(user);
                world.spawnEntity((projectile));

                if (!DEBUG_MODE) storeIsCharged(stack,false);
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

    public static float getDamage(ItemStack stack) {
        return BASE_DAMAGE * (1 + EnchantmentHelper.getLevel(Enchantments.POWER, stack) / 5.0F) * (1 - EnchantmentHelper.getLevel(ModEnchantments.LONG_SHOT, stack) * LONGSHOT_DMG_CONVERSION_PROPORTION);
    }

    public static float getLingeringDamage(ItemStack stack) {
        return (BASE_DAMAGE - getDamage(stack)) * LONGSHOT_DMG_CONVERSION_RATIO;
    }

    public static float getMaxRange(ItemStack stack) {
        return BEAM_RANGE + BEAM_RANGE * EnchantmentHelper.getLevel(ModEnchantments.LONG_SHOT, stack) / 2;
    }

    public static float getMagicDamageProportion(ItemStack stack) {
        int pierce_level = EnchantmentHelper.getLevel(Enchantments.PIERCING, stack);
        return BASE_MAGIC_DAMAGE_PROPORTION + (1 - BASE_MAGIC_DAMAGE_PROPORTION) * Math.min(1, pierce_level * BASE_MAGIC_DAMAGE_PROPORTION);
    }

    private float calcBeamLength(World world, Entity user, Vec3d start, Vec3d dir, float max_range) {
        Vec3d end = start.add(dir.multiply(max_range));
        if (user == null) return max_range;
        BlockHitResult hit = world.raycast(
                new RaycastContext(
                        start,
                        end,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        user
                )
        );
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3d hitPos = hit.getPos();
            return (float) start.distanceTo(hitPos);
        }
        else return max_range;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("When in Main Hand").formatted(Formatting.GRAY));
        tooltip.add(Text.literal(" " + String.format("%.1f", getDamage(stack)) + " Shot Damage").formatted(Formatting.DARK_GREEN));
        if (EnchantmentHelper.getLevel(ModEnchantments.LONG_SHOT, stack) > 0) {
            tooltip.add(Text.literal(" " + String.format("%.1f", getLingeringDamage(stack)) + " Lingering Damage").formatted(Formatting.DARK_GREEN));
        }
        tooltip.add(Text.literal(" " + (int) (getMagicDamageProportion(stack) * 100) + "% Armor Ignore" ).formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.literal(" " + (int) (getMaxRange(stack)) + " Range").formatted(Formatting.DARK_GREEN));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
