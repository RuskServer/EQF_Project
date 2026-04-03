package com.lunar_prototype.eqf.module;

import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import com.lunar_prototype.eqf.model.ActionData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/ActionRegistry.class */
public class ActionRegistry {
    private static final Map<String, EQFActionFactory> FACTORIES = new HashMap();

    public static void registerAction(String id, EQFActionFactory factory) {
        if (FACTORIES.containsKey(id.toLowerCase())) {
            throw new IllegalArgumentException("Action ID already registered: " + id);
        }
        FACTORIES.put(id.toLowerCase(), factory);
    }

    public static EQFAction createAction(ActionData data) {
        String type = data.type.toLowerCase();
        EQFActionFactory factory = FACTORIES.get(type);
        if (factory == null) {
            throw new IllegalStateException("Unknown Action Type: " + type);
        }
        return factory.create(data.params);
    }

    public static List<EQFAction> parseActionList(List<Object> rawList) {
        List<EQFAction> actions = new ArrayList<>();
        if (rawList == null) {
            return actions;
        }
        for (Object obj : rawList) {
            ActionData data = null;
            if (obj instanceof Map) {
                Map<String, Object> map = (Map) obj;
                if (map.size() == 1) {
                    String type = map.keySet().iterator().next();
                    Object value = map.get(type);
                    Map<String, Object> params = new HashMap<>();
                    if (value instanceof Map) {
                        params.putAll((Map) value);
                    } else {
                        params.put("value", value);
                    }
                    data = new ActionData(type, params);
                } else if (map.containsKey("type")) {
                    String type2 = (String) map.get("type");
                    Map<String, Object> params2 = new HashMap<>(map);
                    params2.remove("type");
                    data = new ActionData(type2, params2);
                }
            } else if (obj instanceof String) {
                data = new ActionData((String) obj, new HashMap());
            }
            if (data != null) {
                try {
                    actions.add(createAction(data));
                } catch (Exception e) {
                    System.err.println("[EQF] Failed to parse nested action: " + data.type);
                    e.printStackTrace();
                }
            }
        }
        return actions;
    }
}
