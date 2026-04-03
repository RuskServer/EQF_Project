package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import java.util.Map;
import org.bukkit.event.player.PlayerInteractEvent;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/InteractTrigger.class */
public class InteractTrigger implements EQFTrigger<PlayerInteractEvent> {
    private final String targetIdentifier;

    public InteractTrigger(String targetIdentifier) {
        this.targetIdentifier = targetIdentifier.toLowerCase();
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public Class<PlayerInteractEvent> getEventClass() {
        return PlayerInteractEvent.class;
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public boolean check(PlayerQuestState state, PlayerInteractEvent event) {
        if (this.targetIdentifier.equals("any")) {
            return true;
        }
        return false;
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/InteractTrigger$Factory.class */
    public static class Factory implements EQFTriggerFactory {
        @Override // com.lunar_prototype.eqf.api.EQFTriggerFactory
        public EQFTrigger<?> create(Map<String, Object> params) {
            String target = (String) params.getOrDefault("target", "any");
            return new InteractTrigger(target);
        }
    }
}
