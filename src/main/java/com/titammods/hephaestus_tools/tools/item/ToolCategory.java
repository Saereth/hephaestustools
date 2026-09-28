package com.titammods.hephaestus_tools.tools.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum ToolCategory {
    PICKAXE(BlockTags.MINEABLE_WITH_PICKAXE),
    AXE(BlockTags.MINEABLE_WITH_AXE),
    SHOVEL(BlockTags.MINEABLE_WITH_SHOVEL),
    HOE(BlockTags.MINEABLE_WITH_HOE),
    SWORD(BlockTags.SWORD_EFFICIENT);

    public final TagKey<Block> tag;
    ToolCategory(TagKey<Block> tag) { this.tag = tag; }
}