package com.titammods.hephaestus_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.hephaestus_tools.tables.blockentity.ArsenalTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ArsenalTableRenderer implements BlockEntityRenderer<ArsenalTableBlockEntity> {

    private final BlockRenderDispatcher dispatcher;

    public ArsenalTableRenderer(BlockEntityRendererProvider.Context ctx) {
        this.dispatcher = ctx.getBlockRenderDispatcher();
    }

    @Override
    public void render(ArsenalTableBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay) {
        BlockState state = be.getBlockState();
        BakedModel model = dispatcher.getBlockModel(state);
        RenderType rt = RenderType.cutout();
        VertexConsumer vc = buffer.getBuffer(rt);
        dispatcher.getModelRenderer().renderModel(
                pose.last(), vc, state, model, 1f, 1f, 1f, light, overlay, ModelData.EMPTY, rt);
    }
}