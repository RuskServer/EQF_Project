package com.lunar_prototype.eqf.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketUtil {
    private static final AtomicInteger ENTITY_ID_COUNTER = new AtomicInteger(1000000);

    public static int getNewEntityId() {
        return ENTITY_ID_COUNTER.getAndIncrement();
    }

    public static void spawnTextDisplay(Player player, int entityId, UUID uuid, Location loc, Component text) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                entityId,
                Optional.of(uuid),
                EntityTypes.TEXT_DISPLAY,
                new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                loc.getPitch(),
                loc.getYaw(),
                0,
                0,
                Optional.empty()
        );

        List<EntityData<?>> metadataList = new ArrayList<>();
        
        // Index 15: Billboard (3 = Center)
        metadataList.add(new EntityData<>(15, EntityDataTypes.BYTE, (byte) 3));
        
        // Index 23: Text (Component)
        metadataList.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, text));
        
        // Index 25: Background Color (0 = Transparent)
        metadataList.add(new EntityData<>(25, EntityDataTypes.INT, 0));
        
        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(entityId, metadataList);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnPacket);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadataPacket);
    }

    public static void destroyEntity(Player player, int entityId) {
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(entityId);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
    }
}
