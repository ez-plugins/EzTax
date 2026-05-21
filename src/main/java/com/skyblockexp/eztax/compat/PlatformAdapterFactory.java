package com.skyblockexp.eztax.compat;

/**
 * Returns the {@link LegacyAdapter} for inventory and item-meta operations.
 *
 * <p>We always use the legacy String-based Bukkit API so the plugin works
 * identically on both Paper and Spigot. Adventure is used only for internal
 * message parsing (MiniMessage → Component → legacy String) and is shaded +
 * relocated into the plugin JAR; no Component objects ever cross the server
 * API boundary, avoiding classloader conflicts on either platform.</p>
 */
public final class PlatformAdapterFactory {
    private static final PlatformAdapter INSTANCE = new LegacyAdapter();

    private PlatformAdapterFactory() {}

    public static PlatformAdapter get() {
        return INSTANCE;
    }
}
