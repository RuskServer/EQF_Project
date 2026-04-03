package com.lunar_prototype.eqf.gui;

import com.lunar_prototype.eqf.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/gui/PlayerSelectionGui.class */
public class PlayerSelectionGui extends EQFGui {
    private final List<Player> onlinePlayers;

    public PlayerSelectionGui(Player player) {
        super(player, 54, "Select Target Player");
        this.onlinePlayers = new ArrayList(Bukkit.getOnlinePlayers());
        int slot = 0;
        Iterator<Player> it = this.onlinePlayers.iterator();
        while (it.hasNext()) {
            OfflinePlayer offlinePlayer = (Player) it.next();
            if (slot >= 45) {
                break;
            }
            int i = slot;
            slot++;
            this.inventory.setItem(i, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(offlinePlayer).name(String.valueOf(ChatColor.YELLOW) + offlinePlayer.getName()).lore(String.valueOf(ChatColor.GRAY) + "クリックで詳細を表示").build());
        }
        this.inventory.setItem(49, new ItemBuilder(Material.ARROW).name(String.valueOf(ChatColor.WHITE) + "戻る").build());
    }

    @Override // com.lunar_prototype.eqf.gui.EQFGui
    public void handleClick(Player clicker, ItemStack clickedItem, int slot, boolean isShiftClick, boolean isRightClick) {
        if (clickedItem.getType() == Material.ARROW && slot == 49) {
            new MainMenuGui(clicker).open();
        } else if (slot < this.onlinePlayers.size() && clickedItem.getType() == Material.PLAYER_HEAD) {
            Player target = this.onlinePlayers.get(slot);
            new PlayerStatusGui(clicker, target).open();
        }
    }
}
