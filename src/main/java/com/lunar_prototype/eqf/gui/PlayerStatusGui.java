package com.lunar_prototype.eqf.gui;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import com.lunar_prototype.eqf.execution.QuestManager;
import com.lunar_prototype.eqf.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/gui/PlayerStatusGui.class */
public class PlayerStatusGui extends EQFGui {
    private final Player targetPlayer;
    private final List<PlayerQuestState> stateList;
    private final QuestManager questManager;

    public PlayerStatusGui(Player viewer, Player targetPlayer) {
        super(viewer, 54, "Status: " + targetPlayer.getName());
        this.targetPlayer = targetPlayer;
        this.questManager = EQFPlugin.getInstance().getQuestManager();
        Collection<PlayerQuestState> states = this.questManager.getPlayerAllStates(targetPlayer.getUniqueId());
        this.stateList = new ArrayList(states);
        int slot = 0;
        for (PlayerQuestState state : this.stateList) {
            if (slot >= 45) {
                break;
            }
            Material icon = state.isStarted() ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            String statusText = state.isStarted() ? "§a進行中" : "§c完了/未開始";
            int i = slot;
            slot++;
            this.inventory.setItem(i, new ItemBuilder(icon).name(String.valueOf(ChatColor.GOLD) + state.getQuestId()).lore(String.valueOf(ChatColor.GRAY) + "Status: " + statusText, String.valueOf(ChatColor.GRAY) + "Current Stage: " + String.valueOf(ChatColor.WHITE) + state.getCurrentStage(), String.valueOf(ChatColor.GRAY) + "Flags: " + state.getProgressData().toString(), "", String.valueOf(ChatColor.YELLOW) + "右クリック: " + String.valueOf(ChatColor.RED) + "強制完了", String.valueOf(ChatColor.YELLOW) + "Shift+左クリック: " + String.valueOf(ChatColor.RED) + "メモリからアンロード").build());
        }
        this.inventory.setItem(49, new ItemBuilder(Material.ARROW).name(String.valueOf(ChatColor.WHITE) + "戻る").build());
    }

    @Override // com.lunar_prototype.eqf.gui.EQFGui
    public void handleClick(Player clicker, ItemStack clickedItem, int slot, boolean isShiftClick, boolean isRightClick) {
        if (slot == 49) {
            new PlayerSelectionGui(clicker).open();
            return;
        }
        if (slot < this.stateList.size()) {
            PlayerQuestState state = this.stateList.get(slot);
            UUID targetUuid = this.targetPlayer.getUniqueId();
            if (isShiftClick && !isRightClick) {
                this.questManager.unloadPlayerStates(targetUuid);
                clicker.sendMessage(String.valueOf(ChatColor.GREEN) + "[EQF] " + this.targetPlayer.getName() + " のデータを保存してアンロードしました。");
                clicker.closeInventory();
            } else if (isRightClick) {
                state.setStarted(false);
                state.setCurrentStage("COMPLETED_ADMIN");
                this.questManager.savePlayerStates(targetUuid);
                clicker.sendMessage(String.valueOf(ChatColor.GOLD) + "[EQF] " + state.getQuestId() + " を強制完了扱いにしました。");
                new PlayerStatusGui(clicker, this.targetPlayer).open();
            }
        }
    }
}
