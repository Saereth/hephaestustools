package com.titammods.hephaestus_tools.tools.aoe;

import com.titammods.hephaestus_tools.HephaestusTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.List;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID)
public final class HammerAoeBreakHandler {

    private HammerAoeBreakHandler() {}

    private static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);

    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        if (BREAKING.get()) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof IAoeTool aoeTool)) return;

        Level level = player.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos center = event.getPos();
        BlockState centerState = level.getBlockState(center);
        if (!aoeTool.isEffectiveOnBlock(tool, centerState, player)) return;

        Direction side = BlockSideHitHandler.getSideHit(player);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(center), side, center, false);

        List<BlockPos> extraBlocks = aoeTool.getExtraBlocks(level, hit, player, tool);
        if (extraBlocks.isEmpty()) return;

        BREAKING.set(true);
        try {
            for (BlockPos extraPos : extraBlocks) {
                BlockState extraState = level.getBlockState(extraPos);
                if (extraState.isAir()
                        || !level.hasChunkAt(extraPos)
                        || !player.mayUseItemAt(extraPos, side, tool)
                        || !extraState.canHarvestBlock(level, extraPos, player)) {
                    continue;
                }

                Block extraBlock = extraState.getBlock();
                if (player.getAbilities().instabuild) {
                    if (extraState.onDestroyedByPlayer(level, extraPos, player, tool, true, extraState.getFluidState())) {
                        extraBlock.destroy(level, extraPos, extraState);
                    }
                } else {
                    BlockEntity be = level.getBlockEntity(extraPos);
                    int xp = extraState.getExpDrop(level, extraPos, be, player, tool);
                    tool.getItem().mineBlock(tool, level, extraState, extraPos, player);
                    if (extraState.onDestroyedByPlayer(level, extraPos, player, tool, true, extraState.getFluidState())) {
                        extraBlock.destroy(level, extraPos, extraState);
                        extraBlock.playerDestroy(level, player, extraPos, extraState, be, tool);
                        extraBlock.popExperience(serverLevel, extraPos, xp);
                    }
                }
                player.connection.send(new ClientboundBlockUpdatePacket(level, extraPos));
            }
        } finally {
            BREAKING.set(false);
        }
    }
}