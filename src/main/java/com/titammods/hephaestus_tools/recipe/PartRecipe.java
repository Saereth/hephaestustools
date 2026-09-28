package com.titammods.hephaestus_tools.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.registry.ModRecipes;
import com.titammods.hephaestus_tools.tools.part.ToolPartItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
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

import java.util.List;

public record PartRecipe(
        Identifier resultPart,
        Identifier patternItem,
        int materialCost
) implements Recipe<RecipeInput> {

    public static final MapCodec<PartRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("result_part").forGetter(PartRecipe::resultPart),
                    Identifier.CODEC.fieldOf("pattern").forGetter(PartRecipe::patternItem),
                    ExtraCodecs.POSITIVE_INT
                            .optionalFieldOf("material_cost", 1).forGetter(PartRecipe::materialCost)
            ).apply(instance, PartRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PartRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC, PartRecipe::resultPart,
                    Identifier.STREAM_CODEC, PartRecipe::patternItem,
                    ByteBufCodecs.VAR_INT, PartRecipe::materialCost,
                    PartRecipe::new
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
        Item item = BuiltInRegistries.ITEM.getValue(resultPart);
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

    public ItemStack createResult(MaterialId material) {
        Item item = BuiltInRegistries.ITEM.getValue(resultPart);
        if (item instanceof ToolPartItem partItem) {
            return partItem.withMaterial(material);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<PartRecipe> getSerializer() {
        return ModRecipes.PART_BUILDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<PartRecipe> getType() {
        return ModRecipes.PART_BUILDER.get();
    }
}
