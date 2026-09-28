package com.titammods.hephaestus_tools.event;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.table.ToolRole;
import com.titammods.hephaestus_tools.table.ToolUpgrades;
import com.titammods.hephaestus_tools.table.ToolXp;
import com.titammods.hephaestus_tools.tools.item.ModifiableItem;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Set;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ToolXpEvents {
    private ToolXpEvents() {}

    private static ItemStack tool(ServerPlayer p) {
        ItemStack t = p.getMainHandItem();
        return (t.getItem() instanceof ModifiableItem && ToolStack.isInitialized(t)) ? t : ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent e) {
        if (!(e.getPlayer() instanceof ServerPlayer p)) return;
        ItemStack tool = tool(p);
        if (tool.isEmpty()) return;
        Set<ToolRole> roles = ToolUpgrades.rolesOf(tool.getItem());
        BlockState st = e.getState();
        int xp = 0;
        if (roles.contains(ToolRole.MINING)) {
            if (st.is(Tags.Blocks.ORES)) xp = 5;
            else if (st.is(BlockTags.LOGS)) xp = 3;
            else if (tool.isCorrectToolForDrops(st)) xp = 2;
        }
        if (xp == 0 && roles.contains(ToolRole.HARVEST) && st.is(BlockTags.CROPS)) xp = 2;
        if (xp > 0) ToolXp.addXp(tool, p, xp);
    }

    @SubscribeEvent
    public static void onHit(LivingIncomingDamageEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer p)) return;
        ItemStack tool = tool(p);
        if (tool.isEmpty() || !ToolUpgrades.rolesOf(tool.getItem()).contains(ToolRole.COMBAT)) return;
        ToolXp.addXp(tool, p, Math.max(1, (int) (e.getAmount() / 3f)));
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer p)) return;
        ItemStack tool = tool(p);
        if (tool.isEmpty() || !ToolUpgrades.rolesOf(tool.getItem()).contains(ToolRole.COMBAT)) return;
        ToolXp.addXp(tool, p, 5);
    }
}