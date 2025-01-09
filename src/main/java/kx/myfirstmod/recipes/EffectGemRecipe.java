package kx.myfirstmod.recipes;

import com.google.gson.JsonObject;
import kx.myfirstmod.items.EffectGem;
import kx.myfirstmod.items.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class EffectGemRecipe extends SpecialCraftingRecipe {
    public EffectGemRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }


    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int potionCount = 0;
        int gemCount = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (stack.isOf(Items.POTION)) {
                potionCount++;
                if (PotionUtil.getPotionEffects(stack).size() < 1) {
                    //reject the match
                    potionCount++;
                }
            } else if (stack.isOf(ModItems.EFFECT_GEM)) {
                gemCount++;
                StatusEffectInstance effect = ((EffectGem) ModItems.EFFECT_GEM).getStoredEffect(stack);
                boolean isCreativeGem = ((EffectGem) ModItems.EFFECT_GEM).getIsCreative(stack);
                if (effect != null || !isCreativeGem) {
                    //reject the match
                    gemCount++;
                }
            }
        }

//        System.out.printf("gems: %d, potions: %d\n", gemCount, potionCount);

        return gemCount == 1 && potionCount == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack potionStack = null;
        ItemStack gemStack = null;

        // Find the potion and the gem in the crafting grid
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (stack.isOf(Items.POTION)) {
                potionStack = stack;
            } else if (stack.isOf(ModItems.EFFECT_GEM)) {
                gemStack = stack.copy(); // Copy the gem stack
            }
        }

        if (potionStack != null && gemStack != null && gemStack.getItem() instanceof EffectGem gem) {
            // Transfer the potion's NBT to the gem
            Potion potion = PotionUtil.getPotion(potionStack);
            if (!potion.getEffects().isEmpty()) {
                gem.storeEffect(gemStack, potion.getEffects().get(0));
            }
        }

        return gemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.EFFECT_GEM_RECIPE;
    }
}