package com.lunar_prototype.eqf.execution;

import com.lunar_prototype.eqf.util.PacketUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WaypointManager implements Listener {
    private final Map<UUID, Map<String, Waypoint>> playerWaypoints = new ConcurrentHashMap<>();

    public static class Waypoint {
        private final int entityId;
        private final UUID uuid;
        private final Location location;
        private final Component text;

        public Waypoint(int entityId, UUID uuid, Location location, Component text) {
            this.entityId = entityId;
            this.uuid = uuid;
            this.location = location;
            this.text = text;
        }

        public int getEntityId() { return entityId; }
        public UUID getUuid() { return uuid; }
        public Location getLocation() { return location; }
        public Component getText() { return text; }
    }

    public void addWaypoint(Player player, String id, Location location, Component text) {
        removeWaypoint(player, id);

        int entityId = PacketUtil.getNewEntityId();
        UUID uuid = UUID.randomUUID();
        Waypoint waypoint = new Waypoint(entityId, uuid, location, text);

        playerWaypoints.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(id, waypoint);
        PacketUtil.spawnTextDisplay(player, entityId, uuid, location, text);
    }

    public void removeWaypoint(Player player, String id) {
        Map<String, Waypoint> waypoints = playerWaypoints.get(player.getUniqueId());
        if (waypoints != null) {
            Waypoint waypoint = waypoints.remove(id);
            if (waypoint != null) {
                PacketUtil.destroyEntity(player, waypoint.getEntityId());
            }
        }
    }

    public void clearWaypoints(Player player) {
        Map<String, Waypoint> waypoints = playerWaypoints.remove(player.getUniqueId());
        if (waypoints != null) {
            for (Waypoint waypoint : waypoints.values()) {
                PacketUtil.destroyEntity(player, waypoint.getEntityId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clearWaypoints(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        // Waypoints are usually world-specific. Destroy and re-spawn if necessary?
        // For simplicity, we just destroy them since they might not be relevant in the new world.
        clearWaypoints(event.getPlayer());
    }
}
