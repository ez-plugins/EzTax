package com.skyblockexp.eztax.compat;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Detects the server's API capabilities on first use and returns the
 * appropriate {@link PlatformAdapter} singleton for the lifetime of the plugin.
 *
 * <p>Detection uses {@code ItemMeta.displayName(Component)}, which is a
 * <em>Paper-only</em> extension absent from vanilla Bukkit / Spigot.
 * Spigot 1.18+ exposes the Adventure API but stops short of the Paper-specific
 * {@code ItemMeta} component overloads, so this check correctly selects
 * {@link LegacyAdapter} on Spigot and {@link ComponentAdapter} on Paper.</p>
 */
public final class PlatformAdapterFactory {
    private static volatile PlatformAdapter instance;

    private PlatformAdapterFactory() {}

    /**
     * Returns the adapter, creating it on first call via runtime detection.
     * Thread-safe without locking in the common (already-initialized) path.
     */
    public static PlatformAdapter get() {
        if (instance == null) {
            synchronized (PlatformAdapterFactory.class) {
                if (instance == null) {
                    instance = detect();
                }
            }
        }
        return instance;
    }

    private static PlatformAdapter detect() {
        try {
            // ItemMeta.displayName(Component) is Paper-only; absent from Spigot's ItemMeta interface.
            ItemMeta.class.getMethod("displayName", Component.class);
            return new ComponentAdapter();
        } catch (NoSuchMethodException e) {
            return new LegacyAdapter();
        }
    }
}
