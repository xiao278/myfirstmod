package kx.myfirstmod.utils;

import kx.myfirstmod.items.EffectGem;
import kx.myfirstmod.items.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

import java.util.ArrayList;
import java.util.List;

public class EffectGemColorTint {
    public static void register() {
        ColorProviderRegistry.ITEM.register((stack, layer) -> {
            if (layer == 0) { // Tint only layer1
                StatusEffectInstance effect = ((EffectGem) ModItems.EFFECT_GEM).getStoredEffect(stack);
                int color = 0;
                if (effect == null) {
                    color =  PotionUtil.getColor(Potions.WATER);
                }
                else {
                    List<StatusEffectInstance> effectList = new ArrayList<>();
                    effectList.add(effect);
                    color = PotionUtil.getColor(effectList);
                }
//                color = averageColors(0xFFFFFF, color);
                return color;
            }
            return 0xFFFFFF; // Default to no tint for other layers
        }, ModItems.EFFECT_GEM);
    }

    public static int averageColors(int color1, int color2) {
        // Extract RGB channels for color1
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        // Extract RGB channels for color2
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        // Average each channel
        int rAvg = (r1 + r2) / 2;
        int gAvg = (g1 + g2) / 2;
        int bAvg = (b1 + b2) / 2;

        // Combine averaged channels back into 0xRRGGBB format
        return (rAvg << 16) | (gAvg << 8) | bAvg;
    }
}
