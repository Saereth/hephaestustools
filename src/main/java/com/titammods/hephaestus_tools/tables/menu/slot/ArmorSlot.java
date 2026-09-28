package com.titammods.hephaestus_tools.tables.menu.slot;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;

public class ArmorSlot extends Slot {

    private static final Identifier[] ARMOR_SLOT_BACKGROUNDS = new Identifier[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
    };

    private final Player player;
    private final EquipmentSlot slotType;

    public ArmorSlot(Inventory inv, EquipmentSlot slotType, int x, int y) {
        super(inv, 36 + slotType.getIndex(), x, y);
        this.player = inv.player;
        this.slotType = slotType;
        setBackground(ARMOR_SLOT_BACKGROUNDS[slotType.getIndex()]);
    }

    @Override
    public int getMaxStackSize() { return 1; }

    @Override
    public boolean mayPlace(ItemStack stack) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot() == slotType;
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack stack = this.getItem();
        return stack.isEmpty() || player.isCreative()
                || !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
    }
}