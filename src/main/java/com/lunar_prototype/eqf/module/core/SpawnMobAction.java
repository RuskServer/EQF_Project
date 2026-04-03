package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/SpawnMobAction.class */
public class SpawnMobAction implements EQFAction {
    private final EntityType entityType;
    private final String locationKey;
    private final int count;

    public SpawnMobAction(EntityType entityType, String locationKey, int count) {
        this.entityType = entityType;
        this.locationKey = locationKey;
        this.count = count;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        Location spawnLoc;
        Player player = context.getPlayer();
        if (this.locationKey.equalsIgnoreCase("player")) {
            spawnLoc = player.getLocation();
        } else {
            player.sendMessage("§c[EQF] LocationResolverが未実装のため、プレイヤーの位置に出現させます。");
            spawnLoc = player.getLocation();
        }
        for (int i = 0; i < this.count; i++) {
            Location location = spawnLoc;
            player.getServer().getScheduler().runTask(EQFPlugin.getInstance(), () -> {
                player.getWorld().spawnEntity(location, this.entityType);
            });
        }
        player.sendMessage("§c[EQF] " + this.entityType.name() + "を" + this.count + "体出現させました。");
        return CompletableFuture.completedFuture(ActionResult.SUCCESS);
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/core/SpawnMobAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            String mobName = (String) params.get("mob");
            String locKey = (String) params.getOrDefault("location", "player");
            int count = (params.containsKey("count") && (params.get("count") instanceof Number)) ? ((Number) params.get("count")).intValue() : 1;
            try {
                EntityType type = EntityType.valueOf(mobName.toUpperCase());
                return new SpawnMobAction(type, locKey, count);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown EntityType or Mob ID: " + mobName);
            }
        }
    }
}
