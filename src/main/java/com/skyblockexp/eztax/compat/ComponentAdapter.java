package com.skyblockexp.eztax.compat;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * String-based inventory/item adapter that works on both Paper and Spigot.
 * Adventure types are used only for internal message processing and are never
 * passed across the server API boundary.
 */
final class ComponentAdapter implements PlatformAdapter {

    @Override
    public Inventory createInventory(InventoryHolder holder, int size, String title) {
        return Bukkit.createInventory(holder, size, title);
    }

    @Override
    public void setDisplayName(ItemMeta meta, String name) {
        meta.setDisplayName(name);
    }

    @Override
    public void setLore(ItemMeta meta, List<String> lore) {
        meta.setLore(new ArrayList<>(lore));
    }
}
