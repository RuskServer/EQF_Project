package com.lunar_prototype.eqf.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/gui/EQFGui.class */
public abstract class EQFGui implements InventoryHolder {
    protected final Inventory inventory;
    protected final Player player;

    public abstract void handleClick(Player player, ItemStack itemStack, int i, boolean z, boolean z2);

    public EQFGui(Player player, int size, String title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public void open() {
        this.player.openInventory(this.inventory);
    }

    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }
}
