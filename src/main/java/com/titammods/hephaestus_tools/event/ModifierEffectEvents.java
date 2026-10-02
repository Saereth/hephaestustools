package com.titammods.hephaestus_tools.event;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tools.modifier.ModifierEffects;
import com.titammods.hephaestus_tools.tools.nbt.ToolConstructionData;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import com.titammods.hephaestus_tools.tools.helper.ToolCombat;
import com.titammods.hephaestus_tools.tools.stat.ToolStats;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID)
public final class ModifierEffectEvents {

    private ModifierEffectEvents() {}

    private static int modLevel(ItemStack tool, Identifier id) {
        if (!ToolStack.isUsable(tool)) return 0;
        for (ToolConstructionData.ModifierEntry e : ToolStack.getModifiers(tool)) if (e.id().equals(id)) return e.level();
        return 0;
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post e) {
        if (!(e.getSource().getEntity() instanceof Player p)) return;
        if (e.getHealthDamage() <= 0) return;
        ItemStack tool = ToolCombat.tool(e.getEntity(), e.getSource());
        LivingEntity target = e.getEntity();

        int flame = modLevel(tool, ModifierEffects.FLAME);
        if (flame > 0) target.igniteForSeconds(3 * flame);

        int sweep = modLevel(tool, ModifierEffects.SWEEPING);
        if (sweep > 0 && p.level() instanceof ServerLevel serverLevel) {
            ToolCombat.secondary(() -> {
                double r = 1.5 + sweep * 0.5;
                float frac = 0.25f * sweep;
                DamageSource src = p.damageSources().playerAttack(p);
                for (LivingEntity m : serverLevel.getEntitiesOfClass(LivingEntity.class,
                        new AABB(target.blockPosition()).inflate(r)))
                    if (m != target && m != p && m.isAlive()) m.hurtServer(serverLevel, src, e.getOriginalDamage() * frac);
            });
        }
    }

    @SubscribeEvent
    public static void onEnchantments(GetEnchantmentLevelEvent event) {
        if (!(event.getStack() instanceof ItemStack tool)) return;
        if (event.isTargetting(Enchantments.SILK_TOUCH) && modLevel(tool, ModifierEffects.SILK_TOUCH) > 0) {
            event.getHolder(Enchantments.SILK_TOUCH).ifPresent(enchantment ->
                    event.getEnchantments().set(enchantment, Math.max(1, event.getEnchantments().getLevel(enchantment))));
        }
        if (event.isTargetting(Enchantments.LOOTING) && ToolStack.isUsable(tool)) {
            int looting = Math.max(0, (int) ToolStack.getProperties(tool).getStat(ToolStats.LOOTING, 0));
            // Use the stronger source instead of counting the same benefit twice.
            event.getHolder(Enchantments.LOOTING).ifPresent(enchantment ->
                    event.getEnchantments().set(enchantment, Math.max(looting, event.getEnchantments().getLevel(enchantment))));
        }
    }
}
