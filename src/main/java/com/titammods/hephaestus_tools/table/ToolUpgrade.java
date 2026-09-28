package com.titammods.hephaestus_tools.table;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.Set;

public record ToolUpgrade(Identifier id, String nameKey, Set<ToolRole> roles,
                          int maxLevel, Item costItem, int baseCost, Identifier icon) {

    public int costFor(int targetLevel) {
        int lvl = Math.max(1, targetLevel);
        return baseCost * (1 << (lvl - 1));
    }
}
