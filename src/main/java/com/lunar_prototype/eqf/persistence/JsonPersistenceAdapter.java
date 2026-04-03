package com.lunar_prototype.eqf.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class JsonPersistenceAdapter implements PersistenceAdapter {
    private final EQFPlugin plugin;
    private final File dataFolder;
    private final Gson gson;
    private static final Type STATE_MAP_TYPE = new TypeToken<Map<String, PlayerQuestState>>() {}.getType();

    public JsonPersistenceAdapter(EQFPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private File getPlayerFile(UUID playerUuid) {
        return new File(this.dataFolder, playerUuid.toString() + ".json");
    }

    @Override
    public Optional<PlayerQuestState> loadState(UUID playerUuid, String questId) {
        File playerFile = getPlayerFile(playerUuid);
        if (!playerFile.exists()) {
            return Optional.empty();
        }

        try (FileReader reader = new FileReader(playerFile)) {
            Map<String, PlayerQuestState> allStates = this.gson.fromJson(reader, STATE_MAP_TYPE);
            if (allStates != null) {
                return Optional.ofNullable(allStates.get(questId));
            }
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to load player data for " + playerUuid, e);
        }
        return Optional.empty();
    }

    @Override
    public void saveAllStates(UUID playerUuid, Collection<PlayerQuestState> states) {
        File playerFile = getPlayerFile(playerUuid);
        Map<String, PlayerQuestState> allStatesMap = new HashMap<>();
        for (PlayerQuestState state : states) {
            allStatesMap.put(state.getQuestId(), state);
        }

        try (FileWriter writer = new FileWriter(playerFile)) {
            this.gson.toJson(allStatesMap, writer);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + playerUuid, e);
        }
    }

    @Override
    public void close() {
    }
}
