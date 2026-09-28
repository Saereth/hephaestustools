package com.titammods.hephaestus_tools.client.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ModifierModelRegistry {

    private ModifierModelRegistry() {}

    private static final Map<Identifier, Identifier> TEXTURE_OVERRIDES = new HashMap<>();

    @Nullable
    public static Identifier getTexture(Item toolItem, Identifier modifierId) {
        Identifier override = TEXTURE_OVERRIDES.get(modifierId);
        if (override != null) {
            return override;
        }

        Identifier toolId = BuiltInRegistries.ITEM.getKey(toolItem);
        if (toolId == null) {
            return null;
        }

        String toolPath = toolId.getPath();
        String modifierSuffix = modifierId.getNamespace() + "_" + modifierId.getPath();

        return Identifier.fromNamespaceAndPath(
                "hephaestus_tools",
                "item/tool/" + toolPath + "/modifiers/" + modifierSuffix
        );
    }

    public static void registerOverride(Identifier modifierId, Identifier texture) {
        TEXTURE_OVERRIDES.put(modifierId, texture);
    }
}
