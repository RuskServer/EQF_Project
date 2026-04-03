package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import com.lunar_prototype.eqf.execution.ActionExecutor;
import com.lunar_prototype.eqf.module.ActionRegistry;
import com.lunar_prototype.eqf.module.ConditionExpression;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/ChooseAction.class */
public class ChooseAction implements EQFAction {
    private final String condition;
    private final List<EQFAction> thenActions;
    private final List<EQFAction> elseActions;

    public ChooseAction(String condition, List<EQFAction> thenActions, List<EQFAction> elseActions) {
        this.condition = condition;
        this.thenActions = thenActions;
        this.elseActions = elseActions;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        ConditionExpression evaluator = new ConditionExpression(this.condition);
        boolean result = evaluator.evaluate(context.getPlayer(), EQFPlugin.getInstance().getQuestManager());
        if (result) {
            return ActionExecutor.executeSequence(this.thenActions, context);
        }
        return ActionExecutor.executeSequence(this.elseActions, context);
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/ChooseAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            String condition = (String) params.get("if");
            if (condition == null) {
                throw new IllegalArgumentException("Choose action requires an 'if' condition.");
            }
            List<EQFAction> thenActions = parseBranch(params.get("then"));
            List<EQFAction> elseActions = parseBranch(params.get("else"));
            return new ChooseAction(condition, thenActions, elseActions);
        }

        private List<EQFAction> parseBranch(Object branchData) {
            if (branchData == null) {
                return Collections.emptyList();
            }
            if (branchData instanceof String) {
                return Collections.singletonList(new NextAction((String) branchData));
            }
            if (branchData instanceof List) {
                return ActionRegistry.parseActionList((List) branchData);
            }
            return Collections.emptyList();
        }
    }
}
