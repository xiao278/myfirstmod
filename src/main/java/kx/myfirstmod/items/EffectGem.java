package kx.myfirstmod.items;

import kx.myfirstmod.entities.EffectGemProjectileEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EffectGem extends Item {
    private static final String IS_CREATIVE_KEY = "GemIsCreative";
    private static final String EFFECT_KEY = "StoredEffect";
    private static final String UNSTABLE_KEY = "GemIsUnstable";
    private static final String IS_PROJECTILE_KEY = "GemIsProjectile";
    private static final int COOLDOWN_TICKS_PER_POWER = 20;
    private static final int CHARGE_TIME = 32;
    private static final int TICKS_PER_SECOND = 20;
    private static final float UNSTABLE_COOLDOWN_MODIFIER = 1.6F;
    private static final float UNSTABLE_CHARGE_MODIFIER = 0.4F;
    private static final HashMap<StatusEffect, Float> EFFECT_POWER_PER_LEVEL = new HashMap<>();


    @Override
    public int getMaxUseTime(ItemStack stack) {
        if (getIsUnstable(stack)) {
            return (int) (CHARGE_TIME * UNSTABLE_CHARGE_MODIFIER);
        }
        return CHARGE_TIME;
    }

    public EffectGem(Settings settings) {
        super(settings);
        fill_power_map();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Get the stored effect from the gem
            List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(stack);

            if (!effects.isEmpty()) {
                // Apply the effect to the player
                user.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            }
            else {
                user.sendMessage(Text.literal("No effect stored in the gem!"), true);
                return TypedActionResult.fail(stack);
            }
        }
        else {
            return TypedActionResult.pass(stack);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        if (world.isClient) {
            Vec3d pos = user.getEyePos().add(user.getHandPosOffset(ModItems.EFFECT_GEM).multiply(0.5).add(
                    new Vec3d(0,0,0.5).rotateX((float) Math.toRadians(-user.getPitch())).rotateY((float) Math.toRadians(-user.getYaw()))
            ));

            int color = PotionUtil.getColor(stack);
            float red = (color >> 16 & 0xFF) / 255.0F;
            float green = (color >> 8 & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;

            world.addParticle(ParticleTypes.ENTITY_EFFECT,
                    pos.x, pos.y, pos.z,
                    red,green,blue
            );
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(stack);

            if (!EffectGem.getIsUnstable(stack)) {
                // use on self
                PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
                for (StatusEffectInstance effect: effects) {
                    if (effect.getEffectType().isInstant()) {
                        effect.getEffectType().applyInstantEffect(playerEntity, playerEntity, user, effect.getAmplifier(), (double)1.0F);
                    } else {
                        user.addStatusEffect(new StatusEffectInstance(effect));
                    }
                }
            }
            else {
                // spawn throwing entity
                EffectGemProjectileEntity projectile = new EffectGemProjectileEntity(world, user);
                projectile.setItem(stack);
                projectile.setVelocity(user.getRotationVector().multiply(0.1));

                world.spawnEntity(projectile);
            }
            if (user instanceof PlayerEntity pe) {
                pe.getItemCooldownManager().set(this, calculateCooldownTicks(effects, getIsUnstable(stack)));
            }
        }
        return stack;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPYGLASS;
    }

    // Store a single effect in the gem
    public static void storeEffect(ItemStack stack, List<StatusEffectInstance> effects) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList effectsList = new NbtList();
        // Convert each StatusEffectInstance into NBT and add to the list
        for (StatusEffectInstance effect : effects) {
            NbtCompound effectNbt = new NbtCompound();
            effect.writeNbt(effectNbt);
            effectsList.add(effectNbt);
        }

        // Store the list in the item's NBT
        nbt.put(EFFECT_KEY, effectsList);
    }

    // Retrieve the stored effect from the gem
    public static List<StatusEffectInstance> getStoredEffect(ItemStack stack) {
        List<StatusEffectInstance> effects = new ArrayList<>();
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(EFFECT_KEY)) {
            NbtList effectsList = nbt.getList(EFFECT_KEY, 10); // Type 10 is NbtCompound
            for (int i = 0; i < effectsList.size(); i++) {
                NbtCompound effectNbt = effectsList.getCompound(i);
                effects.add(StatusEffectInstance.fromNbt(effectNbt));
            }
        }

        return effects; // Return the list (empty if no effects are stored)
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // Retrieve the stored potion effect
        List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(stack);
        if (getIsCreative(stack)) {
            tooltip.add(Text.literal("Creative-Only").formatted(Formatting.LIGHT_PURPLE));
        }
        if (getIsUnstable(stack)) {
            tooltip.add(Text.literal("Unstable").formatted(Formatting.BLUE));
        }
        if (!effects.isEmpty()) {
            int cd_ticks = calculateCooldownTicks(PotionUtil.getPotionEffects(stack), getIsUnstable(stack));
            float cd_seconds = (float) cd_ticks / 20;
            tooltip.add(Text.literal(cd_seconds + " second cooldown").formatted(Formatting.GRAY));
        }
        PotionUtil.buildTooltip(effects, tooltip, 1.0F);
    }

    @Override
    public Text getName(ItemStack stack) {
        // Get the base name from the item's settings
        Text baseName = super.getName(stack);
//        if (EffectGem.getIsCreative(stack)) {
//            return Text.literal("Creative " + baseName.getString()); // Append effect name
//        }
        return baseName;
    }

    public static void storeIsCreative (ItemStack stack, boolean isCreative) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(IS_CREATIVE_KEY, isCreative);
    }

    public static boolean getIsCreative(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains(IS_CREATIVE_KEY)) {
            return nbt.getBoolean(IS_CREATIVE_KEY);
        }

        return false;
    }

    public static void storeIsUnstable (ItemStack stack, boolean isUnstable) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(UNSTABLE_KEY, isUnstable);
    }

    public static boolean getIsUnstable(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains(UNSTABLE_KEY)) {
            return nbt.getBoolean(UNSTABLE_KEY);
        }

        return false;
    }

    public static void storeIsProjectile (ItemStack stack, boolean isProjectile) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(IS_PROJECTILE_KEY, isProjectile);
    }

    public static boolean getIsProjectile(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains(IS_PROJECTILE_KEY)) {
            return nbt.getBoolean(IS_PROJECTILE_KEY);
        }

        return false;
    }

    public static int calculateCooldownTicks(List<StatusEffectInstance> instances, boolean isUnstable) {
        float total_power = 0;
        for (StatusEffectInstance instance: instances) {
            StatusEffect effect = instance.getEffectType();
            float power = getPower(effect);
            if (!effect.isInstant()) {
                power *= (1 + ((float) instance.getDuration() / (80 * TICKS_PER_SECOND)));
            }
            if (!effect.isBeneficial()) {
                power *= -1;
            }
            total_power += power;
        }
        total_power = Math.abs(total_power);
        if (isUnstable) {
            total_power *= UNSTABLE_COOLDOWN_MODIFIER;
        }
        return (int) (COOLDOWN_TICKS_PER_POWER * (1 + total_power));
    }

    private static float getPower(StatusEffect effect) {
        if (!EFFECT_POWER_PER_LEVEL.containsKey(effect)) {
            return 1;
        }
        else return EFFECT_POWER_PER_LEVEL.get(effect);
    }

    private void fill_power_map() {
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.INSTANT_DAMAGE, 1.5f);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.INSTANT_HEALTH, 1.5f);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.INVISIBILITY, 0.5f);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.RESISTANCE, 1.3f);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.SPEED, 0.7F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.WEAKNESS, 0.5F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.STRENGTH, 1.2F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.REGENERATION, 1.8F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.POISON, 2.0F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.NIGHT_VISION, 0.3F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.FIRE_RESISTANCE, 0.5F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.WITHER, 1.6F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.JUMP_BOOST, 0.7F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.MINING_FATIGUE, 0.6F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.SLOWNESS, 0.7F);
        EFFECT_POWER_PER_LEVEL.put(StatusEffects.BLINDNESS, 3.2F);
    }
}
