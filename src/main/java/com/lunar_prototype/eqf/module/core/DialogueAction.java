package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/DialogueAction.class */
public class DialogueAction implements EQFAction {
    private final String text;
    private final String speaker;
    private final int speedTicks;
    private final Sound charSound;
    private final boolean useSubtitle;

    public DialogueAction(String text, String speaker, int speedTicks, Sound charSound, boolean useSubtitle) {
        this.text = text;
        this.speaker = speaker;
        this.speedTicks = speedTicks;
        this.charSound = charSound;
        this.useSubtitle = useSubtitle;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        final CompletableFuture<ActionResult> future = new CompletableFuture<>();
        final Player player = context.getPlayer();
        final EQFPlugin plugin = EQFPlugin.getInstance();
        
        new BukkitRunnable() {
            int index = 0;
            final StringBuilder displayBuffer = new StringBuilder();
            final char[] characters = text.toCharArray();

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    future.complete(ActionResult.FAILURE);
                    return;
                }
                if (this.index < this.characters.length) {
                    this.displayBuffer.append(this.characters[this.index]);
                    this.index++;
                    if (charSound != null && this.index % 2 == 0) {
                        player.playSound(player.getLocation(), charSound, 0.5f, 1.5f);
                    }
                }
                String fullText = this.displayBuffer.toString();
                if (speaker != null && !speaker.isEmpty()) {
                    fullText = "§6[" + speaker + "] §f" + fullText;
                }
                if (useSubtitle) {
                    Title title = Title.title(Component.empty(), Component.text(fullText), Title.Times.times(Duration.ZERO, Duration.ofMillis(1000L), Duration.ofMillis(500L)));
                    player.showTitle(title);
                } else {
                    player.sendActionBar(Component.text(fullText));
                }
                if (this.index >= this.characters.length) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            future.complete(ActionResult.SUCCESS);
                        }
                    }.runTaskLater(plugin, 20L);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, this.speedTicks);
        return future;
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/DialogueAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            String text = null;
            Object textObj = params.get("text");
            if (textObj == null) textObj = params.get("value");
            if (textObj == null) textObj = params.get("lines");

            if (textObj instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) textObj;
                java.util.StringJoiner joiner = new java.util.StringJoiner(" ");
                for (Object obj : list) {
                    joiner.add(String.valueOf(obj));
                }
                text = joiner.toString();
            } else if (textObj != null) {
                text = String.valueOf(textObj);
            }

            if (text == null) {
                throw new IllegalArgumentException("Dialogue action requires 'text', 'value', or 'lines'.");
            }

            Object speakerObj = params.getOrDefault("speaker", params.get("npc_name"));
            String speaker = speakerObj != null ? String.valueOf(speakerObj) : null;
            
            int speed = 2;
            if (params.containsKey("speed") && (params.get("speed") instanceof Number)) {
                speed = ((Number) params.get("speed")).intValue();
            }
            Sound sound = null;
            if (params.containsKey("sound")) {
                try {
                    sound = Sound.valueOf(String.valueOf(params.get("sound")).toUpperCase());
                } catch (IllegalArgumentException e) {
                }
            }
            boolean subtitle = false;
            if (params.containsKey("type") && "subtitle".equalsIgnoreCase(String.valueOf(params.get("type")))) {
                subtitle = true;
            }
            return new DialogueAction(text, speaker, speed, sound, subtitle);
        }
    }
}
