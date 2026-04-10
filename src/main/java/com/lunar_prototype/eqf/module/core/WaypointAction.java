package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.api.ActionContext;
import com.lunar_prototype.eqf.api.ActionResult;
import com.lunar_prototype.eqf.api.EQFAction;
import com.lunar_prototype.eqf.api.EQFActionFactory;
import com.lunar_prototype.eqf.execution.WaypointManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WaypointAction implements EQFAction {
    private final String subAction;
    private final String id;
    private final String text;
    private final double x, y, z;
    private final String worldName;
    private final WaypointManager waypointManager;

    public WaypointAction(String subAction, String id, String text, double x, double y, double z, String worldName, WaypointManager waypointManager) {
        this.subAction = subAction;
        this.id = id;
        this.text = text;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.waypointManager = waypointManager;
    }

    @Override
    public CompletableFuture<ActionResult> execute(ActionContext context) {
        Player player = context.getPlayer();
        if (subAction.equalsIgnoreCase("show")) {
            World world = worldName != null ? Bukkit.getWorld(worldName) : player.getWorld();
            if (world != null) {
                Location loc = new Location(world, x, y, z);
                waypointManager.addWaypoint(player, id, loc, MiniMessage.miniMessage().deserialize(text));
            }
        } else if (subAction.equalsIgnoreCase("hide")) {
            waypointManager.removeWaypoint(player, id);
        }
        return CompletableFuture.completedFuture(ActionResult.SUCCESS);
    }

    public static class Factory implements EQFActionFactory {
        private final WaypointManager waypointManager;

        public Factory(WaypointManager waypointManager) {
            this.waypointManager = waypointManager;
        }

        @Override
        public EQFAction create(Map<String, Object> params) {
            String subAction = (String) params.getOrDefault("action", "show");
            String id = (String) params.getOrDefault("id", "default");
            String text = (String) params.getOrDefault("text", "");
            double x = ((Number) params.getOrDefault("x", 0.0)).doubleValue();
            double y = ((Number) params.getOrDefault("y", 0.0)).doubleValue();
            double z = ((Number) params.getOrDefault("z", 0.0)).doubleValue();
            String worldName = (String) params.get("world");

            return new WaypointAction(subAction, id, text, x, y, z, worldName, waypointManager);
        }
    }
}
