package kx.myfirstmod.enchantments;

import kx.myfirstmod.MyFirstMod;
import kx.myfirstmod.entities.ArrowRainEntity;
import kx.myfirstmod.items.ArrowRainWeapon;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final Enchantment LONG_SHOT = register(new LongshotEnchantment(), "long_shot");
    public static Enchantment register(Enchantment enchantment, String id) {
        // Create the identifier for the item.
        Identifier enchID = Identifier.of(MyFirstMod.MOD_ID, id);

        // Register the item.
        Enchantment registeredEnch = Registry.register(Registries.ENCHANTMENT, enchID, enchantment);

        // Return the registered item!
        return registeredEnch;
    }
    public static void initialize(){}
}
