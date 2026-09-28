package com.titammods.hephaestus_tools.table;

import net.minecraft.util.StringRepresentable;

public enum ArsenalCategory implements StringRepresentable {

    UPGRADE("upgrade"),
    CRAFTING("crafting"),
    MODIFIERS("modifiers"),
    MISCELLANEOUS("miscellaneous");

    private final String name;

    ArsenalCategory(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static ArsenalCategory byName(String raw) {
        if (raw == null) return CRAFTING;
        for (ArsenalCategory c : values()) {
            if (c.name.equalsIgnoreCase(raw)) {
                return c;
            }
        }
        return CRAFTING;
    }
}
