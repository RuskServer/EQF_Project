package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/CommandAction.class */
public class CommandAction implements EQFAction {
    private final String command;
    private final boolean runAsConsole;
    private final EQFPlugin plugin = EQFPlugin.getInstance();

    public CommandAction(String command, boolean runAsConsole) {
        this.command = command;
        this.runAsConsole = runAsConsole;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        String executableCommand = this.command.replace("%player_name%", context.getPlayer().getName());
        CommandSender sender = this.runAsConsole ? Bukkit.getConsoleSender() : context.getPlayer();

        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            try {
                Bukkit.dispatchCommand(sender, executableCommand);
            } catch (Exception e) {
                this.plugin.getLogger().warning("Failed to execute command: " + executableCommand + " (as " + (this.runAsConsole ? "CONSOLE" : "PLAYER") + ")");
                e.printStackTrace();
            }
        });
        return CompletableFuture.completedFuture(ActionResult.SUCCESS);
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/CommandAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            String cmdString = null;
            boolean runAsConsole = false;
            if (params.containsKey("value")) {
                cmdString = String.valueOf(params.get("value"));
            } else if (params.containsKey("command")) {
                cmdString = String.valueOf(params.get("command"));
            }
            if (cmdString == null) {
                throw new IllegalArgumentException("Command action requires a 'command' or 'value' parameter.");
            }
            if (params.containsKey("executor")) {
                String executor = String.valueOf(params.get("executor")).toLowerCase();
                runAsConsole = executor.equals("console");
            }
            return new CommandAction(cmdString, runAsConsole);
        }
    }
}
