package com.lunar_prototype.eqf.model;

import java.util.Map;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/model/TriggerData.class */
public class TriggerData {
    public String type;
    public Map<String, Object> params;

    public TriggerData() {
    }

    public TriggerData(String type, Map<String, Object> params) {
        this.type = type;
        this.params = params;
    }
}
