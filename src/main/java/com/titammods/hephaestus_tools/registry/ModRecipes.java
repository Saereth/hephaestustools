package com.titammods.hephaestus_tools.registry;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.recipe.ModifierRecipe;
import com.titammods.hephaestus_tools.recipe.PartRecipe;
import com.titammods.hephaestus_tools.recipe.ToolBuildingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, HephaestusTools.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, HephaestusTools.MOD_ID);

    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_BOOK_CATEGORY, HephaestusTools.MOD_ID);

    public static final Supplier<RecipeBookCategory> TOOLS_CATEGORY =
            RECIPE_BOOK_CATEGORIES.register("hephaestus_tools", RecipeBookCategory::new);

    public static final Supplier<RecipeType<PartRecipe>> PART_BUILDER =
            RECIPE_TYPES.register("part_builder", () -> new RecipeType<PartRecipe>() {
                @Override public String toString() { return "part_builder"; }
            });

    public static final Supplier<RecipeType<ToolBuildingRecipe>> TOOL_BUILDING =
            RECIPE_TYPES.register("tool_building", () -> new RecipeType<ToolBuildingRecipe>() {
                @Override public String toString() { return "tool_building"; }
            });

    public static final Supplier<RecipeType<ModifierRecipe>> MODIFIER =
            RECIPE_TYPES.register("modifier", () -> new RecipeType<ModifierRecipe>() {
                @Override public String toString() { return "modifier"; }
            });

    public static final Supplier<RecipeSerializer<PartRecipe>> PART_BUILDER_SERIALIZER =
            RECIPE_SERIALIZERS.register("part_builder",
                    () -> new RecipeSerializer<>(PartRecipe.CODEC, PartRecipe.STREAM_CODEC));

    public static final Supplier<RecipeSerializer<ToolBuildingRecipe>> TOOL_BUILDING_SERIALIZER =
            RECIPE_SERIALIZERS.register("tool_building",
                    () -> new RecipeSerializer<>(ToolBuildingRecipe.CODEC, ToolBuildingRecipe.STREAM_CODEC));

    public static final Supplier<RecipeSerializer<ModifierRecipe>> MODIFIER_SERIALIZER =
            RECIPE_SERIALIZERS.register("modifier",
                    () -> new RecipeSerializer<>(ModifierRecipe.CODEC, ModifierRecipe.STREAM_CODEC));

    public static void registerRecipeTypes() {
    }
}
