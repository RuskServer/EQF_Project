package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import com.lunar_prototype.eqf.execution.QuestManager;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/CompleteAction.class */
public class CompleteAction implements EQFAction {
    private final QuestManager questManager = EQFPlugin.getInstance().getQuestManager();
    private final boolean success;

    public CompleteAction(boolean success) {
        this.success = success;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        PlayerQuestState state = this.questManager.getPlayerState(context.getPlayer(), context.getQuest().getId());
        state.setStarted(false);
        state.setCurrentStage("COMPLETED");
        this.questManager.savePlayerStates(context.getPlayer().getUniqueId());
        context.getPlayer().sendMessage("§6§l[EQF] §eクエスト「" + context.getQuest().getDisplayName() + "」を完了しました！");
        return CompletableFuture.completedFuture(ActionResult.SUCCESS);
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/CompleteAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            boolean success = true;
            if (params.containsKey("value")) {
                success = Boolean.parseBoolean(String.valueOf(params.get("value")));
            }
            return new CompleteAction(success);
        }
    }
}
