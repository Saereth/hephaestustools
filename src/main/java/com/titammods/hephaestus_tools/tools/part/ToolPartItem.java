package com.titammods.hephaestus_tools.tools.part;

import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.materials.MaterialManager;
import com.titammods.hephaestus_tools.materials.MaterialStats;
import com.titammods.hephaestus_tools.registry.ModComponents;
import com.titammods.hephaestus_tools.tools.helper.ToolBuildHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Locale;
import java.util.function.Consumer;

public class ToolPartItem extends Item {

    private final int partSlot;

    public ToolPartItem(Properties props, int partSlot) {
        super(props);
        this.partSlot = partSlot;
    }

    public int getPartSlot() {
        return partSlot;
    }

    public MaterialId getMaterial(ItemStack stack) {
        MaterialId mat = stack.get(ModComponents.PART_MATERIAL.get());
        return mat == null ? MaterialId.EMPTY : mat;
    }

    public ItemStack withMaterial(MaterialId material) {
        ItemStack stack = new ItemStack(this);
        stack.set(ModComponents.PART_MATERIAL.get(), material);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        MaterialId mat = getMaterial(stack);
        if (mat.isEmpty()) {
            tooltip.accept(Component.translatable("tooltip.hephaestus_tools.part.no_material")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.accept(Component.translatable("tooltip.hephaestus_tools.material",
                        Component.translatable("material.hephaestus_tools." + mat.id().getPath()))
                .withStyle(ChatFormatting.GRAY));

        MaterialStats stats = MaterialManager.getInstance().getStatsForSlot(mat, partSlot);
        if (stats == null) return;

        if (partSlot == 0) {
            tooltip.accept(stat("durability", String.valueOf(stats.durability())));
            tooltip.accept(stat("mining_speed", fmt(stats.miningSpeed())));
            tooltip.accept(stat("attack_damage", fmt(stats.attackDamage())));
            tooltip.accept(stat("harvest_tier",
                    Component.translatable("tooltip.hephaestus_tools.tier."
                            + stats.tier().id()).getString()));
        } else if (partSlot == 1) {
            ToolBuildHandler.SupportStats sup = ToolBuildHandler.supportStats(mat, 1);
            tooltip.accept(stat("durability", plus(sup.durability()) + " " + pct(stats.durabilityMult())));
            tooltip.accept(stat("mining_speed", plus(sup.miningSpeed()) + " " + pct(stats.speedMult())));
            tooltip.accept(stat("attack_damage", plus(sup.attackDamage()) + " " + pct(stats.damageMult())));
            tooltip.accept(stat("attack_speed", pct(stats.attackSpeedMult())));
            tooltip.accept(stat("enchantability", plus(sup.enchantability())));
        } else {
            ToolBuildHandler.SupportStats sup = ToolBuildHandler.supportStats(mat, partSlot);
            tooltip.accept(stat("durability", plus(sup.durability())));
            tooltip.accept(stat("mining_speed", plus(sup.miningSpeed())));
            tooltip.accept(stat("attack_damage", plus(sup.attackDamage())));
            tooltip.accept(stat("enchantability", plus(sup.enchantability())));
        }
    }

    private static Component stat(String key, String value) {
        return Component.translatable("tooltip.hephaestus_tools." + key, value)
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    private static String fmt(float f) {
        return String.format(Locale.ROOT, "%.2f", f);
    }

    private static String plus(float value) {
        return (value >= 0f ? "+" : "") + String.format(Locale.ROOT, "%.2f", value);
    }

    private static String pct(float mult) {
        int p = Math.round(mult * 100f);
        return (p >= 0 ? "+" : "") + p + "%";
    }

    @Override
    public Component getName(ItemStack stack) {
        MaterialId mat = getMaterial(stack);
        if (!mat.isEmpty()) {
            return Component.translatable(getDescriptionId())
                    .append(" (")
                    .append(Component.translatable("material.hephaestus_tools." + mat.id().getPath()))
                    .append(")");
        }
        return super.getName(stack);
    }
}