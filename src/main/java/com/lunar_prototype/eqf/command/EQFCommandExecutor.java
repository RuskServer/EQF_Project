package com.lunar_prototype.eqf.command;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import com.lunar_prototype.eqf.gui.MainMenuGui;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/command/EQFCommandExecutor.class */
public class EQFCommandExecutor implements CommandExecutor, TabCompleter {
    private final EQFPlugin plugin;

    public EQFCommandExecutor(EQFPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("eqf.admin")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "権限がありません。");
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            case "quest":
                handleQuest(sender, args);
                break;
            case "state":
                handleState(sender, args);
                break;
            case "menu":
                new MainMenuGui((Player) sender).open();
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(String.valueOf(ChatColor.GOLD) + "=== Echoes Quest Framework (EQF) ===");
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "/eqf reload" + String.valueOf(ChatColor.WHITE) + " - 設定とクエストを再読み込み");
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "/eqf quest start <player> <questId>" + String.valueOf(ChatColor.WHITE) + " - クエストを強制開始");
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "/eqf state <player>" + String.valueOf(ChatColor.WHITE) + " - プレイヤーの状態を表示");
    }

    private void handleReload(CommandSender sender) {
        long start = System.currentTimeMillis();
        this.plugin.reloadConfig();
        this.plugin.getQuestLoader().loadAll();
        long time = System.currentTimeMillis() - start;
        sender.sendMessage(String.valueOf(ChatColor.GREEN) + "[EQF] リロード完了 (" + time + "ms)");
    }

    private void handleQuest(CommandSender sender, String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("start")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "使用法: /eqf quest start <player> <questId>");
            return;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "プレイヤーが見つかりません: " + args[2]);
            return;
        }
        String questId = args[3];
        if (!this.plugin.getQuestManager().getQuestDefinitions().containsKey(questId)) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "クエストIDが見つかりません: " + questId);
        } else {
            this.plugin.getQuestManager().startQuest(target, questId);
            sender.sendMessage(String.valueOf(ChatColor.GREEN) + "[EQF] " + target.getName() + " にクエスト '" + questId + "' を開始させました。");
        }
    }

    private void handleState(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "使用法: /eqf state <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "プレイヤーが見つかりません: " + args[1]);
            return;
        }
        Collection<PlayerQuestState> states = this.plugin.getQuestManager().getPlayerAllStates(target.getUniqueId());
        sender.sendMessage(String.valueOf(ChatColor.GOLD) + "--- " + target.getName() + " のクエスト状態 ---");
        if (states.isEmpty()) {
            sender.sendMessage(String.valueOf(ChatColor.GRAY) + "進行中のクエストはありません。");
            return;
        }
        for (PlayerQuestState state : states) {
            String color = state.isStarted() ? ChatColor.GREEN.toString() : ChatColor.GRAY.toString();
            String status = state.isStarted() ? "進行中" : "完了/未開始";
            sender.sendMessage(color + "■ " + state.getQuestId() + String.valueOf(ChatColor.WHITE) + " - Stage: " + String.valueOf(ChatColor.AQUA) + state.getCurrentStage() + String.valueOf(ChatColor.WHITE) + " (" + status + ")");
            if (!state.getProgressData().isEmpty()) {
                sender.sendMessage(String.valueOf(ChatColor.GRAY) + "   Progress: " + state.getProgressData().toString());
            }
        }
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("eqf.admin")) {
            return Collections.emptyList();
        }
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("reload");
            suggestions.add("quest");
            suggestions.add("state");
            suggestions.add("menu");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("quest")) {
                suggestions.add("start");
            } else if (args[0].equalsIgnoreCase("state")) {
                return null;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("quest") && args[1].equalsIgnoreCase("start")) {
                return null;
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("quest") && args[1].equalsIgnoreCase("start")) {
                suggestions.addAll(this.plugin.getQuestManager().getQuestDefinitions().keySet());
            }
        } else if (args.length == 0 || args[0].equalsIgnoreCase("menu")) {
            if (sender instanceof Player) {
                new MainMenuGui((Player) sender).open();
            } else {
                sender.sendMessage(String.valueOf(ChatColor.RED) + "コンソールからはGUIを開けません。");
            }
        }
        return (List) suggestions.stream().filter(s -> {
            return s.toLowerCase().startsWith(args[args.length - 1].toLowerCase());
        }).collect(Collectors.toList());
    }
}
