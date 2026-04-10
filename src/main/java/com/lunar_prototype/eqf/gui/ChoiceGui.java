package com.lunar_prototype.eqf.gui;

import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.execution.ActionExecutor;
import com.lunar_prototype.eqf.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChoiceGui extends EQFGui {
    private final Map<String, List<EQFAction>> options;
    private final ActionContext context;
    private final CompletableFuture<ActionResult> future;
    private boolean selected = false;

    public ChoiceGui(Player player, String title, Map<String, List<EQFAction>> options, ActionContext context, CompletableFuture<ActionResult> future) {
        super(player, 27, title);
        this.options = options;
        this.context = context;
        this.future = future;
        setupItems();
    }

    public boolean isSelected() {
        return selected;
    }

    private void setupItems() {
        int slot = 10; // 中央付近から配置開始
        for (String optionName : options.keySet()) {
            ItemStack item = new ItemBuilder(Material.PAPER)
                    .name("§e" + optionName)
                    .lore("§7クリックして選択")
                    .build();
            this.inventory.setItem(slot, item);
            slot += 2; // 間隔を空ける
            if (slot > 16) break; // 簡易的に最大4つ程度まで
        }
    }

    @Override
    public void handleClick(Player player, ItemStack item, int slot, boolean shift, boolean right) {
        if (item == null || item.getType() == Material.AIR) return;
        
        String displayName = item.getItemMeta().getDisplayName().replace("§e", "");
        List<EQFAction> actions = options.get(displayName);
        
        if (actions != null) {
            selected = true;
            player.closeInventory();
            // 選択されたアクションを実行し、完了したらfutureをcompleteさせる
            ActionExecutor.executeSequence(actions, context).thenAccept(result -> {
                future.complete(result);
            });
        }
    }
}
