package kx.myfirstmod;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class GuardianLaser extends Item {
    private static final int range = 64;
    private static final int base_damage = 24;
    private GuardianLaserEntity hook;
    public GuardianLaser(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Ensure we don't spawn the lightning only on the client.
        // This is to prevent desync.
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        LivingEntity target = EntityDetector.findClosestCrosshairEntity(world, user, this.range, 30);

        if (target != null && (hook == null || hook.isRemoved())) {
            ItemStack stack = user.getStackInHand(hand);
            if (!world.isClient()) {
                GuardianLaserEntity GLEntity = new GuardianLaserEntity(ModEntityTypes.GUARDIAN_LASER_ENTITY, world, target, user, getDamage(stack), getWarmupTime(stack));
                world.spawnEntity(GLEntity);
                this.hook = GLEntity;
                user.setCurrentHand(hand);
            }
            return TypedActionResult.consume(stack);
        }
        else {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient()) {
            if (hook != null && (!hook.hasBeamTarget() || !hook.isAlive() || hook.isRemoved())) {
                hook.stopUsing();
                hook = null;
            }
        }
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient()) {
            if (hook != null) {
                hook.stopUsing();
                if (user instanceof PlayerEntity) {
                    ((PlayerEntity) user).getItemCooldownManager().set(this, 8);
                }
                hook = null;
            }
        }
    }

    public static int getMaxWarmupTime() {
        return 80;
    }

    public int getWarmupTime(ItemStack stack) {
        return getMaxWarmupTime() - EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack) * 20;
    }

    public int getDamage(ItemStack stack) {
        return base_damage * getWarmupTime(stack) / getMaxWarmupTime();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true; // Allows this item to be enchanted
    }

    @Override
    public int getEnchantability() {
        return 15; // Enchantability value (similar to iron)
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.literal(this.getDamage(stack) + " Attack Damage").formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.literal(this.getWarmupTime(stack)/20 + "s Charge Time" ).formatted(Formatting.DARK_GREEN));
    }

    public Entity getHook() {
        return this.hook;
    }
}
