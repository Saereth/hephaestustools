package com.titammods.hephaestus_tools.tools.aoe;

import com.titammods.hephaestus_tools.HephaestusTools;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID)
public final class BlockSideHitHandler {

    private BlockSideHitHandler() {}

    private static final Map<UUID, Direction> HIT_FACE = new ConcurrentHashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            Direction face = event.getFace();
            if (face != null && !event.getEntity().level().isClientSide()) {
                HIT_FACE.put(event.getEntity().getUUID(), face);
            }
        }
    }

    public static Direction getSideHit(Player player) {
        return HIT_FACE.getOrDefault(player.getUUID(), Direction.UP);
    }
}