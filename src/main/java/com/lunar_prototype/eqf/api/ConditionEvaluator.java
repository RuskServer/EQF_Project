package com.lunar_prototype.eqf.api;

import com.lunar_prototype.eqf.execution.QuestManager;
import org.bukkit.entity.Player;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/api/ConditionEvaluator.class */
public interface ConditionEvaluator {
    boolean evaluate(Player player, QuestManager questManager);
}
