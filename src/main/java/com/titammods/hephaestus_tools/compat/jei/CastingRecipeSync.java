package com.titammods.hephaestus_tools.compat.jei;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.setup.ModRecipes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID)
public final class CastingRecipeSync {

    private CastingRecipeSync() {}

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(ModRecipes.CASTING_TABLE_TYPE.get());
    }
}
