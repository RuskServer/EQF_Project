package com.lunar_prototype.eqf.util;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/* JADX INFO: loaded from: EQF-Project-1.0-SNAPSHOT.jar:com/lunar_prototype/eqf/util/ItemBuilder.class */
public class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            this.item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta != null) {
            meta.setLore(Arrays.asList(lore));
            this.item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta != null) {
            meta.setLore(lore);
            this.item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder skullOwner(OfflinePlayer player) {
        if (this.item.getType() == Material.PLAYER_HEAD) {
            ItemMeta meta = this.item.getItemMeta();
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(player);
                this.item.setItemMeta(skullMeta);
            }
        }
        return this;
    }

    public ItemStack build() {
        return this.item;
    }
}
