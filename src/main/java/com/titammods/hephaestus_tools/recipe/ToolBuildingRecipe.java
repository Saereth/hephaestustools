package com.titammods.hephaestus_tools.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.registry.ModRecipes;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import com.titammods.hephaestus_tools.tools.part.ToolPartItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record ToolBuildingRecipe(
        Identifier result,
        List<Identifier> parts
) implements Recipe<RecipeInput> {

    public static final MapCodec<ToolBuildingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("result").forGetter(ToolBuildingRecipe::result),
                    Identifier.CODEC.listOf().fieldOf("parts").forGetter(ToolBuildingRecipe::parts)
            ).apply(instance, ToolBuildingRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolBuildingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC, ToolBuildingRecipe::result,
                    Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), ToolBuildingRecipe::parts,
                    ToolBuildingRecipe::new
            );

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return ItemStack.EMPTY;
    }

    public ItemStack getResultItem() {
        Item item = BuiltInRegistries.ITEM.getValue(result);
        return item != null && item != Items.AIR ? new ItemStack(item) : ItemStack.EMPTY;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.TOOLS_CATEGORY.get();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    private static final PlacementInfo PLACEMENT = PlacementInfo.createFromOptionals(List.of());

    @Override
    public PlacementInfo placementInfo() {
        return PLACEMENT;
    }

    public List<MaterialId> extractMaterials(List<ItemStack> inputParts) {
        if (inputParts.size() < parts.size()) return null;
        List<MaterialId> materials = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            ItemStack stack = inputParts.get(i);
            if (stack.isEmpty()) return null;
            if (!(stack.getItem() instanceof ToolPartItem partItem)) return null;
            Identifier expectedPart = parts.get(i);
            Identifier actualPart = BuiltInRegistries.ITEM.getKey(partItem);
            if (!expectedPart.equals(actualPart)) return null;
            MaterialId mat = partItem.getMaterial(stack);
            if (mat.isEmpty()) return null;
            materials.add(mat);
        }
        return materials;
    }

    public ItemStack createResult(List<MaterialId> materials) {
        Item item = BuiltInRegistries.ITEM.getValue(result);
        if (item == null || item == Items.AIR) return ItemStack.EMPTY;
        return ToolStack.createTool(new ItemStack(item), materials);
    }

    @Override
    public RecipeSerializer<ToolBuildingRecipe> getSerializer() {
        return ModRecipes.TOOL_BUILDING_SERIALIZER.get();
    }

    @Override
    public RecipeType<ToolBuildingRecipe> getType() {
        return ModRecipes.TOOL_BUILDING.get();
    }
}
