package com.skyblockexp.eztax.compat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Uses Paper's native Adventure Component APIs (available on Paper 1.16.5+).
 * Input strings are treated as legacy-formatted (§ codes); they are deserialized
 * to Components before being forwarded to the server, yielding correct rendering
 * without deprecation warnings on modern Paper builds.
 */
final class ComponentAdapter implements PlatformAdapter {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    @Override
    public Inventory createInventory(InventoryHolder holder, int size, String title) {
        Component comp = LEGACY.deserialize(title);
        return Bukkit.createInventory(holder, size, comp);
    }

    @Override
    public void setDisplayName(ItemMeta meta, String name) {
        meta.displayName(LEGACY.deserialize(name));
    }

    @Override
    public void setLore(ItemMeta meta, List<String> lore) {
        List<Component> comps = lore.stream()
                .map(LEGACY::deserialize)
                .collect(Collectors.toList());
        meta.lore(comps);
    }
}
