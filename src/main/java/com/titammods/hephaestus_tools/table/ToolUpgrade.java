package com.titammods.hephaestus_tools.table;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Set;

public record ToolUpgrade(ResourceLocation id, String nameKey, Set<ToolRole> roles,
                          int maxLevel, Item costItem, int baseCost, ResourceLocation icon) {

    public int costFor(int targetLevel) {
        int lvl = Math.max(1, targetLevel);
        return baseCost * (1 << (lvl - 1));
    }
}