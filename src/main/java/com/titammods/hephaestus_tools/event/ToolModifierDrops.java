package com.titammods.hephaestus_tools.event;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tools.item.ModifiableItem;
import com.titammods.hephaestus_tools.tools.modifier.ModifierEffects;
import com.titammods.hephaestus_tools.tools.nbt.ToolConstructionData;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID)
public final class ToolModifierDrops {

    private ToolModifierDrops() {}

    private static int modLevel(ItemStack tool, Identifier id) {
        if (!(tool.getItem() instanceof ModifiableItem) || !ToolStack.isInitialized(tool)) return 0;
        for (ToolConstructionData.ModifierEntry e : ToolStack.getModifiers(tool)) if (e.id().equals(id)) return e.level();
        return 0;
    }

    @SubscribeEvent
    public static void onDrops(BlockDropsEvent e) {
        ItemStack tool = e.getTool();
        if (modLevel(tool, ModifierEffects.SILK_TOUCH) > 0) return;
        int fortune = modLevel(tool, ModifierEffects.FORTUNE);
        if (fortune <= 0) return;
        var st = e.getState();
        if (!(st.is(Tags.Blocks.ORES) || st.getBlock() instanceof CropBlock)) return;
        var rng = e.getLevel().getRandom();
        int mult = 1 + Math.max(0, rng.nextInt(fortune + 1));
        if (mult <= 1) return;
        for (ItemEntity ie : e.getDrops()) {
            ItemStack s = ie.getItem();
            if (s.getItem() != st.getBlock().asItem())
                s.setCount(s.getCount() * mult);
        }
    }
}
