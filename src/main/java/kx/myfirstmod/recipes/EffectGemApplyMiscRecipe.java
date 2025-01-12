package kx.myfirstmod.recipes;

import kx.myfirstmod.items.EffectGem;
import kx.myfirstmod.items.ModItems;
import kx.myfirstmod.misc.ModCustomPotions;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
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

import java.util.HashMap;

public class EffectGemApplyMiscRecipe extends SpecialCraftingRecipe {
    private final HashMap<Item, Potion> INGREDIENT_ITEMS = new HashMap<>();

    public EffectGemApplyMiscRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
        initializeIngredientMap();
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int ingredientCount = 0;
        int gemCount = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (isIngredient(stack.getItem())) {
                ingredientCount++;
            } else if (stack.isOf(ModItems.EFFECT_GEM)) {
                gemCount++;
                boolean isCreativeGem = EffectGem.getIsCreative(stack);
                if (!isCreativeGem) {
                    //reject the match
                    gemCount++;
                }
            }
        }
        return ingredientCount == 1 && gemCount == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        Potion potion = null;
        ItemStack gemStack = null;

        // Find the potion and the gem in the crafting grid
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (isIngredient(stack.getItem())) {
                potion = getIngredientPotion(stack.getItem());
            } else if (stack.isOf(ModItems.EFFECT_GEM)) {
                gemStack = stack.copy(); // Copy the gem stack
            }
        }

        if (potion != null && gemStack != null && gemStack.getItem() instanceof EffectGem) {
            // Transfer the potion's NBT to the gem
            PotionUtil.setPotion(gemStack, potion);
        }

        return gemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.EFFECT_GEM_MISC_RECIPE;
    }

    public boolean isIngredient(Item item) {
        return INGREDIENT_ITEMS.containsKey(item);
    }

    public Potion getIngredientPotion(Item item) {
        if (!isIngredient(item)) {
            return null;
        }
        else {
            return INGREDIENT_ITEMS.get(item);
        }
    }

    private void initializeIngredientMap() {
        INGREDIENT_ITEMS.put(Items.NETHER_STAR, ModCustomPotions.WITHERING);
        INGREDIENT_ITEMS.put(Items.GLOWSTONE, ModCustomPotions.GLOWING);
    }
}
