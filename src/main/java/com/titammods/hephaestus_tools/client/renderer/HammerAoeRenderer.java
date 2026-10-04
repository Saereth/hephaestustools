package com.titammods.hephaestus_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.table.MasteryAoe;
import com.titammods.hephaestus_tools.table.MasteryLevel;
import com.titammods.hephaestus_tools.table.ToolMastery;
import com.titammods.hephaestus_tools.tools.aoe.IAoeTool;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID, value = Dist.CLIENT)
public final class HammerAoeRenderer {

    private static final int MAX_BLOCKS = 60;
    private static final int AOE_COLOR = ARGB.colorFromFloat(0.4f, 0f, 0f, 0f);

    private HammerAoeRenderer() {}

    @SubscribeEvent
    public static void onExtractBlockOutlineRenderState(ExtractBlockOutlineRenderStateEvent event) {
        event.addCustomRenderer(new AoeOutlineRenderer(event.getHitResult(), event.getCamera()));
    }

    private static class AoeOutlineRenderer implements CustomBlockOutlineRenderer {

        private final BlockHitResult target;
        private final Camera camera;

        AoeOutlineRenderer(BlockHitResult hitResult, Camera camera) {
            this.target = hitResult;
            this.camera = camera;
        }

        @Override
        public boolean render(BlockOutlineRenderState renderState, MultiBufferSource.BufferSource buffer,
                              PoseStack poseStack, boolean translucentPass, LevelRenderState levelRenderState) {
            Entity entity = camera.entity();
            if (!(entity instanceof Player player)) return false;

            ItemStack stack = player.getMainHandItem();
            if (!ToolStack.isUsable(stack)) return false;

            if (target == null || target.getType() != HitResult.Type.BLOCK) return false;

            Level level = Minecraft.getInstance().level;
            if (level == null) return false;

            List<BlockPos> extra;
            if (stack.getItem() instanceof IAoeTool aoeTool) {
                if (!aoeTool.isEffectiveOnBlock(stack, level.getBlockState(target.getBlockPos()), player)) return false;
                extra = aoeTool.getExtraBlocks(level, target, player, stack);
            } else {
                extra = masteryBlocks(level, target, stack);
            }
            if (extra.isEmpty()) return false;

            VertexConsumer lineBuilder = buffer.getBuffer(RenderTypes.lines());
            double camX = camera.position().x();
            double camY = camera.position().y();
            double camZ = camera.position().z();

            int rendered = 0;
            for (BlockPos pos : extra) {
                if (!level.getWorldBorder().isWithinBounds(pos)) continue;
                VoxelShape shape = level.getBlockState(pos).getShape(level, pos);
                if (shape.isEmpty()) continue;
                ShapeRenderer.renderShape(poseStack, lineBuilder, shape,
                        pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ,
                        AOE_COLOR, 2f);
                if (++rendered >= MAX_BLOCKS) break;
            }

            return false;
        }
    }

    private static List<BlockPos> masteryBlocks(Level level, BlockHitResult hit, ItemStack stack) {
        String mastery = ToolMastery.selected(stack);
        int lv = MasteryLevel.of(stack);
        if (mastery.isEmpty() || lv < MasteryLevel.T1) return List.of();
        BlockPos pos = hit.getBlockPos();
        return switch (mastery) {
            case "groundworker" -> MasteryAoe.square(level, pos, hit.getDirection(), lv >= 30 ? 2 : 1, MasteryAoe::isEarth);
            case "reaper" -> MasteryAoe.square(level, pos, Direction.UP,
                    lv >= 30 ? 4 : lv >= 20 ? 3 : 2, MasteryAoe::isMatureCrop);
            case "harvest_sweep", "replanter", "green_thumb" -> MasteryAoe.square(level, pos, Direction.UP,
                    lv >= 30 ? 3 : lv >= 20 ? 2 : 1, MasteryAoe::isMatureCrop);
            case "precision_felling" -> {
                BlockState state = level.getBlockState(pos);
                if (!state.is(BlockTags.LOGS)) yield List.of();
                yield connected(level, pos, s -> s.is(BlockTags.LOGS) && s.getBlock() == state.getBlock(),
                        lv >= 30 ? 32 : lv >= 20 ? 16 : 8);
            }
            default -> List.of();
        };
    }

    private static List<BlockPos> connected(Level level, BlockPos origin, Predicate<BlockState> match, int limit) {
        List<BlockPos> result = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        visited.add(origin);
        queue.add(origin);
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dy == 0 && dz == 0) continue;
                BlockPos next = current.offset(dx, dy, dz);
                if (!visited.add(next) || !level.isInWorldBounds(next) || !level.hasChunkAt(next)) continue;
                if (match.test(level.getBlockState(next))) {
                    result.add(next);
                    queue.add(next);
                    if (result.size() >= limit) return result;
                }
            }
        }
        return result;
    }
}