package com.titammods.hephaestus_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.titammods.hephaestus_tools.tools.aoe.HammerAoeBreakHandler;
import com.titammods.hephaestus_tools.tools.aoe.PlayerBlockBreaks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow @Final protected ServerPlayer player;

    @WrapMethod(method = "removeBlock")
    private boolean hephaestusTools$afterRemoval(BlockPos pos, BlockState state, boolean canHarvest,
                                                ItemStack tool, Operation<Boolean> original) {
        ItemStack held = player.getMainHandItem();
        var extras = HammerAoeBreakHandler.extraBlocks(player, pos);
        boolean removed = original.call(pos, state, canHarvest, tool);
        // This hook runs only after break cancellation and an actual successful removal.
        if (removed && player.getMainHandItem() == held) {
            PlayerBlockBreaks.afterBreak(player, pos, state, extras);
        }
        return removed;
    }
}
