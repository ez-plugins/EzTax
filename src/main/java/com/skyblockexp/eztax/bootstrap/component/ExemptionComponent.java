package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.service.ExemptionService;

public class ExemptionComponent implements Component {
    private final EzTaxPlugin plugin;
    private ExemptionService exemptionService;

    public ExemptionComponent(EzTaxPlugin plugin) { this.plugin = plugin; }

    @Override
    public void start() {
        this.exemptionService = new ExemptionService(plugin);
    }

    @Override
    public void stop() {
        // nothing
    }

    @Override
    public void reload() {
        // nothing
    }

    public ExemptionService getExemptionService() { return exemptionService; }
}
