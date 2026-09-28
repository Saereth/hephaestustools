package com.titammods.hephaestus_tools.table;

import net.minecraft.world.item.ItemStack;

public final class MasteryLevel {
    private MasteryLevel() {}

    public static final int T1 = 10, T2 = 20, T3 = 30;

    public static int of(ItemStack tool) { return ToolXp.getLevel(tool); }

    public static String path(ItemStack tool) { return ToolMastery.selected(tool); }

    public static int tier(ItemStack tool) {
        int l = of(tool);
        return l >= T3 ? 3 : l >= T2 ? 2 : l >= T1 ? 1 : 0;
    }
}