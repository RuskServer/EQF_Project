package com.lunar_prototype.eqf.gui;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/gui/MainMenuGui.class */
public class MainMenuGui extends EQFGui {
    public MainMenuGui(Player player) {
        super(player, 27, "EQF Main Menu");
        this.inventory.setItem(11, new ItemBuilder(Material.PLAYER_HEAD).name(String.valueOf(ChatColor.AQUA) + "プレイヤー管理").lore(String.valueOf(ChatColor.GRAY) + "オンラインプレイヤーのクエスト状態を確認・編集します").build());
        this.inventory.setItem(15, new ItemBuilder(Material.REDSTONE_BLOCK).name(String.valueOf(ChatColor.RED) + "リロード").lore(String.valueOf(ChatColor.GRAY) + "設定とクエスト定義を再読み込みします").build());
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 27; i++) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, filler);
            }
        }
    }

    @Override // com.lunar_prototype.eqf.gui.EQFGui
    public void handleClick(Player clicker, ItemStack clickedItem, int slot, boolean isShiftClick, boolean isRightClick) {
        if (clickedItem.getType() == Material.PLAYER_HEAD && slot == 11) {
            new PlayerSelectionGui(clicker).open();
        } else if (clickedItem.getType() == Material.REDSTONE_BLOCK && slot == 15) {
            clicker.closeInventory();
            clicker.sendMessage(String.valueOf(ChatColor.YELLOW) + "[EQF] システムをリロードしています...");
            Bukkit.getScheduler().runTask(EQFPlugin.getInstance(), () -> {
                Bukkit.dispatchCommand(clicker, "eqf reload");
            });
        }
    }
}
