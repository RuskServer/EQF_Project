package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import com.lunar_prototype.eqf.module.ActionRegistry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/ParallelAction.class */
public class ParallelAction implements EQFAction {
    private final List<EQFAction> actions;

    public ParallelAction(List<EQFAction> actions) {
        this.actions = actions;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        if (this.actions.isEmpty()) {
            return CompletableFuture.completedFuture(ActionResult.SUCCESS);
        }
        List<CompletableFuture<ActionResult>> futures = (List) this.actions.stream().map(action -> {
            return action.execute(context);
        }).collect(Collectors.toList());
        return CompletableFuture.allOf((CompletableFuture[]) futures.toArray(new CompletableFuture[0])).thenApply(v -> {
            return ActionResult.SUCCESS;
        });
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/ParallelAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            Object value = params.get("value");
            if (!(value instanceof List)) {
                throw new IllegalArgumentException("Parallel action requires a list of actions.");
            }
            List<EQFAction> actions = ActionRegistry.parseActionList((List) value);
            return new ParallelAction(actions);
        }
    }
}
