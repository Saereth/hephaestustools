package com.titammods.hephaestus_tools.tools.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public final class ToolEnchantments {
    private ToolEnchantments() {}

    private static final List<ResourceKey<Enchantment>> BANNED = List.of(
            key("silk_touch"),
            key("looting"),
            key("fortune"),
            key("sharpness"),
            key("sweeping_edge"),
            key("efficiency"),
            key("unbreaking"),
            key("mending"));

    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.withDefaultNamespace(path));
    }

    public static boolean isBanned(Holder<Enchantment> enchantment) {
        for (ResourceKey<Enchantment> key : BANNED) {
            if (enchantment.is(key)) return true;
        }
        return false;
    }
}