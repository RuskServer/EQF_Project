package com.lunar_prototype.eqf.execution;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import com.lunar_prototype.eqf.model.ActionData;
import com.lunar_prototype.eqf.model.Quest;
import com.lunar_prototype.eqf.model.Stage;
import com.lunar_prototype.eqf.model.TriggerData;
import com.lunar_prototype.eqf.module.ConditionExpression;
import com.lunar_prototype.eqf.module.TriggerRegistry;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/execution/TriggerManager.class */
public class TriggerManager implements Listener {
    private final EQFPlugin plugin;
    private final QuestManager questManager;
    private final Map<String, Map<String, EQFTrigger<?>>> triggerCache = new HashMap();
    private final Map<UUID, Long> executionDebounce = new HashMap();
    private static final long DEBOUNCE_MILLIS = 200;

    public TriggerManager(EQFPlugin plugin, QuestManager questManager) {
        this.plugin = plugin;
        this.questManager = questManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        checkAndDispatchTriggers(event.getPlayer(), event);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            checkAndDispatchTriggers(event.getEntity().getKiller(), event);
        }
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        if (event.getKiller() instanceof Player) {
            Player player = (Player) event.getKiller();
            checkAndDispatchTriggers(player, event);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            return;
        }
        checkAndDispatchTriggers(event.getPlayer(), event);
    }

    @EventHandler
    public void onNPCInteract(NPCRightClickEvent event) {
        if (event.getClicker().getActiveItemHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.isCancelled()) {
            EQFPlugin.getInstance().getLogger().warning("[EQF Debug: NPCInteract] NPCInteractEvent is cancelled! This may prevent EQF triggers from firing.");
        }
        checkAndDispatchTriggers(event.getClicker(), event);
    }

    private <E extends Event> void checkAndDispatchTriggers(Player player, E event) {
        Stage currentStage;
        for (Quest quest : this.questManager.getQuestDefinitions().values()) {
            PlayerQuestState state = this.questManager.getPlayerState(player, quest.getId());
            if (!state.isExecuting()) {
                if (!state.isStarted()) {
                    if (quest.isRepeatable() || !"COMPLETED".equals(state.getCurrentStage())) {
                        if (!checkTriggers(quest.getStartTriggers(), event, state)) {
                            continue;
                        } else {
                            if (isDebounced(player.getUniqueId())) {
                                return;
                            }
                            updateDebounce(player.getUniqueId());
                            this.plugin.getLogger().info(player.getName() + " started quest via trigger: " + quest.getId());
                            state.setExecuting(true);
                            this.questManager.startQuest(player, quest.getId());
                            String firstStageId = state.getCurrentStage();
                            Stage firstStage = quest.getStages().get(firstStageId);
                            if (firstStage != null) {
                                executeActionsWithLock(player, quest, firstStage.getActions(), state);
                            } else {
                                state.setExecuting(false);
                            }
                        }
                    }
                } else {
                    String stageId = state.getCurrentStage();
                    if (stageId != null && !stageId.equals("COMPLETED") && !stageId.equals("COMPLETED_ADMIN") && (currentStage = quest.getStages().get(stageId)) != null && checkTriggers(currentStage.getTriggers(), event, state) && checkConditions(player, currentStage.getConditions())) {
                        if (isDebounced(player.getUniqueId())) {
                            return;
                        }
                        updateDebounce(player.getUniqueId());
                        this.plugin.getLogger().info(player.getName() + " advanced quest: " + quest.getId());
                        state.setExecuting(true);
                        executeActionsWithLock(player, quest, currentStage.getActions(), state);
                    }
                }
            }
        }
    }

    private boolean isDebounced(UUID playerUUID) {
        long now = System.currentTimeMillis();
        long lastTime = this.executionDebounce.getOrDefault(playerUUID, 0L).longValue();
        return now - lastTime < DEBOUNCE_MILLIS;
    }

    private void updateDebounce(UUID playerUUID) {
        this.executionDebounce.put(playerUUID, Long.valueOf(System.currentTimeMillis()));
    }

    private void executeActionsWithLock(Player player, Quest quest, List<ActionData> actions, PlayerQuestState state) {
        try {
            CompletableFuture<?> future = ActionExecutor.executeActions(player, quest, actions);
            if (future != null) {
                future.whenComplete((result, ex) -> {
                    state.setExecuting(false);
                    this.questManager.savePlayerStates(player.getUniqueId());
                    if (ex != null) {
                        this.plugin.getLogger().log(Level.SEVERE, "Error during action execution sequence", ex);
                    }
                });
            } else {
                state.setExecuting(false);
                this.questManager.savePlayerStates(player.getUniqueId());
            }
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error executing actions", (Throwable) e);
            state.setExecuting(false);
        }
    }

    private <E extends Event> boolean checkTriggers(List<TriggerData> triggerDatas, E event, PlayerQuestState state) {
        if (triggerDatas == null || triggerDatas.isEmpty()) {
            return false;
        }
        for (TriggerData data : triggerDatas) {
            try {
                EQFTrigger<?> trigger = TriggerRegistry.createTrigger(data);
                if (trigger.getEventClass().isInstance(event)) {
                    @SuppressWarnings("unchecked")
                    EQFTrigger<Event> untypedTrigger = (EQFTrigger<Event>) trigger;
                    if (untypedTrigger.check(state, event)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Error checking trigger type: " + data.type, e);
            }
        }
        return false;
    }

    private boolean checkConditions(Player player, List<String> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        for (String condition : conditions) {
            ConditionExpression evaluator = new ConditionExpression(condition);
            if (!evaluator.evaluate(player, this.questManager)) {
                return false;
            }
        }
        return true;
    }
}
