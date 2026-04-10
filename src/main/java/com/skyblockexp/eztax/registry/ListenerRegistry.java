package com.skyblockexp.eztax.registry;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.listener.DeathFeeListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ListenerRegistry {
    private final Plugin plugin;
    private final TaxConfig taxConfig;
    private final TaxEngine taxEngine;

    public ListenerRegistry(Plugin plugin, TaxConfig taxConfig, TaxEngine taxEngine) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
        this.taxEngine = taxEngine;
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new DeathFeeListener(taxConfig, taxEngine), plugin);
        // Intercept configured chat commands for transaction tax if enabled
        if (taxConfig != null && taxConfig.isTransactionTaxCaptureCommands()) {
            Bukkit.getPluginManager().registerEvents(new com.skyblockexp.eztax.listener.PayCommandListener(taxConfig, taxEngine), plugin);
        }
    }
}
