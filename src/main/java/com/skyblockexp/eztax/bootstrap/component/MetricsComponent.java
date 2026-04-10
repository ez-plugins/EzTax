package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import org.bstats.bukkit.Metrics;

public class MetricsComponent implements Component {
    private final EzTaxPlugin plugin;
    private Metrics metrics;

    public MetricsComponent(EzTaxPlugin plugin) { this.plugin = plugin; }

    @Override
    public void start() {
        try {
            this.metrics = new Metrics(plugin, 28537);
        } catch (IllegalStateException ex) {
            plugin.getLogger().warning("bStats Metrics failed to initialize: " + ex.getMessage() + " — continuing without metrics.");
            this.metrics = null;
        } catch (Throwable t) {
            plugin.getLogger().warning("Unexpected error initializing bStats Metrics: " + t.getMessage() + " — continuing without metrics.");
            this.metrics = null;
        }
    }

    @Override
    public void stop() {
        // bStats has no stop API
    }

    @Override
    public void reload() {
        // nothing
    }

    public Metrics getMetrics() { return metrics; }
}
