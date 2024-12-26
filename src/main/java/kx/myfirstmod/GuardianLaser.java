package kx.myfirstmod;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.sound.Sound;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class GuardianLaser extends Item {
    private static final int range = 80;
    private static final float base_damage = 16;
    private static final float reducible_damage = 12; // ideally multiples of 4 or 2
    private static final int LASER_SOUND_COOLDOWN_TICKS = 0;
    private int sound_cooldown_remaining = 0;
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

        LivingEntity target = EntityDetector.findClosestCrosshairEntity(world, user, range, 30);

        if (target != null && (hook == null || hook.isRemoved())) {
//            user.getStackInHand(Hand.OFF_HAND).onStoppedUsing(world, user, 0);
            boolean canSee = EntityDetector.isLineOfSightClear(world, user, target);
            if (!canSee) return TypedActionResult.fail(user.getStackInHand(hand));

            ItemStack stack = user.getStackInHand(hand);
            GuardianLaserEntity GLEntity = new GuardianLaserEntity(ModEntityTypes.GUARDIAN_LASER_ENTITY, world, target, user, getDamage(stack), getWarmupTime(stack));
            world.spawnEntity(GLEntity);
            this.hook = GLEntity;
            user.setCurrentHand(hand);
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
        else {
            sound_cooldown_remaining = Math.max(sound_cooldown_remaining - 1, 0);
            if (hook != null && (hook.hasBeamTarget() && hook.isAlive() && !hook.isRemoved())) {
                if (sound_cooldown_remaining == 0) {
                    float progress = (hook.getBeamTicks() / hook.getWarmupTime());
                    user.playSound(
                            ModSounds.GUARDIAN_LASER_CHARGE_SOUND, 0.5F + 0.5F * progress * progress, progress >= 0.99 ? 0.7F : progress * 0.5F
                    );
                    sound_cooldown_remaining = LASER_SOUND_COOLDOWN_TICKS;
                }
            }
        }
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient()) {
            if (hook != null) {
                hook.stopUsing();
                hook = null;
            }
        } else {
            if (hook != null && (hook.hasBeamTarget() && hook.isAlive() && !hook.isRemoved())) {
                if (hook.getBeamTicks() >= hook.getWarmupTime()) {
                    float volume = 0.1F;
                    user.playSound(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, volume, 1F);
                    user.playSound(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, volume, 2F);
                    user.playSound(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, volume, 3F);
                }
            }
        }
    }

    public static int getMaxWarmupTime() {
        return 80;
    }

    public int getWarmupTime(ItemStack stack) {
        return getMaxWarmupTime() - EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack) * 20;
    }

    public float getDamage(ItemStack stack) {
        return (
                ((base_damage - reducible_damage) + (reducible_damage * getWarmupTime(stack) / getMaxWarmupTime()))
                * (1 + EnchantmentHelper.getLevel(Enchantments.POWER, stack) / (float) 2.0)
        );
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
