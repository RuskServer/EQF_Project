package com.lunar_prototype.eqf.module.worldguard;

import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.Map;
import org.bukkit.event.player.PlayerMoveEvent;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/worldguard/AreaEnterTrigger.class */
public class AreaEnterTrigger implements EQFTrigger<PlayerMoveEvent> {
    private final String targetRegionId;

    public AreaEnterTrigger(String targetRegionId) {
        this.targetRegionId = targetRegionId.toLowerCase();
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public Class<PlayerMoveEvent> getEventClass() {
        return PlayerMoveEvent.class;
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public boolean check(PlayerQuestState state, PlayerMoveEvent event) {
        if (WorldGuard.getInstance() == null) {
            return false;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet toRegions = query.getApplicableRegions(BukkitAdapter.adapt(event.getTo()));
        ApplicableRegionSet fromRegions = query.getApplicableRegions(BukkitAdapter.adapt(event.getFrom()));
        boolean isNowInTarget = toRegions.getRegions().stream().anyMatch(region -> {
            return region.getId().equalsIgnoreCase(this.targetRegionId);
        });
        boolean wasNotInTarget = fromRegions.getRegions().stream().noneMatch(region2 -> {
            return region2.getId().equalsIgnoreCase(this.targetRegionId);
        });
        return isNowInTarget && wasNotInTarget;
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/worldguard/AreaEnterTrigger$Factory.class */
    public static class Factory implements EQFTriggerFactory {
        @Override // com.lunar_prototype.eqf.api.EQFTriggerFactory
        public EQFTrigger<?> create(Map<String, Object> params) {
            String regionId = (String) params.get("region");
            if (regionId == null) {
                throw new IllegalArgumentException("AreaEnterTrigger requires a 'region' ID.");
            }
            return new AreaEnterTrigger(regionId);
        }
    }
}
