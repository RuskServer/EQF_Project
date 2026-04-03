package com.lunar_prototype.eqf.persistence;

import com.lunar_prototype.eqf.api.PlayerQuestState;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/persistence/PersistenceAdapter.class */
public interface PersistenceAdapter {
    Optional<PlayerQuestState> loadState(UUID uuid, String str);

    void saveAllStates(UUID uuid, Collection<PlayerQuestState> collection);

    void close();
}
