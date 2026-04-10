package com.lunar_prototype.eqf.dsl;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.model.ActionData;
import com.lunar_prototype.eqf.model.Quest;
import com.lunar_prototype.eqf.model.Stage;
import com.lunar_prototype.eqf.model.TriggerData;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.yaml.snakeyaml.Yaml;

public class QuestLoader {
    private final EQFPlugin plugin;
    private final Path questDir;
    private final Yaml yaml = new Yaml();

    public QuestLoader(EQFPlugin plugin, Path questDir) {
        this.plugin = plugin;
        this.questDir = questDir;
    }

    public void loadAll() {
        if (!Files.exists(this.questDir)) {
            try {
                Files.createDirectories(this.questDir);
            } catch (Exception e) {
                this.plugin.getLogger().severe("Could not create quest directory: " + e.getMessage());
                return;
            }
        }
        File[] files = this.questDir.toFile().listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));
        if (files == null) return;
        
        for (File file : files) {
            try {
                Quest quest = loadQuest(file);
                this.plugin.getQuestManager().registerQuest(quest);
                this.plugin.getLogger().info("Loaded quest: " + quest.getId());
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to load quest file: " + file.getName(), e);
            }
        }
    }

    private Quest loadQuest(File file) throws Exception {
        InputStream is = new FileInputStream(file);
        try {
            Map<String, Object> data = (Map<String, Object>) this.yaml.load(is);
            if (data == null) throw new IllegalArgumentException("YAML data is empty");

            String id = (String) data.getOrDefault("QuestID", data.getOrDefault("id", file.getName().replace(".yml", "").replace(".yaml", "")));
            Quest quest = new Quest(id);
            
            String initialStage = (String) data.get("InitialStage");
            if (initialStage == null) initialStage = (String) data.get("initial_stage");
            if (initialStage == null) initialStage = (String) data.get("initialStage");
            quest.setInitialStage(initialStage);

            String displayName = (String) data.get("DisplayName");
            if (displayName == null) displayName = (String) data.get("title");
            quest.setDisplayName(displayName);

            quest.setVersion(((Number) data.getOrDefault("Version", data.getOrDefault("version", 1))).intValue());
            quest.setRepeatable(((Boolean) data.getOrDefault("Repeatable", data.getOrDefault("repeatable", true))).booleanValue());

            Map<String, Object> stagesRaw = (Map<String, Object>) data.get("Stages");
            if (stagesRaw == null) stagesRaw = (Map<String, Object>) data.get("stages");
            
            Map<String, Stage> stages = new LinkedHashMap<>();
            if (stagesRaw != null) {
                for (Map.Entry<String, Object> entry : stagesRaw.entrySet()) {
                    String stageId = entry.getKey();
                    Object val = entry.getValue();
                    if (val instanceof Map) {
                        stages.put(stageId, parseStage(stageId, (Map<String, Object>) val));
                    }
                }
            }
            quest.setStages(stages);

            Object triggersRaw = data.get("StartTriggers");
            if (triggersRaw == null) triggersRaw = data.get("start_triggers");
            if (triggersRaw == null) triggersRaw = data.get("triggers");
            
            quest.setStartTriggers(parseTriggers(triggersRaw));
            is.close();
            return quest;
        } catch (Throwable th) {
            try { is.close(); } catch (Throwable th2) { th.addSuppressed(th2); }
            throw th;
        }
    }

    private Stage parseStage(String id, Map<String, Object> data) {
        Stage stage = new Stage();
        stage.setId(id);
        stage.setDescription((String) data.getOrDefault("Description", data.get("description")));
        
        Object triggerObj = data.get("Triggers");
        if (triggerObj == null) triggerObj = data.get("triggers");
        if (triggerObj == null) triggerObj = data.get("trigger");
        
        stage.setTriggers(parseTriggers(triggerObj));
        
        Object actionObj = data.get("Actions");
        if (actionObj == null) actionObj = data.get("actions");
        
        if (actionObj instanceof List) {
            stage.setActions(parseActions((List<Object>) actionObj));
        } else {
            stage.setActions(new ArrayList<>());
        }
        return stage;
    }

    private List<TriggerData> parseTriggers(Object raw) {
        List<TriggerData> result = new ArrayList<>();
        if (raw == null) return result;

        if (raw instanceof List) {
            for (Object obj : (List<?>) raw) {
                if (obj instanceof Map) {
                    result.add(parseSingleTrigger((Map<String, Object>) obj));
                }
            }
        } else if (raw instanceof Map) {
            result.add(parseSingleTrigger((Map<String, Object>) raw));
        }
        return result;
    }

    private TriggerData parseSingleTrigger(Map<String, Object> map) {
        // Handle format: {type: npc_interact, id: 5}
        if (map.containsKey("type")) {
            String type = (String) map.get("type");
            Map<String, Object> params = new HashMap<>(map);
            params.remove("type");
            return new TriggerData(type, params);
        }
        
        // Handle format: {npc_interact: {id: 5}}
        if (map.size() == 1) {
            String type = map.keySet().iterator().next();
            Object value = map.get(type);
            Map<String, Object> params = new HashMap<>();
            if (value instanceof Map) {
                params.putAll((Map<String, Object>) value);
            } else {
                params.put("value", value);
            }
            return new TriggerData(type, params);
        }

        return new TriggerData("unknown", map);
    }

    private List<ActionData> parseActions(List<Object> rawList) {
        List<ActionData> result = new ArrayList<>();
        if (rawList == null) return result;
        
        for (Object obj : rawList) {
            if (obj instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) obj;
                if (map.size() == 1) {
                    String type = map.keySet().iterator().next();
                    Object value = map.get(type);
                    Map<String, Object> params = new HashMap<>();
                    if (value instanceof Map) {
                        params.putAll((Map<String, Object>) value);
                    } else {
                        params.put("value", value);
                    }
                    result.add(new ActionData(type, params));
                } else {
                    String type2 = (String) map.get("type");
                    if (type2 != null) {
                        Map<String, Object> params2 = new HashMap<>(map);
                        params2.remove("type");
                        result.add(new ActionData(type2, params2));
                    }
                }
            } else if (obj instanceof String) {
                result.add(new ActionData((String) obj, new HashMap<>()));
            }
        }
        return result;
    }
}
