package com.lunar_prototype.eqf.module.mythic;

import com.lunar_prototype.eqf.EQFPlugin;
import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/mythic/MythicSpawnAction.class */
public class MythicSpawnAction implements EQFAction {
    private final String mobId;
    private final int level;
    private final int count;

    public MythicSpawnAction(String mobId, int level, int count) {
        this.mobId = mobId;
        this.level = level;
        this.count = count;
    }

    @Override // com.lunar_prototype.eqf.api.EQFAction
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        MythicMob mob = (MythicMob) MythicBukkit.inst().getMobManager().getMythicMob(this.mobId).orElse(null);
        if (mob == null) {
            EQFPlugin.getInstance().getLogger().warning("MythicMob not found: " + this.mobId);
            return CompletableFuture.completedFuture(ActionResult.FAILURE);
        }
        Location spawnLoc = context.getPlayer().getLocation();
        context.getPlayer().getServer().getScheduler().runTask(EQFPlugin.getInstance(), () -> {
            for (int i = 0; i < this.count; i++) {
                mob.spawn(BukkitAdapter.adapt(spawnLoc), this.level);
            }
        });
        return CompletableFuture.completedFuture(ActionResult.SUCCESS);
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/mythic/MythicSpawnAction$Factory.class */
    public static class Factory implements EQFActionFactory {
        @Override // com.lunar_prototype.eqf.api.EQFActionFactory
        public EQFAction create(Map<String, Object> params) {
            String mobId = (String) params.get("mob");
            if (mobId == null) {
                if (params.containsKey("value")) {
                    mobId = String.valueOf(params.get("value"));
                } else {
                    throw new IllegalArgumentException("MythicSpawnAction requires a 'mob' parameter.");
                }
            }
            int level = 1;
            if (params.containsKey("level") && (params.get("level") instanceof Number)) {
                level = ((Number) params.get("level")).intValue();
            }
            int count = 1;
            if (params.containsKey("count") && (params.get("count") instanceof Number)) {
                count = ((Number) params.get("count")).intValue();
            }
            return new MythicSpawnAction(mobId, level, count);
        }
    }
}
