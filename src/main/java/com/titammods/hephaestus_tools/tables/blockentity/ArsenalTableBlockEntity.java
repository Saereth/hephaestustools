package com.titammods.hephaestus_tools.tables.blockentity;

import com.titammods.hephaestus_tools.registry.ModBlocks;
import com.titammods.hephaestus_tools.tables.menu.ArsenalTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class ArsenalTableBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler upgradeSlot = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); syncToClient(); }
        @Override public int getSlotLimit(int slot) { return 1; }
    };
    private final ItemStackHandler blueprintSlot = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); syncToClient(); }
        @Override public int getSlotLimit(int slot) { return 1; }
    };
    private final ItemStackHandler inputSlots = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged(); syncToClient(); }
        @Override public int getSlotLimit(int slot) { return 1; }
    };
    private final ItemStackHandler outputSlot = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); syncToClient(); }
    };

    public ArsenalTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ARSENAL_TABLE_BE.get(), pos, state);
    }

    public ItemStackHandler getUpgradeSlot()   { return upgradeSlot; }
    public ItemStackHandler getBlueprintSlot() { return blueprintSlot; }
    public ItemStack getUpgradeItem()          { return upgradeSlot.getStackInSlot(0); }
    public ItemStackHandler getInputSlots()    { return inputSlots; }
    public ItemStackHandler getOutputSlot()    { return outputSlot; }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        SimpleContainer c = new SimpleContainer(7);
        c.setItem(0, upgradeSlot.getStackInSlot(0));
        c.setItem(1, blueprintSlot.getStackInSlot(0));
        for (int i = 0; i < 4; i++) c.setItem(2 + i, inputSlots.getStackInSlot(i));
        c.setItem(6, outputSlot.getStackInSlot(0));
        Containers.dropContents(level, pos, c);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.hephaestus_tools.arsenal_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return new ArsenalTableMenu(containerId, inv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Upgrade", upgradeSlot.serializeNBT(registries));
        tag.put("Blueprint", blueprintSlot.serializeNBT(registries));
        tag.put("Inputs", inputSlots.serializeNBT(registries));
        tag.put("Output", outputSlot.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Upgrade")) upgradeSlot.deserializeNBT(registries, tag.getCompound("Upgrade"));
        if (tag.contains("Blueprint")) blueprintSlot.deserializeNBT(registries, tag.getCompound("Blueprint"));
        if (tag.contains("Inputs")) inputSlots.deserializeNBT(registries, tag.getCompound("Inputs"));
        if (tag.contains("Output")) outputSlot.deserializeNBT(registries, tag.getCompound("Output"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("Upgrade", upgradeSlot.serializeNBT(registries));
        tag.put("Blueprint", blueprintSlot.serializeNBT(registries));
        tag.put("Inputs", inputSlots.serializeNBT(registries));
        tag.put("Output", outputSlot.serializeNBT(registries));
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}