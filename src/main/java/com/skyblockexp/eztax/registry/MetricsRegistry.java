package com.skyblockexp.eztax.registry;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MetricsRegistry {
    private final JavaPlugin plugin;
    private final int metricsId;

    public MetricsRegistry(Plugin plugin, int metricsId) {
        this.plugin = (JavaPlugin) plugin;
        this.metricsId = metricsId;
    }

    public void registerMetrics() {
        new Metrics(plugin, metricsId);
    }
}
