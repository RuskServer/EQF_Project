package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/MessageAction.class */
public class MessageAction implements EQFAction {
    private final String message;

    public MessageAction(String message) {
        this.message = message;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        context.getPlayer().getServer().getScheduler().runTask(EQFPlugin.getInstance(), () -> {
            context.getPlayer().sendMessage("§a[QUEST] §f" + this.message);
        });
        return CompletableFuture.completedFuture(ActionResult.SUCCESS);
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/MessageAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            Object value = params.get("value");
            if (value == null) {
                throw new IllegalArgumentException("Message action requires a 'value' parameter.");
            }
            return new MessageAction(String.valueOf(value));
        }
    }
}
