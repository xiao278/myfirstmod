package kx.myfirstmod.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EffectGem extends Item {
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
        nbt.put("StoredEffect", effectNbt);
    }

    // Retrieve the stored effect from the gem
    public StatusEffectInstance getStoredEffect(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null && nbt.contains("StoredEffect")) {
            return StatusEffectInstance.fromNbt(nbt.getCompound("StoredEffect"));
        }

        return null; // No effect stored
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // Retrieve the stored potion effect
        StatusEffectInstance effect = getStoredEffect(stack);
        List<StatusEffectInstance> effectList = new ArrayList<>();
        effectList.add(effect);
        PotionUtil.buildTooltip(effectList, tooltip, 1.0F);
    }
}
