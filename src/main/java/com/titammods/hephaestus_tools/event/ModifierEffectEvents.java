package com.titammods.hephaestus_tools.event;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tools.item.ModifiableItem;
import com.titammods.hephaestus_tools.tools.modifier.ModifierEffects;
import com.titammods.hephaestus_tools.tools.nbt.ToolConstructionData;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ModifierEffectEvents {
    private ModifierEffectEvents() {}
    private static boolean sweeping = false;

    private static int modLevel(ItemStack tool, ResourceLocation id) {
        if (!(tool.getItem() instanceof ModifiableItem) || !ToolStack.isInitialized(tool)) return 0;
        for (ToolConstructionData.ModifierEntry e : ToolStack.getModifiers(tool)) if (e.id().equals(id)) return e.level();
        return 0;
    }

    @SubscribeEvent
    public static void onHit(LivingIncomingDamageEvent e) {
        if (!(e.getSource().getEntity() instanceof Player p)) return;
        ItemStack tool = p.getMainHandItem();
        LivingEntity target = e.getEntity();

        int flame = modLevel(tool, ModifierEffects.FLAME);
        if (flame > 0) target.igniteForSeconds(3 * flame);

        int sweep = modLevel(tool, ModifierEffects.SWEEPING);
        if (sweep > 0 && !sweeping && p.level() instanceof ServerLevel) {
            sweeping = true;
            try {
                double r = 1.5 + sweep * 0.5; float frac = 0.25f * sweep;
                DamageSource src = p.damageSources().playerAttack(p);
                for (LivingEntity m : p.level().getEntitiesOfClass(LivingEntity.class, new AABB(target.blockPosition()).inflate(r)))
                    if (m != target && m != p && m.isAlive()) m.hurt(src, e.getAmount() * frac);
            } finally { sweeping = false; }
        }
    }

    @SubscribeEvent
    public static void onDrops(BlockDropsEvent e) {
        if (modLevel(e.getTool(), ModifierEffects.SILK_TOUCH) <= 0) return;
        var block = e.getState().getBlock();
        if (block.asItem() == net.minecraft.world.item.Items.AIR) return;
        e.getDrops().clear();
        ServerLevel l = e.getLevel();
        var pos = e.getPos();
        e.getDrops().add(new ItemEntity(l, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(block)));
    }
}