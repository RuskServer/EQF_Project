package com.lunar_prototype.eqf.module;

import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import java.util.Map;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/KillTrigger.class */
public class KillTrigger implements EQFTrigger<EntityDeathEvent> {
    private final EntityType targetType;
    private final int requiredAmount;
    private final String progressKey;

    public KillTrigger(EntityType targetType, int requiredAmount) {
        this.targetType = targetType;
        this.requiredAmount = requiredAmount;
        this.progressKey = "kill_" + targetType.name().toLowerCase();
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public Class<EntityDeathEvent> getEventClass() {
        return EntityDeathEvent.class;
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public boolean check(PlayerQuestState state, EntityDeathEvent event) {
        Player killer;
        if (event.getEntityType() != this.targetType || (killer = event.getEntity().getKiller()) == null || !killer.getUniqueId().equals(state.getPlayerUuid())) {
            return false;
        }
        Map<String, Object> progress = state.getProgressData();
        int currentCount = (int) ((Number) progress.getOrDefault(this.progressKey, 0)).longValue();
        int newCount = currentCount + 1;
        progress.put(this.progressKey, Integer.valueOf(newCount));
        return newCount >= this.requiredAmount;
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/KillTrigger$Factory.class */
    public static class Factory implements EQFTriggerFactory {
        @Override // com.lunar_prototype.eqf.api.EQFTriggerFactory
        public EQFTrigger<?> create(Map<String, Object> params) {
            String mobName = (String) params.get("mob");
            int amount = (params.containsKey("amount") && (params.get("amount") instanceof Number)) ? ((Number) params.get("amount")).intValue() : 1;
            try {
                EntityType type = EntityType.valueOf(mobName.toUpperCase());
                return new KillTrigger(type, amount);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid mob type for KillTrigger: " + mobName);
            }
        }
    }
}
