package com.titammods.hephaestus_tools.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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
import java.util.Optional;

public record ModifierRecipe(
        Identifier modifierId,
        List<Ingredient> inputs,
        int maxLevel,
        int slotsRequired,
        String slotType,
        Optional<Ingredient> toolRequirement
) implements Recipe<RecipeInput> {

    public static final MapCodec<ModifierRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("modifier").forGetter(ModifierRecipe::modifierId),
                    Ingredient.CODEC.listOf().fieldOf("inputs").forGetter(ModifierRecipe::inputs),
                    ExtraCodecs.POSITIVE_INT
                            .optionalFieldOf("max_level", 1).forGetter(ModifierRecipe::maxLevel),
                    ExtraCodecs.POSITIVE_INT
                            .optionalFieldOf("slots_required", 1).forGetter(ModifierRecipe::slotsRequired),
                    ExtraCodecs.NON_EMPTY_STRING
                            .optionalFieldOf("slot_type", "upgrade").forGetter(ModifierRecipe::slotType),
                    Ingredient.CODEC.optionalFieldOf("tool_requirement")
                            .forGetter(ModifierRecipe::toolRequirement)
            ).apply(instance, ModifierRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifierRecipe> STREAM_CODEC =
            StreamCodec.of(
                    (buf, r) -> {
                        buf.writeIdentifier(r.modifierId());
                        buf.writeVarInt(r.inputs().size());
                        for (Ingredient ing : r.inputs()) {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                        }
                        buf.writeVarInt(r.maxLevel());
                        buf.writeVarInt(r.slotsRequired());
                        buf.writeUtf(r.slotType());
                        buf.writeBoolean(r.toolRequirement().isPresent());
                        r.toolRequirement().ifPresent(ing ->
                                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing));
                    },
                    buf -> {
                        Identifier id = buf.readIdentifier();
                        int count = buf.readVarInt();
                        List<Ingredient> inputs = new ArrayList<>(count);
                        for (int i = 0; i < count; i++) {
                            inputs.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        }
                        int maxLevel = buf.readVarInt();
                        int slots = buf.readVarInt();
                        String slotType = buf.readUtf();
                        Optional<Ingredient> req = buf.readBoolean()
                                ? Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buf))
                                : Optional.empty();
                        return new ModifierRecipe(id, inputs, maxLevel, slots, slotType, req);
                    }
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
        return ItemStack.EMPTY;
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

    @Override
    public RecipeSerializer<ModifierRecipe> getSerializer() {
        return ModRecipes.MODIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<ModifierRecipe> getType() {
        return ModRecipes.MODIFIER.get();
    }
}
