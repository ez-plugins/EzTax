package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.registry.ListenerRegistry;

public class ListenerComponent implements Component {
    private final EzTaxPlugin plugin;
    private final TaxEngine taxEngine;
    private final com.skyblockexp.eztax.config.TaxConfig taxConfig;
    private boolean started = false;

    public ListenerComponent(EzTaxPlugin plugin, com.skyblockexp.eztax.config.TaxConfig taxConfig, TaxEngine taxEngine) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
        this.taxEngine = taxEngine;
    }

    @Override
    public void start() {
        com.skyblockexp.eztax.registry.ListenerRegistry registry = new com.skyblockexp.eztax.registry.ListenerRegistry(plugin, taxConfig, taxEngine);
        registry.registerListeners();
        // Also register GUI listeners here to match previous behavior
        org.bukkit.Bukkit.getPluginManager().registerEvents(new com.skyblockexp.eztax.gui.listener.TaxGUIListener(), plugin);
        org.bukkit.Bukkit.getPluginManager().registerEvents(new com.skyblockexp.eztax.gui.listener.PayGUIListener(), plugin);
        this.started = true;
    }

    @Override
    public void stop() {
        // nothing
    }

    @Override
    public void reload() {
        // nothing
    }

    public boolean isStarted() { return started; }
}
