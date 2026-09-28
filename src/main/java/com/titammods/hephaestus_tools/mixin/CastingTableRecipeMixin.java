package com.titammods.hephaestus_tools.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.registry.ModComponents;
import com.titammods.hephaestus_tools.tools.part.ToolPartItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.titammods.setup.ModRecipes$CastingTableRecipe", remap = false)
public abstract class CastingTableRecipeMixin {

    private static final String MOLTEN_PREFIX = "molten_";

    @Shadow public abstract Identifier fluidId();

    @ModifyReturnValue(method = "result", at = @At("RETURN"))
    private ItemStack hephaestusTools$applyPartMaterial(ItemStack result) {
        if (!(result.getItem() instanceof ToolPartItem)) return result;
        Identifier fluidId = this.fluidId();
        if (fluidId == null) return result;

        String path = fluidId.getPath();
        if (path.startsWith(MOLTEN_PREFIX)) path = path.substring(MOLTEN_PREFIX.length());

        ItemStack copy = result.copy();
        copy.set(ModComponents.PART_MATERIAL.get(), MaterialId.of(HephaestusTools.MOD_ID, path));
        return copy;
    }
}
