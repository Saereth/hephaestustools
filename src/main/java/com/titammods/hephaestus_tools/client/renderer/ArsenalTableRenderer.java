package com.titammods.hephaestus_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.titammods.hephaestus_tools.tables.blockentity.ArsenalTableBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

public class ArsenalTableRenderer implements BlockEntityRenderer<ArsenalTableBlockEntity, ArsenalTableRenderer.State> {

    public ArsenalTableRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    public static class State extends BlockEntityRenderState {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ArsenalTableBlockEntity blockEntity, State state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    }
}
