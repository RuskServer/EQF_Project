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

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/NextAction.class */
public class NextAction implements EQFAction {
    private final String nextStageId;
    private final QuestManager questManager = EQFPlugin.getInstance().getQuestManager();

    public NextAction(String nextStageId) {
        this.nextStageId = nextStageId;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        PlayerQuestState state = this.questManager.getPlayerState(context.getPlayer(), context.getQuest().getId());
        if (!context.getQuest().getStages().containsKey(this.nextStageId)) {
            EQFPlugin.getInstance().getLogger().warning("Quest " + context.getQuest().getId() + " has no stage named " + this.nextStageId);
            return CompletableFuture.completedFuture(ActionResult.FAILURE);
        }
        state.setCurrentStage(this.nextStageId);
        this.questManager.savePlayerStates(context.getPlayer().getUniqueId());
        context.getPlayer().sendMessage("§6[EQF] ステージが「" + this.nextStageId + "」に遷移しました。");
        return CompletableFuture.completedFuture(ActionResult.SUCCESS);
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/NextAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            Object value = params.get("value");
            if (value == null) {
                throw new IllegalArgumentException("Next action requires a stage ID.");
            }
            return new NextAction(String.valueOf(value));
        }
    }
}
