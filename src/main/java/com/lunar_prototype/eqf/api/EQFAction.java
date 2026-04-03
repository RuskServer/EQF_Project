package com.lunar_prototype.eqf.api;

import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/api/EQFAction.class */
public interface EQFAction {
    CompletableFuture<ActionResult> execute(ActionContext actionContext);
}
