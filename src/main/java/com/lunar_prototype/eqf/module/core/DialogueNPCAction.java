package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DialogueNPCAction implements EQFAction {
    private final int npcId;
    private final List<String> lines;
    private final int speedTicks;
    private final boolean lockView;
    private final String soundName;

    public DialogueNPCAction(int npcId, List<String> lines, int speedTicks, boolean lockView, String soundName) {
        this.npcId = npcId;
        this.lines = lines;
        this.speedTicks = speedTicks;
        this.lockView = lockView;
        this.soundName = soundName;
    }

    @Override
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        CompletableFuture<ActionResult> future = new CompletableFuture<>();
        Player player = context.getPlayer();
        NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);

        if (npc == null || !npc.isSpawned()) {
            future.complete(ActionResult.FAILURE);
            return future;
        }

        Entity npcEntity = npc.getEntity();
        String npcName = npc.getName();
        Sound sound = null;
        if (soundName != null) {
            try {
                sound = Sound.valueOf(soundName.toUpperCase());
            } catch (Exception ignored) {}
        }

        final Sound finalSound = sound;

        new BukkitRunnable() {
            int currentLineIndex = 0;
            int currentCharIndex = 0;
            StringBuilder currentBuffer = new StringBuilder();
            boolean waitingForNextLine = false;
            int waitCounter = 0;

            @Override
            public void run() {
                if (!player.isOnline() || !npc.isSpawned()) {
                    this.cancel();
                    future.complete(ActionResult.FAILURE);
                    return;
                }

                // 視線固定
                if (lockView) {
                    Location playerLoc = player.getLocation();
                    Location npcLoc = npc.getEntity().getLocation().add(0, 1.2, 0); // 目線の高さ付近
                    Vector direction = npcLoc.toVector().subtract(playerLoc.toVector()).normalize();
                    playerLoc.setDirection(direction);
                    player.teleport(playerLoc);
                }

                if (waitingForNextLine) {
                    waitCounter++;
                    if (waitCounter > 40) { // 2秒待機して次へ
                        waitingForNextLine = false;
                        waitCounter = 0;
                        currentLineIndex++;
                        currentCharIndex = 0;
                        currentBuffer.setLength(0);
                        if (currentLineIndex >= lines.size()) {
                            this.cancel();
                            future.complete(ActionResult.SUCCESS);
                        }
                    }
                    return;
                }

                String currentLine = lines.get(currentLineIndex);
                if (currentCharIndex < currentLine.length()) {
                    currentBuffer.append(currentLine.charAt(currentCharIndex));
                    currentCharIndex++;
                    
                    if (finalSound != null && currentCharIndex % 2 == 0) {
                        player.playSound(player.getLocation(), finalSound, 0.5f, 1.2f);
                    }
                } else {
                    waitingForNextLine = true;
                }

                // 表示処理 (MiniMessage対応)
                String text = "<gold>[" + npcName + "]</gold> <white>" + currentBuffer.toString() + "</white>";
                Component component = MiniMessage.miniMessage().deserialize(text);
                
                // タイトル（サブタイトル）として表示
                Title title = Title.title(Component.empty(), component, 
                    Title.Times.times(Duration.ZERO, Duration.ofMillis(1500), Duration.ofMillis(500)));
                player.showTitle(title);
                // 同時にActionBarにも出すと親切
                player.sendActionBar(component);
            }
        }.runTaskTimer(EQFPlugin.getInstance(), 0L, speedTicks);

        return future;
    }

    public static class Factory implements EQFActionFactory {
        @Override
        public EQFAction create(Map<String, Object> params) {
            int npcId = ((Number) params.getOrDefault("npc_id", -1)).intValue();
            Object linesObj = params.get("lines");
            List<String> lines = new ArrayList<>();
            if (linesObj instanceof List) {
                for (Object o : (List<?>) linesObj) {
                    lines.add(String.valueOf(o));
                }
            } else if (linesObj != null) {
                lines.add(String.valueOf(linesObj));
            }

            int speed = ((Number) params.getOrDefault("speed", 2)).intValue();
            boolean lock = (boolean) params.getOrDefault("lock_view", true);
            String sound = (String) params.getOrDefault("sound", "BLOCK_NOTE_BLOCK_HARP");

            return new DialogueNPCAction(npcId, lines, speed, lock, sound);
        }
    }
}
