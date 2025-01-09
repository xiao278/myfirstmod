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

    public static final RecipeType<EffectGemRecipe> EFFECT_GEM_RECIPE_TYPE = Registry.register(
                    Registries.RECIPE_TYPE,
                    new Identifier(MyFirstMod.MOD_ID, "effect_gem"),
                    new RecipeType<>() {}
            );
    public static final RecipeSerializer<EffectGemRecipe> EFFECT_GEM_RECIPE = Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    new Identifier(MyFirstMod.MOD_ID, "effect_gem"),
                    new SpecialRecipeSerializer<>(EffectGemRecipe::new)
            );
}
