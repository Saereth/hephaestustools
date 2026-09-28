package com.titammods.hephaestus_tools.event;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.table.MasteryAoe;
import com.titammods.hephaestus_tools.table.MasteryLevel;
import com.titammods.hephaestus_tools.table.ToolMastery;
import com.titammods.hephaestus_tools.tools.item.ModifiableItem;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID)
public final class MasteryInteract {

    private MasteryInteract() {}

    private static String mastery(ItemStack t) {
        if (!(t.getItem() instanceof ModifiableItem) || !ToolStack.isInitialized(t)) return "";
        return ToolMastery.selected(t);
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        Player p = e.getEntity();
        if (p.level().isClientSide() || !(p instanceof ServerPlayer sp) || !(p.level() instanceof ServerLevel sl)) return;
        ItemStack tool = e.getItemStack();
        String m = mastery(tool);
        if (m.isEmpty()) return;
        int lv = MasteryLevel.of(tool);
        int r = lv >= 30 ? 2 : 1;
        if (m.equals("cultivator")) till(sl, sp, tool, e.getPos(), r);
        else if (m.equals("homesteader")) plant(sl, sp, tool, e.getPos(), r);
        else if (m.equals("reaper") || m.equals("harvest_sweep") || m.equals("replanter") || m.equals("green_thumb"))
            harvest(sl, sp, tool, m, lv);
    }

    private static void harvest(ServerLevel l, ServerPlayer p, ItemStack tool, String m, int lv) {
        int r = m.equals("reaper") ? (lv >= 30 ? 4 : lv >= 20 ? 3 : 2) : (lv >= 30 ? 3 : lv >= 20 ? 2 : 1);
        var area = MasteryAoe.square(l, p, r, MasteryAoe::isMatureCrop);
        MasteryAoe.harvestCrops(l, p, tool, area, m.equals("replanter"), m.equals("green_thumb"));
    }

    private static void till(ServerLevel l, ServerPlayer p, ItemStack tool, BlockPos c, int r) {
        boolean any = false;
        for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) {
            BlockPos pos = c.offset(dx, 0, dz);
            var s = l.getBlockState(pos);
            boolean tillable = s.is(Blocks.GRASS_BLOCK) || s.is(Blocks.DIRT) || s.is(Blocks.DIRT_PATH)
                    || s.is(Blocks.COARSE_DIRT) || s.is(Blocks.ROOTED_DIRT);
            if (tillable && l.getBlockState(pos.above()).isAir()) {
                l.setBlock(pos, Blocks.FARMLAND.defaultBlockState(), 3);
                any = true;
            }
        }
        if (any) {
            l.playSound(null, c, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1f, 1f);
            tool.hurtAndBreak(1, l, p, item -> {});
        }
    }

    private static void plant(ServerLevel l, ServerPlayer p, ItemStack tool, BlockPos c, int r) {
        for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) {
            BlockPos farm = c.offset(dx, 0, dz), above = farm.above();
            if (l.getBlockState(farm).is(Blocks.FARMLAND) && l.getBlockState(above).isAir()) {
                Block crop = takeSeed(p);
                if (crop != null) l.setBlock(above, crop.defaultBlockState(), 3);
            }
        }
    }

    private static Block takeSeed(ServerPlayer p) {
        var inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            Block crop = null;
            if (s.is(Items.WHEAT_SEEDS)) crop = Blocks.WHEAT;
            else if (s.is(Items.CARROT)) crop = Blocks.CARROTS;
            else if (s.is(Items.POTATO)) crop = Blocks.POTATOES;
            else if (s.is(Items.BEETROOT_SEEDS)) crop = Blocks.BEETROOTS;
            if (crop != null) {
                if (!p.getAbilities().instabuild) s.shrink(1);
                return crop;
            }
        }
        return null;
    }
}