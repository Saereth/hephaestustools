package com.titammods.hephaestus_tools.compat.jei;

import com.titammods.compat.jei.TitamModsJEIPlugin;
import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.registry.ModComponents;
import com.titammods.hephaestus_tools.registry.ModItems;
import com.titammods.hephaestus_tools.tools.part.ToolPartItem;
import com.titammods.setup.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JeiPlugin
public class HephaestusToolsJeiPlugin implements IModPlugin {

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(HephaestusTools.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() { return UID; }

    private static Set<Item> partItems() {
        return Set.of(
                ModItems.PICK_HEAD.get(), ModItems.HAMMER_HEAD.get(), ModItems.SMALL_AXE_HEAD.get(),
                ModItems.BROAD_AXE_HEAD.get(), ModItems.ADZE_HEAD.get(), ModItems.LARGE_PLATE.get(),
                ModItems.SMALL_BLADE.get(), ModItems.LARGE_BLADE.get(), ModItems.TOOL_HANDLE.get(),
                ModItems.TOUGH_HANDLE.get(), ModItems.TOOL_BINDING.get(), ModItems.TOUGH_BINDING.get());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        IIngredientSubtypeInterpreter<ItemStack> interp = (stack, ctx) -> {
            MaterialId id = stack.get(ModComponents.PART_MATERIAL.get());
            return id == null ? "none" : id.toString();
        };
        for (Item part : partItems()) reg.registerSubtypeInterpreter(part, interp);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jei) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        var rm = level.getRecipeManager();
        IRecipeManager jeiRm = jei.getRecipeManager();

        List<ItemStack> variants = new ArrayList<>();
        List<ModRecipes.CastingTableRecipe> castGeneric = new ArrayList<>();
        List<ModRecipes.CastingTableRecipe> castWood = new ArrayList<>();

        for (RecipeHolder<ModRecipes.CastingTableRecipe> holder :
                rm.getAllRecipesFor(ModRecipes.CASTING_TABLE_TYPE.get())) {
            ModRecipes.CastingTableRecipe r = holder.value();
            ItemStack result = r.result();

            if (result.getItem() instanceof ToolPartItem && result.has(ModComponents.PART_MATERIAL.get())) {
                variants.add(result);
                continue;
            }

            ResourceLocation rid = BuiltInRegistries.ITEM.getKey(result.getItem());
            if (rid == null || !rid.getPath().endsWith("_cast")) continue;
            ItemStack[] castItems = r.cast().getItems();
            if (castItems.length == 0 || !(castItems[0].getItem() instanceof ToolPartItem)) continue;

            ItemStack woodPart = new ItemStack(castItems[0].getItem());
            woodPart.set(ModComponents.PART_MATERIAL.get(), MaterialId.of(HephaestusTools.MOD_ID, "wood"));
            Ingredient woodIng = DataComponentIngredient.of(false, woodPart);

            castGeneric.add(r);
            castWood.add(new ModRecipes.CastingTableRecipe(woodIng, r.castConsumed(), r.fluid(), result, r.coolingTime()));
        }

        if (!variants.isEmpty()) {
            try { jei.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, variants); }
            catch (Exception ignored) {}
        }
        if (!castGeneric.isEmpty()) {
            jeiRm.hideRecipes(TitamModsJEIPlugin.CASTING_TABLE_TYPE, castGeneric);
            jeiRm.addRecipes(TitamModsJEIPlugin.CASTING_TABLE_TYPE, castWood);
        }
    }
}