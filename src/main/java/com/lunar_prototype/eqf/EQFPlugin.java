package com.lunar_prototype.eqf;

import com.lunar_prototype.eqf.api.EQFActionFactory;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.command.EQFCommandExecutor;
import com.lunar_prototype.eqf.dsl.QuestLoader;
import com.lunar_prototype.eqf.execution.QuestManager;
import com.lunar_prototype.eqf.execution.TriggerManager;
import com.lunar_prototype.eqf.execution.WaypointManager;
import com.lunar_prototype.eqf.gui.GuiListener;
import com.lunar_prototype.eqf.module.ActionRegistry;
import com.lunar_prototype.eqf.module.KillTrigger;
import com.lunar_prototype.eqf.module.TriggerRegistry;
import com.lunar_prototype.eqf.module.citizens.NPCInteractTrigger;
import com.lunar_prototype.eqf.module.core.*;
import com.lunar_prototype.eqf.module.edf.DungeonRegionEnterTrigger;
import com.lunar_prototype.eqf.module.mythic.MythicKillTrigger;
import com.lunar_prototype.eqf.module.mythic.MythicSpawnAction;
import com.lunar_prototype.eqf.module.worldguard.AreaEnterTrigger;
import com.lunar_prototype.eqf.persistence.JsonPersistenceAdapter;
import com.lunar_prototype.eqf.persistence.PersistenceAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/EQFPlugin.class */
public class EQFPlugin extends JavaPlugin implements Listener {
    private static EQFPlugin instance;
    private QuestManager questManager;
    private QuestLoader questLoader;
    private PersistenceAdapter persistenceAdapter;
    private WaypointManager waypointManager;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.persistenceAdapter = new JsonPersistenceAdapter(this);
        this.waypointManager = new WaypointManager();
        ActionRegistry.registerAction("message", new MessageAction.Factory());
        ActionRegistry.registerAction("next", new NextAction.Factory());
        ActionRegistry.registerAction("wait", new WaitAction.Factory());
        ActionRegistry.registerAction("spawnmob", new SpawnMobAction.Factory());
        ActionRegistry.registerAction("give_item", new GiveItemAction.Factory());
        ActionRegistry.registerAction("complete", new CompleteAction.Factory());
        ActionRegistry.registerAction("parallel", new ParallelAction.Factory());
        ActionRegistry.registerAction("choose", new ChooseAction.Factory());
        ActionRegistry.registerAction("choice", new MultipleChoiceAction.Factory());
        ActionRegistry.registerAction("command", new CommandAction.Factory());
        ActionRegistry.registerAction("dialogue", new DialogueAction.Factory());
        ActionRegistry.registerAction("dialogue_npc", new com.lunar_prototype.eqf.module.core.DialogueNPCAction.Factory());
        ActionRegistry.registerAction("waypoint", new WaypointAction.Factory(this.waypointManager));
        TriggerRegistry.registerTrigger("interact", new InteractTrigger.Factory());
        TriggerRegistry.registerTrigger("kill", new KillTrigger.Factory());
        TriggerRegistry.registerTrigger("location", new LocationTrigger.Factory());
        PluginManager pm = getServer().getPluginManager();
        Plugin worldGuard = pm.getPlugin("WorldGuard");
        if (worldGuard != null && worldGuard.isEnabled()) {
            getLogger().info("WorldGuard detected! Registering WG modules.");
            TriggerRegistry.registerTrigger("area_enter", new AreaEnterTrigger.Factory());
        } else {
            getLogger().warning("WorldGuard not found. Area determination integration disabled.");
        }
        Plugin citizens = pm.getPlugin("Citizens");
        if (citizens != null && citizens.isEnabled()) {
            getLogger().info("Citizens detected! Registering NPC modules.");
            TriggerRegistry.registerTrigger("npc_interact", new NPCInteractTrigger.Factory());
        } else {
            getLogger().warning("Citizens not found. NPC interaction integration disabled.");
        }
        Plugin edf = pm.getPlugin("EDF-Project");
        if (edf != null && edf.isEnabled()) {
            getLogger().info("EDF-Project detected! Registering EDF modules.");
            TriggerRegistry.registerTrigger("dungeon_region_enter", new DungeonRegionEnterTrigger.Factory());
        } else {
            getLogger().warning("EDF-Project not found. EDF integration disabled.");
        }
        if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            getLogger().info("MythicMobs detected! Registering MM modules.");
            ActionRegistry.registerAction("mythicmob_spawn", new MythicSpawnAction.Factory());
            TriggerRegistry.registerTrigger("mythicmob_kill", new MythicKillTrigger.Factory());
        } else {
            getLogger().warning("MythicMobs not found. MM integration disabled.");
        }
        this.questManager = new QuestManager(this, this.persistenceAdapter);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new TriggerManager(this, this.questManager), this);
        getServer().getPluginManager().registerEvents(this.waypointManager, this);
        this.questLoader = new QuestLoader(this, getDataFolder().toPath().resolve("quests"));
        this.questLoader.loadAll();
        if (getCommand("eqf") != null) {
            EQFCommandExecutor cmdExecutor = new EQFCommandExecutor(this);
            getCommand("eqf").setExecutor(cmdExecutor);
            getCommand("eqf").setTabCompleter(cmdExecutor);
        }
        getLogger().info("Echoes Quest Framework has been enabled!");
    }

    public void onDisable() {
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            this.questManager.savePlayerStates(onlinePlayer.getUniqueId());
        }
        this.persistenceAdapter.close();
        getLogger().info("Echoes Quest Framework has been disabled.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.questManager.unloadPlayerStates(event.getPlayer().getUniqueId());
    }

    public static EQFPlugin getInstance() {
        return instance;
    }

    /**
     * Registers a custom action type.
     * @param id The unique identifier for the action.
     * @param factory The factory to create the action instances.
     */
    public void registerAction(String id, EQFActionFactory factory) {
        ActionRegistry.registerAction(id, factory);
        getLogger().info("Registered custom action: " + id);
    }

    /**
     * Registers a custom trigger type.
     * @param id The unique identifier for the trigger.
     * @param factory The factory to create the trigger instances.
     */
    public void registerTrigger(String id, EQFTriggerFactory factory) {
        TriggerRegistry.registerTrigger(id, factory);
        getLogger().info("Registered custom trigger: " + id);
    }

    public QuestManager getQuestManager() {
        return this.questManager;
    }

    public QuestLoader getQuestLoader() {
        return this.questLoader;
    }
}
