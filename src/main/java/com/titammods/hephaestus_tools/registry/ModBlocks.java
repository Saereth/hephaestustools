package com.titammods.hephaestus_tools.registry;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tables.block.ArsenalTableBlock;
import com.titammods.hephaestus_tools.tables.blockentity.ArsenalTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(HephaestusTools.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HephaestusTools.MOD_ID);

    private static BlockBehaviour.Properties anvilProps(Identifier key) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, key))
                .mapColor(MapColor.COLOR_GRAY)
                .sound(SoundType.ANVIL)
                .pushReaction(PushReaction.BLOCK)
                .requiresCorrectToolForDrops()
                .strength(5.0f, 1200.0f)
                .noOcclusion();
    }

    private static BlockBehaviour.Properties woodTableProps(Identifier key) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, key))
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .strength(1.0f, 5.0f)
                .noOcclusion();
    }

    public static final DeferredBlock<ArsenalTableBlock> ARSENAL_TABLE =
            registerBlock("arsenal_table", k -> new ArsenalTableBlock(anvilProps(k)));

    public static final Supplier<BlockEntityType<ArsenalTableBlockEntity>> ARSENAL_TABLE_BE =
            BLOCK_ENTITIES.register("arsenal_table",
                    () -> new BlockEntityType<>(ArsenalTableBlockEntity::new, ARSENAL_TABLE.get()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name,
                                                                    Function<Identifier, T> factory) {
        DeferredBlock<T> block = BLOCKS.register(name, factory);
        ModItems.ITEMS.register(name,
                k -> new BlockItem(block.get(),
                        new Item.Properties().setId(ResourceKey.create(Registries.ITEM, k))));
        return block;
    }
}
