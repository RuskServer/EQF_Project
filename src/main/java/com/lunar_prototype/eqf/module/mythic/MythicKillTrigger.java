package com.lunar_prototype.eqf.module.mythic;

import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import java.util.Map;
import org.bukkit.entity.Player;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/mythic/MythicKillTrigger.class */
public class MythicKillTrigger implements EQFTrigger<MythicMobDeathEvent> {
    private final String targetMobId;
    private final int requiredAmount;
    private final String progressKey;

    public MythicKillTrigger(String targetMobId, int requiredAmount) {
        this.targetMobId = targetMobId;
        this.requiredAmount = requiredAmount;
        this.progressKey = "mm_kill_" + targetMobId.toLowerCase();
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public Class<MythicMobDeathEvent> getEventClass() {
        return MythicMobDeathEvent.class;
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public boolean check(PlayerQuestState state, MythicMobDeathEvent event) {
        if (!event.getMob().getName().equalsIgnoreCase(this.targetMobId)) {
            return false;
        }
        if (!(event.getKiller() instanceof Player killer)) {
            return false;
        }
        if (!killer.getUniqueId().equals(state.getPlayerUuid())) {
            return false;
        }
        Map<String, Object> progress = state.getProgressData();
        int currentCount = ((Number) progress.getOrDefault(this.progressKey, 0)).intValue();
        int newCount = currentCount + 1;
        progress.put(this.progressKey, newCount);
        return newCount >= this.requiredAmount;
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/mythic/MythicKillTrigger$Factory.class */
    public static class Factory implements EQFTriggerFactory {
        @Override // com.lunar_prototype.eqf.api.EQFTriggerFactory
        public EQFTrigger<?> create(Map<String, Object> params) {
            String mobId = (String) params.get("mob");
            if (mobId == null) {
                throw new IllegalArgumentException("MythicKillTrigger requires 'mob' ID.");
            }
            int amount = (params.containsKey("amount") && (params.get("amount") instanceof Number)) ? ((Number) params.get("amount")).intValue() : 1;
            return new MythicKillTrigger(mobId, amount);
        }
    }
}
