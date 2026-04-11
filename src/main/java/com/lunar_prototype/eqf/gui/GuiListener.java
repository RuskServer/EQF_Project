package com.lunar_prototype.eqf.gui;

import com.lunar_prototype.eqf.EQFPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof ChoiceGui) {
            ChoiceGui choiceGui = (ChoiceGui) holder;
            if (!choiceGui.isSelected() && event.getPlayer() instanceof Player) {
                Player player = (Player) event.getPlayer();
                // 1ティック待機して、Minecraftのコンテナクローズ処理が完全に完了してから再度開く
                Bukkit.getScheduler().runTaskLater(EQFPlugin.getInstance(), () -> {
                    if (player.isOnline() && !choiceGui.isSelected()) {
                        // 既に別のインベントリが開いている可能性も考慮し、
                        // 明示的に再度開かせる
                        choiceGui.open();
                    }
                }, 1L);
            }
        }
    }
}
