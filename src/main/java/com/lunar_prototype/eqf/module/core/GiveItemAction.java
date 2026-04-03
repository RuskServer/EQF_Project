package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/GiveItemAction.class */
public class GiveItemAction implements EQFAction {
    private final Material material;
    private final int amount;

    public GiveItemAction(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        ItemStack item = new ItemStack(this.material, this.amount);
        context.getPlayer().getServer().getScheduler().runTask(EQFPlugin.getInstance(), () -> {
            Map<Integer, ItemStack> overflow = context.getPlayer().getInventory().addItem(new ItemStack[]{item});
            if (!overflow.isEmpty()) {
                overflow.values().forEach(i -> {
                    context.getPlayer().getWorld().dropItem(context.getPlayer().getLocation(), i);
                });
            }
        });
        context.getPlayer().sendMessage("§b[EQF] " + this.material.name() + "を" + this.amount + "個手に入れました。");
        return CompletableFuture.completedFuture(ActionResult.SUCCESS);
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/GiveItemAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            Material material = null;
            int amount = 1;
            if (params.containsKey("value")) {
                String fullId = String.valueOf(params.get("value"));
                String[] parts = fullId.split(":");
                try {
                    material = Material.valueOf(parts[0].toUpperCase());
                    if (parts.length > 1) {
                        amount = Integer.parseInt(parts[1]);
                    }
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid item format in DSL: " + fullId);
                }
            } else if (params.containsKey("item")) {
                try {
                    material = Material.valueOf(String.valueOf(params.get("item")).toUpperCase());
                    if (params.containsKey("amount") && (params.get("amount") instanceof Number)) {
                        amount = ((Number) params.get("amount")).intValue();
                    }
                } catch (IllegalArgumentException e2) {
                    throw new IllegalArgumentException("Invalid item material: " + String.valueOf(params.get("item")));
                }
            }
            if (material == null) {
                throw new IllegalArgumentException("GiveItem action requires a valid item material.");
            }
            return new GiveItemAction(material, amount);
        }
    }
}
