package com.skyblockexp.eztax.service;

import com.skyblockexp.eztax.registry.Registry;
import com.skyblockexp.eztax.EzTaxPlugin;

public class ConfigService {
    private final EzTaxPlugin plugin;
    private final Registry registry;

    public ConfigService(EzTaxPlugin plugin, Registry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public void reloadEzTax() {
        plugin.reloadConfig();
        registry.getTaxConfig().reload();
        registry.getMessages().reload();
        // If you have other reloadable components, reload them here
    }
}
