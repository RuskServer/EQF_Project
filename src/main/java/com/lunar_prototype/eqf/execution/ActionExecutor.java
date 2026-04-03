package com.lunar_prototype.eqf.execution;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.model.ActionData;
import com.lunar_prototype.eqf.model.Quest;
import com.lunar_prototype.eqf.module.ActionRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/execution/ActionExecutor.class */
public class ActionExecutor {
    public static CompletableFuture<ActionResult> executeActions(Player player, Quest quest, List<ActionData> actionDatas) {
        if (actionDatas == null || actionDatas.isEmpty()) {
            return CompletableFuture.completedFuture(ActionResult.SUCCESS);
        }
        ActionContext context = new ActionContext(player, quest);
        List<EQFAction> actions = new ArrayList<>();
        for (ActionData data : actionDatas) {
            try {
                actions.add(ActionRegistry.createAction(data));
            } catch (Exception e) {
                EQFPlugin.getInstance().getLogger().warning("Failed to create action: " + data.type);
            }
        }
        executeSequence(actions, context);
        return executeSequence(actions, context);
    }

    public static CompletableFuture<ActionResult> executeSequence(List<EQFAction> actions, ActionContext context) {
        CompletableFuture<ActionResult> currentFuture = CompletableFuture.completedFuture(ActionResult.SUCCESS);
        for (EQFAction action : actions) {
            currentFuture = currentFuture.thenCompose(result -> {
                if (result == ActionResult.FAILURE) {
                    return CompletableFuture.completedFuture(ActionResult.FAILURE);
                }
                try {
                    return action.execute(context);
                } catch (Exception e) {
                    e.printStackTrace();
                    return CompletableFuture.completedFuture(ActionResult.FAILURE);
                }
            });
        }
        return currentFuture;
    }
}
