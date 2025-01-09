package kx.myfirstmod.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EffectGem extends Item {
    private static final String IS_CREATIVE_KEY = "GemIsCreative";
    private static final String EFFECT_KEY = "StoredEffect";

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
                user.addStatusEffect(new StatusEffectInstance(effect));
            }
//            else {
//                user.sendMessage(Text.literal("No effect stored in the gem!"), true);
//            }
        }

        return TypedActionResult.success(stack);
    }

    // Store a single effect in the gem
    public void storeEffect(ItemStack stack, StatusEffectInstance effect) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound effectNbt = new NbtCompound();
        effect.writeNbt(effectNbt);
        nbt.put(EFFECT_KEY, effectNbt);
    }

    // Retrieve the stored effect from the gem
    public StatusEffectInstance getStoredEffect(ItemStack stack) {
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
        if (((EffectGem) ModItems.EFFECT_GEM).getIsCreative(stack)) {
            return Text.literal("Creative " + baseName.getString()); // Append effect name
        }
        return baseName;
    }

    public void storeIsCreative (ItemStack stack, boolean isCreative) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(IS_CREATIVE_KEY, isCreative);
    }

    public boolean getIsCreative(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains(IS_CREATIVE_KEY)) {
            return nbt.getBoolean(IS_CREATIVE_KEY);
        }

        return false;
    }
}
