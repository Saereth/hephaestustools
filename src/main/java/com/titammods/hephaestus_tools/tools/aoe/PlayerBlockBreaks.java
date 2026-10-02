package com.titammods.hephaestus_tools.tools.aoe;

import com.titammods.hephaestus_tools.event.MasteryEvents;
import com.titammods.hephaestus_tools.tools.item.ModifiableItem;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class PlayerBlockBreaks {
    private static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);

    private PlayerBlockBreaks() {}

    public static boolean isBreaking() { return BREAKING.get(); }

    public static boolean isUsable(ItemStack tool) {
        return !tool.isEmpty() && tool.getItem() instanceof ModifiableItem
                && ToolStack.isInitialized(tool) && !ToolStack.isBroken(tool);
    }

    public static void afterBreak(ServerPlayer player, BlockPos origin, BlockState state, List<BlockPos> extras) {
        if (isBreaking() || !isUsable(player.getMainHandItem())) return;
        BREAKING.set(true);
        try {
            ItemStack tool = player.getMainHandItem();
            for (BlockPos pos : extras) breakExtra(player, tool, pos);
            if (player.getMainHandItem() == tool && isUsable(tool)) {
                MasteryEvents.afterBlockBreak(player, origin, state);
            }
        } finally {
            BREAKING.remove();
        }
    }

    public static boolean breakExtra(ServerPlayer player, ItemStack tool, BlockPos pos) {
        var level = player.level();
        if (player.getMainHandItem() != tool || !isUsable(tool)
                || !level.isInWorldBounds(pos) || !level.hasChunkAt(pos)
                || !level.getWorldBorder().isWithinBounds(pos)
                || !player.mayUseItemAt(pos, BlockSideHitHandler.getSideHit(player), tool)) return false;
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(level, pos) < 0
                || !tool.isCorrectToolForDrops(state) || !state.canHarvestBlock(level, pos, player)) return false;
        boolean wasBreaking = isBreaking();
        BREAKING.set(true);
        try {
            boolean accepted = player.gameMode.destroyBlock(pos);
            player.connection.send(new ClientboundBlockUpdatePacket(level, pos));
            return accepted && level.getBlockState(pos) != state;
        } finally {
            if (wasBreaking) BREAKING.set(true);
            else BREAKING.remove();
        }
    }
}
