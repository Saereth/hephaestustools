package com.titammods.hephaestus_tools.tables.layout;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ToolLayout(
        String id,
        Component displayName,
        Component description,
        Item iconItem,
        ToolSlotDef toolSlot,
        List<InputSlotDef> inputs
) {
    public record ToolSlotDef(int x, int y, boolean hidden, @Nullable Identifier iconTexture) {
        public ToolSlotDef(int x, int y) { this(x, y, false, null); }
        public ToolSlotDef(int x, int y, Identifier icon) { this(x, y, false, icon); }
    }

    public record InputSlotDef(int x, int y, @Nullable Item expectedPart,
                               @Nullable Identifier iconTexture) {
        public InputSlotDef(int x, int y) { this(x, y, null, null); }
        public InputSlotDef(int x, int y, Item part) { this(x, y, part, null); }
    }

    public int inputCount() { return inputs.size(); }
}
