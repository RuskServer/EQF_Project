package com.lunar_prototype.eqf.gui;

import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.execution.ActionExecutor;
import com.lunar_prototype.eqf.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChoiceGui extends EQFGui {
    private final Map<String, List<EQFAction>> options;
    private final ActionContext context;
    private final CompletableFuture<ActionResult> future;
    private boolean selected = false;

    public ChoiceGui(Player player, String title, Map<String, List<EQFAction>> options, ActionContext context, CompletableFuture<ActionResult> future) {
        // 動的にサイズを決定 (オプション数に応じて調整)
        super(player, calculateSize(options.size()), title);
        this.options = options;
        this.context = context;
        this.future = future;
        setupItems();
    }

    private static int calculateSize(int optionCount) {
        if (optionCount <= 7) return 27;
        if (optionCount <= 14) return 36;
        if (optionCount <= 21) return 45;
        return 54;
    }

    public boolean isSelected() {
        return selected;
    }

    private void setupItems() {
        // 1. 背景を板ガラスで埋める (チカチカ防止と見た目の向上)
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, filler);
        }

        List<String> optionNames = new ArrayList<>(options.keySet());
        int count = optionNames.size();

        // 2. 動的な配置
        // 基本的に中央の行(インベントリサイズの中央付近)に配置する
        int[] slots = getCenteredSlots(count, this.inventory.getSize());

        for (int i = 0; i < Math.min(count, slots.length); i++) {
            String optionName = optionNames.get(i);
            ItemStack item = new ItemBuilder(Material.PAPER)
                    .name("§e" + optionName)
                    .lore("§7クリックして選択")
                    .build();
            this.inventory.setItem(slots[i], item);
        }
    }

    private int[] getCenteredSlots(int count, int inventorySize) {
        int middleStart = (inventorySize / 18) * 9; // 3行なら9, 4行なら9, 5行なら18...
        int center = middleStart + 4;

        switch (count) {
            case 1: return new int[]{center};
            case 2: return new int[]{center - 2, center + 2};
            case 3: return new int[]{center - 2, center, center + 2};
            case 4: return new int[]{center - 3, center - 1, center + 1, center + 3};
            case 5: return new int[]{center - 4, center - 2, center, center + 2, center + 4};
            case 6: return new int[]{center - 4, center - 2, center - 1, center + 1, center + 2, center + 4};
            case 7: return new int[]{center - 4, center - 3, center - 2, center, center + 2, center + 3, center + 4};
            default:
                // 8個以上は単純に並べる
                int[] fallback = new int[count];
                for (int i = 0; i < count; i++) {
                    int s = middleStart + (i % 9);
                    if (s >= inventorySize) break;
                    fallback[i] = s;
                }
                return fallback;
        }
    }

    @Override
    public void handleClick(Player player, ItemStack item, int slot, boolean shift, boolean right) {
        if (item == null || item.getType() != Material.PAPER) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        
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
