package com.lunar_prototype.eqf.model;

import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/model/Quest.class */
public class Quest {
    private String id;
    private int version;
    private String displayName;
    private String category;
    private Map<String, Object> metadata;
    private List<TriggerData> startTriggers;
    private Map<String, Stage> stages;
    private String initialStage;
    private boolean repeatable = true;

    public String getInitialStage() {
        return this.initialStage;
    }

    public void setInitialStage(String initialStage) {
        this.initialStage = initialStage;
    }

    public Quest(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, Stage> getStages() {
        return this.stages;
    }

    public void setStages(Map<String, Stage> stages) {
        this.stages = stages;
    }

    public List<TriggerData> getStartTriggers() {
        return this.startTriggers;
    }

    public void setStartTriggers(List<TriggerData> startTriggers) {
        this.startTriggers = startTriggers;
    }

    public void setVersion(Integer version) {
        this.version = version.intValue();
    }

    public Integer getVersion() {
        return Integer.valueOf(this.version);
    }

    public boolean isRepeatable() {
        return this.repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }
}
