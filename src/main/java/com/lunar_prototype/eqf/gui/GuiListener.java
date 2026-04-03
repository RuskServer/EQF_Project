package com.lunar_prototype.eqf.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/gui/GuiListener.class */
public class GuiListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            InventoryHolder holder = event.getInventory().getHolder();
            if (holder instanceof EQFGui) {
                event.setCancelled(true);
                if (event.getCurrentItem() == null) {
                    return;
                }
                EQFGui gui = (EQFGui) holder;
                gui.handleClick((Player) event.getWhoClicked(), event.getCurrentItem(), event.getRawSlot(), event.isShiftClick(), event.isRightClick());
            }
        }
    }
}
