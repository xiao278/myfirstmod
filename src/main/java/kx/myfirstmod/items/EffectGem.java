package kx.myfirstmod.items;

import kx.myfirstmod.utils.ParticleSpawnPacket;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.world.ServerWorld;
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
            StatusEffectInstance effect = getStoredEffect(stack);

            if (effect != null) {
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
            world.addParticle(ParticleTypes.EFFECT,
                    pos.x, pos.y, pos.z,
                    0,0,0
            );
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            StatusEffectInstance effect = EffectGem.getStoredEffect(stack);
            if (effect != null) {
                user.addStatusEffect(effect);
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
    public static void storeEffect(ItemStack stack, StatusEffectInstance effect) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound effectNbt = new NbtCompound();
        effect.writeNbt(effectNbt);
        nbt.put(EFFECT_KEY, effectNbt);
    }

    // Retrieve the stored effect from the gem
    public static StatusEffectInstance getStoredEffect(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains(EFFECT_KEY)) {
            return StatusEffectInstance.fromNbt(nbt.getCompound(EFFECT_KEY));
        }

        return null; // No effect stored
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // Retrieve the stored potion effect
        StatusEffectInstance effect = getStoredEffect(stack);
        List<StatusEffectInstance> effectList = new ArrayList<>();
        if (effect == null) {
            PotionUtil.buildTooltip(effectList, tooltip, 0);
        } else {
            effectList.add(effect);
            PotionUtil.buildTooltip(effectList, tooltip, 1.0F);
        }
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
