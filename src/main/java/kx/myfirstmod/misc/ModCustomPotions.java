package kx.myfirstmod.misc;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModCustomPotions {
    public static final int TICKS_PER_SECOND = 20;
    public static final Potion WITHERING;
    public static final Potion STRONG_WITHERING;
    public static final Potion LONG_WITHERING;
    public static final Potion GLOWING;

    static {
        //StatusEffect type, int duration, int amplifier
        WITHERING = register("withering", new Potion(
                new StatusEffectInstance(StatusEffects.WITHER, (int) (45 * TICKS_PER_SECOND), 0)
        ));
        STRONG_WITHERING = register("strong_withering", new Potion(
                new StatusEffectInstance(StatusEffects.WITHER, (int) (30 * TICKS_PER_SECOND), 1)
        ));
        LONG_WITHERING = register("long_withering", new Potion(
                new StatusEffectInstance(StatusEffects.WITHER, (int) (120 * TICKS_PER_SECOND), 0)
        ));
        GLOWING = register("glowing", new Potion(
                new StatusEffectInstance(StatusEffects.GLOWING, (int) (60 * TICKS_PER_SECOND), 0)
        ));
    }

    private static Potion register(String name, Potion potion) {
        return (Potion) Registry.register(Registries.POTION, name, potion);
    }

    public static void initialize() {
//        System.out.println(W);
//        Potions
    }
}
