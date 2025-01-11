package kx.myfirstmod.recipes;

import com.google.gson.JsonObject;
import kx.myfirstmod.MyFirstMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static void initialize() {
        // This is called during mod initialization
    }

    public static final RecipeType<EffectGemApplyPotionRecipe> EFFECT_GEM_POTION_RECIPE_TYPE = Registry.register(
                    Registries.RECIPE_TYPE,
                    new Identifier(MyFirstMod.MOD_ID, "effect_gem_potion"),
                    new RecipeType<>() {}
            );
    public static final RecipeSerializer<EffectGemApplyPotionRecipe> EFFECT_GEM_POTION_RECIPE = Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    new Identifier(MyFirstMod.MOD_ID, "effect_gem_potion"),
                    new SpecialRecipeSerializer<>(EffectGemApplyPotionRecipe::new)
            );

    public static final RecipeType<EffectGemApplyUnstableRecipe> EFFECT_GEM_UNSTABLE_RECIPE_TYPE = Registry.register(
            Registries.RECIPE_TYPE,
            new Identifier(MyFirstMod.MOD_ID, "effect_gem_unstable"),
            new RecipeType<>() {}
    );
    public static final RecipeSerializer<EffectGemApplyUnstableRecipe> EFFECT_GEM_UNSTABLE_RECIPE = Registry.register(
            Registries.RECIPE_SERIALIZER,
            new Identifier(MyFirstMod.MOD_ID, "effect_gem_unstable"),
            new SpecialRecipeSerializer<>(EffectGemApplyUnstableRecipe::new)
    );

    public static final RecipeType<EffectGemApplyMiscRecipe> EFFECT_GEM_MISC_RECIPE_TYPE = Registry.register(
            Registries.RECIPE_TYPE,
            new Identifier(MyFirstMod.MOD_ID, "effect_gem_misc"),
            new RecipeType<>() {}
    );
    public static final RecipeSerializer<EffectGemApplyMiscRecipe> EFFECT_GEM_MISC_RECIPE = Registry.register(
            Registries.RECIPE_SERIALIZER,
            new Identifier(MyFirstMod.MOD_ID, "effect_gem_misc"),
            new SpecialRecipeSerializer<>(EffectGemApplyMiscRecipe::new)
    );
}
