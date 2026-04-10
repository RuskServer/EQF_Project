package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import com.lunar_prototype.eqf.gui.ChoiceGui;
import com.lunar_prototype.eqf.module.ActionRegistry;
import org.bukkit.scheduler.BukkitRunnable;
import com.lunar_prototype.eqf.EQFPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MultipleChoiceAction implements EQFAction {
    private final String title;
    private final Map<String, List<EQFAction>> options;

    public MultipleChoiceAction(String title, Map<String, List<EQFAction>> options) {
        this.title = title;
        this.options = options;
    }

    @Override
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        CompletableFuture<ActionResult> future = new CompletableFuture<>();
        
        // メインスレッドでGUIを開く
        new BukkitRunnable() {
            @Override
            public void run() {
                new ChoiceGui(context.getPlayer(), title, options, context, future).open();
            }
        }.runTask(EQFPlugin.getInstance());

        return future;
    }

    public static class Factory implements EQFActionFactory {
        @Override
        public EQFAction create(Map<String, Object> params) {
            String title = (String) params.getOrDefault("title", "選択してください");
            Map<String, List<EQFAction>> options = new HashMap<>();
            
            Object optionsObj = params.get("options");
            if (optionsObj instanceof Map) {
                Map<String, Object> rawOptions = (Map<String, Object>) optionsObj;
                for (Map.Entry<String, Object> entry : rawOptions.entrySet()) {
                    String optionName = entry.getKey();
                    Object actionsData = entry.getValue();
                    
                    List<EQFAction> actions;
                    if (actionsData instanceof Map && ((Map<?, ?>) actionsData).containsKey("actions")) {
                        actions = ActionRegistry.parseActionList((List<Object>) ((Map<?, ?>) actionsData).get("actions"));
                    } else if (actionsData instanceof List) {
                        actions = ActionRegistry.parseActionList((List<Object>) actionsData);
                    } else {
                        actions = java.util.Collections.emptyList();
                    }
                    options.put(optionName, actions);
                }
            }
            
            return new MultipleChoiceAction(title, options);
        }
    }
}
