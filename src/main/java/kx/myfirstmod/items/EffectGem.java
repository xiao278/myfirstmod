package kx.myfirstmod.items;

import kx.myfirstmod.utils.ParticleUtils;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.impl.client.particle.ParticleFactoryRegistryImpl;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EffectGem extends Item {
    private static final String IS_CREATIVE_KEY = "GemIsCreative";
    private static final String EFFECT_KEY = "StoredEffect";

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 64;
    }

    public EffectGem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Get the stored effect from the gem
            List<StatusEffectInstance> effects = getStoredEffect(stack);

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

            List<StatusEffectInstance> effects = EffectGem.getStoredEffect(stack);
            int color = PotionUtil.getColor(effects);
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
            List<StatusEffectInstance> effects = EffectGem.getStoredEffect(stack);
            for (StatusEffectInstance effect: effects) {
                if (effect != null) {
                    user.addStatusEffect(effect);
                }
            }

            if (user instanceof PlayerEntity pe) {
                pe.getItemCooldownManager().set(this, 400);
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
        List<StatusEffectInstance> effects = getStoredEffect(stack);
        PotionUtil.buildTooltip(effects, tooltip, 1.0F);
    }

    @Override
    public Text getName(ItemStack stack) {
        // Get the base name from the item's settings
        Text baseName = super.getName(stack);
        if (EffectGem.getIsCreative(stack)) {
            return Text.literal("Creative " + baseName.getString()); // Append effect name
        }
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
}
