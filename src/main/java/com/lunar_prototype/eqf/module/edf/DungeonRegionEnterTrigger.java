package com.lunar_prototype.eqf.module.edf;

import com.lunar_prototype.EDFProject.EDF;
import com.lunar_prototype.EDFProject.api.EDFAPIImpl;
import com.lunar_prototype.eqf.api.EQFTrigger;
import com.lunar_prototype.eqf.api.EQFTriggerFactory;
import com.lunar_prototype.eqf.api.PlayerQuestState;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/edf/DungeonRegionEnterTrigger.class */
public class DungeonRegionEnterTrigger implements EQFTrigger<PlayerMoveEvent> {
    private final String targetRegionName;
    private final EDFAPIImpl edfApi = EDF.getInstance().getEdfapi();

    public DungeonRegionEnterTrigger(String targetRegionName) {
        this.targetRegionName = targetRegionName.toLowerCase();
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public Class<PlayerMoveEvent> getEventClass() {
        return PlayerMoveEvent.class;
    }

    @Override // com.lunar_prototype.eqf.api.EQFTrigger
    public boolean check(PlayerQuestState state, PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if ((event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) || !this.edfApi.isInDungeon(player)) {
            return false;
        }
        List<String> currentRegions = this.edfApi.getPlayerDungeonRegions(player);
        for (String region : currentRegions) {
            if (region.equalsIgnoreCase(this.targetRegionName)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/module/edf/DungeonRegionEnterTrigger$Factory.class */
    public static class Factory implements EQFTriggerFactory {
        @Override // com.lunar_prototype.eqf.api.EQFTriggerFactory
        public EQFTrigger<?> create(Map<String, Object> params) {
            String region = (String) params.get("region");
            if (region == null) {
                throw new IllegalArgumentException("DungeonRegionEnter trigger requires 'region' parameter.");
            }
            return new DungeonRegionEnterTrigger(region);
        }
    }
}
