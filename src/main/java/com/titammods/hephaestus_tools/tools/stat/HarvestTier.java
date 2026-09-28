package com.titammods.hephaestus_tools.tools.stat;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public enum HarvestTier {

    WOOD("wood", 0, BlockTags.INCORRECT_FOR_WOODEN_TOOL),
    STONE("stone", 1, BlockTags.INCORRECT_FOR_STONE_TOOL),
    IRON("iron", 2, BlockTags.INCORRECT_FOR_IRON_TOOL),
    DIAMOND("diamond", 3, BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
    GOLD("gold", 0, BlockTags.INCORRECT_FOR_GOLD_TOOL),
    NETHERITE("netherite", 4, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);

    private final String id;
    private final int miningLevel;
    private final TagKey<Block> incorrectForTag;

    HarvestTier(String id, int miningLevel, TagKey<Block> incorrectForTag) {
        this.id = id;
        this.miningLevel = miningLevel;
        this.incorrectForTag = incorrectForTag;
    }

    public String id() {
        return id;
    }

    public int miningLevel() {
        return miningLevel;
    }

    public TagKey<Block> incorrectForTag() {
        return incorrectForTag;
    }

    public boolean canHarvest(BlockState state) {
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return miningLevel >= 3;
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) return miningLevel >= 2;
        if (state.is(BlockTags.NEEDS_STONE_TOOL)) return miningLevel >= 1;
        return true;
    }

    public static HarvestTier byOrdinal(int ordinal) {
        HarvestTier[] values = values();
        if (ordinal < 0 || ordinal >= values.length) return WOOD;
        return values[ordinal];
    }
}
