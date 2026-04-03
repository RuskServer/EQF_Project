package com.lunar_prototype.eqf.module;

import com.lunar_prototype.deepwither.StatManager;
import com.lunar_prototype.deepwither.StatMap;
import com.lunar_prototype.deepwither.StatType;
import com.lunar_prototype.eqf.api.ConditionEvaluator;
import com.lunar_prototype.eqf.execution.QuestManager;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/ConditionExpression.class */
public class ConditionExpression implements ConditionEvaluator {
    private final String fullExpression;

    public ConditionExpression(String expression) {
        this.fullExpression = expression.trim();
    }

    @Override // com.lunar_prototype.eqf.api.ConditionEvaluator
    public boolean evaluate(Player player, QuestManager questManager) {
        return evaluateExpression(player, questManager, this.fullExpression);
    }

    private boolean evaluateExpression(Player player, QuestManager questManager, String expression) {
        String exp = expression.trim();
        if (exp.contains("||")) {
            return Arrays.stream(exp.split("\\|\\|")).anyMatch(part -> {
                return evaluateExpression(player, questManager, part);
            });
        }
        if (exp.contains("&&")) {
            return Arrays.stream(exp.split("&&")).allMatch(part2 -> {
                return evaluateExpression(player, questManager, part2);
            });
        }
        return evaluateAtomic(player, questManager, exp);
    }

    private boolean evaluateAtomic(Player player, QuestManager questManager, String atomicExp) {
        String exp = atomicExp.trim();
        if (exp.startsWith("player.has_item:")) {
            return evaluateHasItem(player, exp.substring("player.has_item:".length()));
        }
        if (exp.startsWith("player.stat:")) {
            return evaluatePlayerStat(player, exp.substring("player.stat:".length()));
        }
        return false;
    }

    private boolean evaluatePlayerStat(Player player, String statParams) {
        try {
            String[] parts = statParams.split(":");
            if (parts.length < 2) {
                return false;
            }
            String statName = parts[0].toUpperCase();
            double requiredValue = Double.parseDouble(parts[1]);
            try {
                StatType type = StatType.valueOf(statName);
                StatMap totalStats = StatManager.getTotalStatsFromEquipment(player);
                double currentValue = totalStats.getFinal(type);
                return currentValue >= requiredValue;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } catch (Exception e2) {
            return false;
        }
    }

    private boolean evaluateHasItem(Player player, String itemParams) {
        try {
            String[] parts = itemParams.split(":");
            Material material = Material.valueOf(parts[0].toUpperCase());
            int requiredAmount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            return player.getInventory().contains(material, requiredAmount);
        } catch (Exception e) {
            return false;
        }
    }
}
