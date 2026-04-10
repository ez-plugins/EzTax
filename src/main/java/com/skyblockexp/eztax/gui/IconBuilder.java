package com.skyblockexp.eztax.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Small helper to build ItemStacks for GUI icons.
 */
public final class IconBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public IconBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public IconBuilder name(String name) {
        if (meta != null) meta.setDisplayName(name);
        return this;
    }

    public IconBuilder lore(List<String> lines) {
        if (meta != null) meta.setLore(new ArrayList<>(lines));
        return this;
    }

    public ItemStack build() {
        if (meta != null) item.setItemMeta(meta);
        return item;
    }
}
