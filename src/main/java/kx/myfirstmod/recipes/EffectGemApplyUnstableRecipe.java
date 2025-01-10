package kx.myfirstmod.recipes;

import kx.myfirstmod.items.EffectGem;
import kx.myfirstmod.items.ModItems;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class EffectGemApplyUnstableRecipe extends SpecialCraftingRecipe {
    public EffectGemApplyUnstableRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }


    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int modifierItemCount = 0;
        int gemCount = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (stack.isOf(Items.END_CRYSTAL)) {
                modifierItemCount++;
            } else if (stack.isOf(ModItems.EFFECT_GEM)) {
                gemCount++;
                boolean isCreativeGem = EffectGem.getIsCreative(stack);
                if (!isCreativeGem) {
                    //reject the match
                    gemCount++;
                }
            }
        }

//        System.out.printf("gems: %d, potions: %d\n", gemCount, potionCount);

        return gemCount == 1 && modifierItemCount == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack gemStack = null;

        // Find the potion and the gem in the crafting grid
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(ModItems.EFFECT_GEM)) {
                gemStack = stack.copy(); // Copy the gem stack
            }
        }

        if (gemStack != null && gemStack.getItem() instanceof EffectGem) {
            // Transfer the potion's NBT to the gem
            EffectGem.storeIsUnstable(gemStack, !EffectGem.getIsUnstable(gemStack));
        }

        return gemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.EFFECT_GEM_UNSTABLE_RECIPE;
    }
}
