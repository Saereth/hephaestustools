package com.titammods.hephaestus_tools.tools.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public final class ToolEnchantments {
    private ToolEnchantments() {}

    public static final ResourceKey<Enchantment> SILK_TOUCH = key("silk_touch");
    public static final ResourceKey<Enchantment> LOOTING = key("looting");
    public static final ResourceKey<Enchantment> FORTUNE = key("fortune");
    public static final ResourceKey<Enchantment> SHARPNESS = key("sharpness");
    public static final ResourceKey<Enchantment> SWEEPING_EDGE = key("sweeping_edge");
    public static final ResourceKey<Enchantment> EFFICIENCY = key("efficiency");
    public static final ResourceKey<Enchantment> UNBREAKING = key("unbreaking");
    public static final ResourceKey<Enchantment> MENDING = key("mending");

    private static final List<ResourceKey<Enchantment>> BANNED = List.of(
            SILK_TOUCH, LOOTING, FORTUNE, SHARPNESS, SWEEPING_EDGE, EFFICIENCY, UNBREAKING, MENDING);

    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.withDefaultNamespace(path));
    }

    public static List<ResourceKey<Enchantment>> banned() {
        return BANNED;
    }

    public static boolean isBanned(Holder<Enchantment> enchantment) {
        for (ResourceKey<Enchantment> key : BANNED) {
            if (enchantment.is(key)) return true;
        }
        return false;
    }
}