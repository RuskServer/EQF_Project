package com.lunar_prototype.eqf.execution;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import com.lunar_prototype.eqf.model.Quest;
import com.lunar_prototype.eqf.persistence.PersistenceAdapter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/execution/QuestManager.class */
public class QuestManager {
    private final EQFPlugin plugin;
    private final PersistenceAdapter persistenceAdapter;
    private final Map<String, Quest> questDefinitions = new HashMap();
    private final Map<UUID, Map<String, PlayerQuestState>> playerStates = new HashMap();

    public QuestManager(EQFPlugin plugin, PersistenceAdapter adapter) {
        this.plugin = plugin;
        this.persistenceAdapter = adapter;
    }

    public void registerQuest(Quest quest) {
        this.questDefinitions.put(quest.getId(), quest);
    }

    public PlayerQuestState getPlayerState(Player player, String questId) {
        UUID playerUuid = player.getUniqueId();
        this.playerStates.putIfAbsent(playerUuid, new HashMap());
        Map<String, PlayerQuestState> playerQuestMap = this.playerStates.get(playerUuid);
        if (!playerQuestMap.containsKey(questId)) {
            PlayerQuestState loadedState = this.persistenceAdapter.loadState(playerUuid, questId).orElseGet(() -> {
                return new PlayerQuestState(playerUuid, questId);
            });
            playerQuestMap.put(questId, loadedState);
            return loadedState;
        }
        return playerQuestMap.get(questId);
    }

    public Collection<PlayerQuestState> getPlayerAllStates(UUID playerUuid) {
        Map<String, PlayerQuestState> questMap = this.playerStates.get(playerUuid);
        return questMap != null ? questMap.values() : Collections.emptyList();
    }

    public void savePlayerStates(UUID playerUuid) {
        savePlayerStates(playerUuid, true);
    }

    public void savePlayerStates(UUID playerUuid, boolean async) {
        if (async && this.plugin.isEnabled()) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                savePlayerStatesInternal(playerUuid);
            });
        } else {
            savePlayerStatesInternal(playerUuid);
        }
    }

    private void savePlayerStatesInternal(UUID playerUuid) {
        Collection<PlayerQuestState> statesToSave = getPlayerAllStates(playerUuid);
        if (!statesToSave.isEmpty()) {
            this.persistenceAdapter.saveAllStates(playerUuid, statesToSave);
            this.plugin.getLogger().info("Saved " + statesToSave.size() + " quest states for " + String.valueOf(playerUuid));
        }
    }

    public void unloadPlayerStates(UUID playerUuid) {
        savePlayerStates(playerUuid);
        this.playerStates.remove(playerUuid);
        this.plugin.getLogger().info("Unloaded quest states for " + String.valueOf(playerUuid));
    }

    public void startQuest(Player player, String questId) {
        Quest quest = this.questDefinitions.get(questId);
        if (quest == null) {
            this.plugin.getLogger().warning("Attempted to start non-existent quest: " + questId);
            return;
        }
        PlayerQuestState state = getPlayerState(player, questId);
        if (state.isStarted()) {
            player.sendMessage("§e[EQF] Quests is already active.");
            return;
        }
        String firstStageId = null;
        if (quest.getInitialStage() != null && quest.getStages().containsKey(quest.getInitialStage())) {
            firstStageId = quest.getInitialStage();
        } else if (!quest.getStages().isEmpty()) {
            firstStageId = quest.getStages().keySet().iterator().next();
        }
        if (firstStageId == null) {
            this.plugin.getLogger().severe("Quest " + questId + " has no stages defined!");
            return;
        }
        state.setCurrentStage(firstStageId);
        state.setStarted(true);
        this.plugin.getLogger().info(player.getName() + " started quest: " + questId + " at stage: " + firstStageId);
        savePlayerStates(player.getUniqueId());
    }

    public Map<String, Quest> getQuestDefinitions() {
        return Collections.unmodifiableMap(this.questDefinitions);
    }
}
