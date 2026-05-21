package com.skyblockexp.eztax.compat;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Fallback adapter that uses the legacy String-based Bukkit API.
 * Selected automatically when the server does not expose Paper's
 * Adventure Component overloads (e.g. vanilla Bukkit / old Spigot builds).
 */
final class LegacyAdapter implements PlatformAdapter {

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
