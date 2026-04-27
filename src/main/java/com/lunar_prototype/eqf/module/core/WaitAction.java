package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/WaitAction.class */
public class WaitAction implements EQFAction {
    private final long delayTicks;

    public WaitAction(long delaySeconds) {
        this.delayTicks = delaySeconds * 20;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        CompletableFuture<ActionResult> future = new CompletableFuture<>();
        if (this.delayTicks <= 0) {
            return CompletableFuture.completedFuture(ActionResult.SUCCESS);
        }
        EQFPlugin.getInstance().getServer().getScheduler().runTaskLater(EQFPlugin.getInstance(), () -> {
            future.complete(ActionResult.SUCCESS);
        }, this.delayTicks);
        return future;
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/WaitAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            Object value = params.get("value");
            if (value == null) {
                throw new IllegalArgumentException("Wait action requires a delay value (seconds).");
            }
            try {
                long delay = value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value));
                return new WaitAction(delay);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Wait value must be a number.", e);
            }
        }

        @Override
        public com.lunar_prototype.eqf.api.ValidationResult validate(Map<String, Object> params) {
            com.lunar_prototype.eqf.api.ValidationResult result = new com.lunar_prototype.eqf.api.ValidationResult();
            if (!params.containsKey("value")) {
                result.addError("Wait action requires a 'value' (seconds).");
            } else {
                Object value = params.get("value");
                try {
                    if (value instanceof Number) {
                        if (((Number) value).doubleValue() < 0) {
                            result.addError("Wait delay cannot be negative.");
                        }
                    } else {
                        Double.parseDouble(String.valueOf(value));
                    }
                } catch (NumberFormatException e) {
                    result.addError("Wait value must be a number.");
                }
            }
            return result;
        }
    }
}
