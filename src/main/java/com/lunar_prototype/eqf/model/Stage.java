package com.lunar_prototype.eqf.model;

import java.util.List;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/model/Stage.class */
public class Stage {
    private String id;
    private String description;
    private List<TriggerData> triggers;
    private List<String> conditions;
    private List<ActionData> actions;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TriggerData> getTriggers() {
        return this.triggers;
    }

    public void setTriggers(List<TriggerData> triggers) {
        this.triggers = triggers;
    }

    public List<ActionData> getActions() {
        return this.actions;
    }

    public void setActions(List<ActionData> actions) {
        this.actions = actions;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getConditions() {
        return this.conditions;
    }
}
