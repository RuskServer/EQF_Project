package com.lunar_prototype.eqf.api;

import com.lunar_prototype.eqf.model.Quest;
import org.bukkit.entity.Player;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/api/ActionContext.class */
public class ActionContext {
    private final Player player;
    private final Quest quest;

    public ActionContext(Player player, Quest quest) {
        this.player = player;
        this.quest = quest;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Quest getQuest() {
        return this.quest;
    }
}
