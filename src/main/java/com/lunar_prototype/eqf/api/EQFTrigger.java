package com.lunar_prototype.eqf.api;

import org.bukkit.event.Event;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/api/EQFTrigger.class */
public interface EQFTrigger<E extends Event> {
    Class<E> getEventClass();

    boolean check(PlayerQuestState playerQuestState, E e);
}
