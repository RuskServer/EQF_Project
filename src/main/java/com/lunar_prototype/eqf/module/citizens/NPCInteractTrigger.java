package com.lunar_prototype.eqf.module.citizens;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import java.util.Map;
import net.citizensnpcs.api.event.NPCRightClickEvent;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/citizens/NPCInteractTrigger.class */
public class NPCInteractTrigger implements EQFTrigger<NPCRightClickEvent> {
    private final int targetNpcId;

    public NPCInteractTrigger(int targetNpcId) {
        this.targetNpcId = targetNpcId;
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public Class<NPCRightClickEvent> getEventClass() {
        return NPCRightClickEvent.class;
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public boolean check(PlayerQuestState state, NPCRightClickEvent event) {
        EQFPlugin.getInstance().getLogger().info(String.format("[EQF Debug: NPCInteract] Checking quest %s for player %s. Event NPC ID: %d, Target ID: %d", state.getQuestId(), event.getClicker().getName(), Integer.valueOf(event.getNPC().getId()), Integer.valueOf(this.targetNpcId)));
        if (!event.getClicker().getUniqueId().equals(state.getPlayerUuid())) {
            return false;
        }
        if (this.targetNpcId == -1) {
            return true;
        }
        boolean isMatch = event.getNPC().getId() == this.targetNpcId;
        if (isMatch) {
            EQFPlugin.getInstance().getLogger().info(String.format("[EQF Debug: NPCInteract] Quest %s Trigger MATCHED for NPC ID %d.", state.getQuestId(), Integer.valueOf(this.targetNpcId)));
        }
        return isMatch;
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/citizens/NPCInteractTrigger$Factory.class */
    public static class Factory implements EQFTriggerFactory {
        @Override // com.lunar_prototype.eqf.api.EQFTriggerFactory
        public EQFTrigger<?> create(Map<String, Object> params) {
            Object targetObj = params.get("target");
            int targetId = -1;
            if (targetObj != null) {
                if (targetObj instanceof Number) {
                    targetId = ((Number) targetObj).intValue();
                } else if (targetObj instanceof String) {
                    String targetStr = (String) targetObj;
                    if ("any".equalsIgnoreCase(targetStr)) {
                        targetId = -1;
                    } else {
                        try {
                            targetId = Integer.parseInt(targetStr);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("NPCInteract 'target' must be a number or 'any'.", e);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("NPCInteract 'target' parameter is of an unexpected type: " + targetObj.getClass().getSimpleName());
                }
            }
            return new NPCInteractTrigger(targetId);
        }
    }
}
