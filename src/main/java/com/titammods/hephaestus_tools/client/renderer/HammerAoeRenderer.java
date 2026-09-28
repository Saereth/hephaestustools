package com.titammods.hephaestus_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tools.aoe.IAoeTool;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;

import java.util.List;

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
            if (!(stack.getItem() instanceof IAoeTool aoeTool)) return false;

            if (target == null || target.getType() != HitResult.Type.BLOCK) return false;

            Level level = Minecraft.getInstance().level;
            if (level == null) return false;

            if (!aoeTool.isEffectiveOnBlock(stack, level.getBlockState(target.getBlockPos()), player)) return false;

            List<BlockPos> extra = aoeTool.getExtraBlocks(level, target, player, stack);
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
}