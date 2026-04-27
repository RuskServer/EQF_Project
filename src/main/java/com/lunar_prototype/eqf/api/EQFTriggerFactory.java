package com.lunar_prototype.eqf.api;

import java.util.Map;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/api/EQFTriggerFactory.class */
public interface EQFTriggerFactory {
    EQFTrigger<?> create(Map<String, Object> map);

    default ValidationResult validate(Map<String, Object> params) {
        return new ValidationResult();
    }
}
