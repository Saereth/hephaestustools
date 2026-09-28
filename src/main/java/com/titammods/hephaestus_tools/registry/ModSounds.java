package com.titammods.hephaestus_tools.registry;

import com.titammods.hephaestus_tools.HephaestusTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, HephaestusTools.MOD_ID);

    public static final Supplier<SoundEvent> ARSENAL_TABLE_OPEN  = register("arsenal_table_open");
    public static final Supplier<SoundEvent> ARSENAL_TABLE_CLOSE = register("arsenal_table_close");
    public static final Supplier<SoundEvent> ARSENAL_TABLE_CRAFT = register("arsenal_table_craft");
    public static final Supplier<SoundEvent> ARSENAL_TABLE_CRAFT_HOLD = register("arsenal_table_craft_hold");

    private static Supplier<SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HephaestusTools.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}