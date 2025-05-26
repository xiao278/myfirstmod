package kx.myfirstmod.enchantments;

import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.items.GuardianLaser;
import net.minecraft.enchantment.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LongshotEnchantment extends Enchantment {
    public LongshotEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.CROSSBOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        Item item = stack.getItem();
        if (!(
                item instanceof BeamWeapon
                || item instanceof GuardianLaser
        )) return false;
        return EnchantmentHelper.getLevel(Enchantments.POWER, stack) <= 0 && EnchantmentHelper.getLevel(Enchantments.PIERCING, stack) <= 0;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean isTreasure() {
        return false; // Set to true to make it only available from loot
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return true;
    }
}
