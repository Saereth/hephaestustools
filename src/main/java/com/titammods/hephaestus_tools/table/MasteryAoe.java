package com.titammods.hephaestus_tools.table;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class MasteryAoe {
    private MasteryAoe() {}

    public static List<BlockPos> square(Level level, Player player, int r, Predicate<BlockState> match) {
        List<BlockPos> out = new ArrayList<>();
        HitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (!(hit instanceof BlockHitResult brt) || brt.getType() != HitResult.Type.BLOCK || brt.getDirection() == null) return out;
        Direction d1, d2;
        switch (brt.getDirection().getAxis()) {
            case Y -> { d1 = Direction.SOUTH; d2 = Direction.EAST; }
            case X -> { d1 = Direction.UP;    d2 = Direction.SOUTH; }
            default -> { d1 = Direction.UP;   d2 = Direction.EAST; }
        }
        BlockPos c = brt.getBlockPos();
        for (int i = -r; i <= r; i++) for (int j = -r; j <= r; j++) {
            if (i == 0 && j == 0) continue;
            BlockPos p = c.relative(d1, i).relative(d2, j);
            BlockState s = level.getBlockState(p);
            if (!level.isEmptyBlock(p) && s.getDestroySpeed(level, p) >= 0 && match.test(s)) out.add(p);
        }
        return out;
    }

    public static BlockPos behind(Level level, Player player) {
        HitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (!(hit instanceof BlockHitResult brt) || brt.getType() != HitResult.Type.BLOCK) return null;
        return brt.getBlockPos().relative(brt.getDirection().getOpposite());
    }

    public static void breakBlocks(ServerLevel level, ServerPlayer player, ItemStack tool, List<BlockPos> positions, boolean freeDurability) {
        for (BlockPos p : positions) {
            BlockState st = level.getBlockState(p);
            if (level.isEmptyBlock(p) || !level.hasChunkAt(p) || !st.canHarvestBlock(level, p, player)) continue;
            Block b = st.getBlock();
            if (player.getAbilities().instabuild) {
                if (st.onDestroyedByPlayer(level, p, player, true, st.getFluidState())) b.destroy(level, p, st);
            } else {
                BlockEntity be = level.getBlockEntity(p);
                int xp = st.getExpDrop(level, p, be, player, tool);
                if (!freeDurability) tool.getItem().mineBlock(tool, level, st, p, player);
                if (st.onDestroyedByPlayer(level, p, player, true, st.getFluidState())) {
                    b.destroy(level, p, st);
                    b.playerDestroy(level, player, p, st, be, tool);
                    b.popExperience(level, p, xp);
                }
            }
            player.connection.send(new ClientboundBlockUpdatePacket(level, p));
        }
    }

    public static void harvestCrops(ServerLevel level, ServerPlayer player, ItemStack tool, List<BlockPos> positions, boolean replant, boolean partial) {
        for (BlockPos p : positions) {
            BlockState st = level.getBlockState(p);
            if (!(st.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(st)) continue;
            Block.dropResources(st, level, p, null, player, tool);
            if (replant || partial) {
                int age = partial ? Math.max(1, crop.getMaxAge() / 2) : 0;
                level.setBlock(p, crop.getStateForAge(age), 3);
            } else {
                level.destroyBlock(p, false);
            }
            player.connection.send(new ClientboundBlockUpdatePacket(level, p));
        }
    }

    public static boolean isCrop(BlockState s) { return s.getBlock() instanceof CropBlock; }
    public static boolean isMatureCrop(BlockState s) { return s.getBlock() instanceof CropBlock c && c.isMaxAge(s); }
    public static boolean isEarth(BlockState s) {
        return s.is(BlockTags.DIRT) || s.is(BlockTags.SAND) || s.is(BlockTags.REPLACEABLE_BY_TREES)
                || s.is(net.minecraft.world.level.block.Blocks.GRAVEL) || s.is(net.minecraft.world.level.block.Blocks.CLAY);
    }
    public static boolean notOre(BlockState s) { return !s.is(net.neoforged.neoforge.common.Tags.Blocks.ORES); }
}