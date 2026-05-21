package com.skyblockexp.eztax.compat;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Abstracts version-sensitive Bukkit inventory and item-meta API calls, allowing
 * the plugin to run on servers that expose Paper's native Adventure Component APIs
 * as well as on older servers that only expose the legacy String-based API.
 */
public interface PlatformAdapter {
    Inventory createInventory(InventoryHolder holder, int size, String title);
    void setDisplayName(ItemMeta meta, String name);
    void setLore(ItemMeta meta, List<String> lore);
}
