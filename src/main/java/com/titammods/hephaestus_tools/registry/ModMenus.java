package com.titammods.hephaestus_tools.registry;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tables.blockentity.ArsenalTableBlockEntity;
import com.titammods.hephaestus_tools.tables.menu.ArsenalTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, HephaestusTools.MOD_ID);

    public static final Supplier<MenuType<ArsenalTableMenu>> ARSENAL_TABLE =
            MENUS.register("arsenal_table",
                    () -> IMenuTypeExtension.create((id, playerInv, buf) -> {
                        BlockPos pos = buf.readBlockPos();
                        BlockEntity be = playerInv.player.level().getBlockEntity(pos);
                        if (be instanceof ArsenalTableBlockEntity arsenal) {
                            return new ArsenalTableMenu(id, playerInv, arsenal);
                        }
                        return null;
                    })
            );
}
