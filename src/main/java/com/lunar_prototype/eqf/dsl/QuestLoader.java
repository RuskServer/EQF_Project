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

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/dsl/QuestLoader.class */
public class QuestLoader {
    private final EQFPlugin plugin;
    private final Path questDir;
    private final Yaml yaml = new Yaml();

    public QuestLoader(EQFPlugin plugin, Path questDir) {
        this.plugin = plugin;
        this.questDir = questDir;
    }

    public void loadAll() {
        if (!Files.exists(this.questDir, new LinkOption[0])) {
            try {
                Files.createDirectories(this.questDir, new FileAttribute[0]);
            } catch (Exception e) {
                this.plugin.getLogger().severe("Could not create quest directory: " + e.getMessage());
                return;
            }
        }
        File[] files = this.questDir.toFile().listFiles((dir, name) -> {
            return name.endsWith(".yml") || name.endsWith(".yaml");
        });
        if (files == null) {
            return;
        }
        for (File file : files) {
            try {
                Quest quest = loadQuest(file);
                this.plugin.getQuestManager().registerQuest(quest);
                this.plugin.getLogger().info("Loaded quest: " + quest.getId());
            } catch (Exception e2) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to load quest file: " + file.getName(), (Throwable) e2);
            }
        }
    }

    private Quest loadQuest(File file) throws Exception {
        InputStream is = new FileInputStream(file);
        try {
            Map<String, Object> data = (Map) this.yaml.load(is);
            if (data == null) throw new IllegalArgumentException("YAML data is empty");

            // Key mapping for flexibility
            String id = (String) data.getOrDefault("QuestID", data.getOrDefault("id", file.getName().replace(".yml", "")));
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

            Map<String, Object> stagesRaw = (Map) data.get("Stages");
            if (stagesRaw == null) stagesRaw = (Map) data.get("stages");
            
            Map<String, Stage> stages = new LinkedHashMap<>();
            if (stagesRaw != null) {
                for (Map.Entry<String, Object> entry : stagesRaw.entrySet()) {
                    String stageId = entry.getKey();
                    Map<String, Object> stageData = (Map) entry.getValue();
                    stages.put(stageId, parseStage(stageId, stageData));
                }
            }
            quest.setStages(stages);

            List<Map<String, Object>> triggersRaw = (List) data.get("StartTriggers");
            if (triggersRaw == null) triggersRaw = (List) data.get("start_triggers");
            if (triggersRaw == null) triggersRaw = (List) data.get("triggers");
            
            quest.setStartTriggers(parseTriggers(triggersRaw));
            is.close();
            return quest;
        } catch (Throwable th) {
            try {
                is.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private Stage parseStage(String id, Map<String, Object> data) {
        Stage stage = new Stage();
        stage.setId(id);
        stage.setDescription((String) data.getOrDefault("Description", data.get("description")));
        
        List<Map<String, Object>> triggerList = (List) data.get("Triggers");
        if (triggerList == null) triggerList = (List) data.get("triggers");
        if (triggerList == null) triggerList = (List) data.get("trigger");
        
        if (triggerList != null) {
            stage.setTriggers(parseTriggers(triggerList));
        } else {
            stage.setTriggers(new ArrayList());
        }
        
        List<Object> actionList = (List) data.get("Actions");
        if (actionList == null) actionList = (List) data.get("actions");
        
        if (actionList != null) {
            stage.setActions(parseActions(actionList));
        } else {
            stage.setActions(new ArrayList());
        }
        return stage;
    }

    private List<TriggerData> parseTriggers(List<Map<String, Object>> rawList) {
        List<TriggerData> result = new ArrayList<>();
        if (rawList == null) {
            return result;
        }
        for (Map<String, Object> map : rawList) {
            String type = (String) map.get("type");
            Map<String, Object> params = new HashMap<>(map);
            params.remove("type");
            result.add(new TriggerData(type, params));
        }
        return result;
    }

    private List<ActionData> parseActions(List<Object> rawList) {
        List<ActionData> result = new ArrayList<>();
        if (rawList == null) {
            return result;
        }
        for (Object obj : rawList) {
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
                result.add(new ActionData((String) obj, new HashMap()));
            }
        }
        return result;
    }
}
