package com.lunar_prototype.eqf.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/api/PlayerQuestState.class */
public class PlayerQuestState {
    private final UUID playerUuid;
    private final String questId;
    private Map<String, Object> progressData = new HashMap();
    private boolean isExecuting = false;
    private String currentStage = null;
    private boolean isStarted = false;

    public boolean isExecuting() {
        return this.isExecuting;
    }

    public void setExecuting(boolean executing) {
        this.isExecuting = executing;
    }

    public PlayerQuestState(UUID playerUuid, String questId) {
        this.playerUuid = playerUuid;
        this.questId = questId;
    }

    public String getCurrentStage() {
        return this.currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public void setStarted(boolean started) {
        this.isStarted = started;
    }

    public String getQuestId() {
        return this.questId;
    }

    public Map<String, Object> getProgressData() {
        return this.progressData;
    }

    @NotNull
    public UUID getPlayerUuid() {
        return this.playerUuid;
    }
}
