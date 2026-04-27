package com.lunar_prototype.eqf.module;

import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.api.ValidationResult;
import com.lunar_prototype.eqf.model.TriggerData;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/TriggerRegistry.class */
public class TriggerRegistry {
    private static final Map<String, EQFTriggerFactory> FACTORIES = new HashMap();

    public static void registerTrigger(String id, EQFTriggerFactory factory) {
        if (FACTORIES.containsKey(id.toLowerCase())) {
            throw new IllegalArgumentException("Trigger ID already registered: " + id);
        }
        FACTORIES.put(id.toLowerCase(), factory);
    }

    public static ValidationResult validateTrigger(TriggerData data) {
        ValidationResult result = new ValidationResult();
        String type = data.type.toLowerCase();
        EQFTriggerFactory factory = FACTORIES.get(type);
        if (factory == null) {
            result.addError("Unknown Trigger Type: " + type);
            return result;
        }
        return factory.validate(data.params);
    }

    public static EQFTrigger<?> createTrigger(TriggerData data) {
        String type = data.type.toLowerCase();
        EQFTriggerFactory factory = FACTORIES.get(type);
        if (factory == null) {
            throw new IllegalStateException("Unknown Trigger Type: " + type);
        }
        return factory.create(data.params);
    }
}
