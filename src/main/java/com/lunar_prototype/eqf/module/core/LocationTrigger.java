package com.lunar_prototype.eqf.module.core;

import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;

/**
 * Trigger that activates when a player reaches a specific location.
 */
public class LocationTrigger implements EQFTrigger<PlayerMoveEvent> {
    private final String worldName;
    private final double x, y, z;
    private final double radiusSquared;

    public LocationTrigger(String worldName, double x, double y, double z, double radius) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radiusSquared = radius * radius;
    }

    @Override
    public Class<PlayerMoveEvent> getEventClass() {
        return PlayerMoveEvent.class;
    }

    @Override
    public boolean check(PlayerQuestState state, PlayerMoveEvent event) {
        Location loc = event.getTo();
        if (loc == null) return false;

        // Check world
        if (!loc.getWorld().getName().equalsIgnoreCase(worldName)) {
            return false;
        }

        // Calculate squared distance (performance optimization)
        double dx = loc.getX() - x;
        double dy = loc.getY() - y;
        double dz = loc.getZ() - z;
        double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);

        return distanceSquared <= radiusSquared;
    }

    public static class Factory implements EQFTriggerFactory {
        @Override
        public EQFTrigger<?> create(Map<String, Object> params) {
            String world = (String) params.getOrDefault("world", "world");
            double x = ((Number) params.getOrDefault("x", 0.0)).doubleValue();
            double y = ((Number) params.getOrDefault("y", 0.0)).doubleValue();
            double z = ((Number) params.getOrDefault("z", 0.0)).doubleValue();
            double radius = ((Number) params.getOrDefault("radius", 3.0)).doubleValue();
            
            return new LocationTrigger(world, x, y, z, radius);
        }
    }
}
