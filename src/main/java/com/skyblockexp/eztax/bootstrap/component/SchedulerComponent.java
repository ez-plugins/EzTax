package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.service.SchedulerService;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.config.TaxConfig;

public class SchedulerComponent implements Component {
    private final EzTaxPlugin plugin;
    private final TaxConfig taxConfig;
    private final TaxEngine taxEngine;
    private SchedulerService schedulerService;

    public SchedulerComponent(EzTaxPlugin plugin, TaxConfig taxConfig, TaxEngine taxEngine) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
        this.taxEngine = taxEngine;
    }

    @Override
    public void start() {
        this.schedulerService = new SchedulerService(plugin, taxConfig, taxEngine);
        this.schedulerService.start();
    }

    @Override
    public void stop() {
        if (schedulerService != null) schedulerService.stop();
    }

    @Override
    public void reload() {
        // nothing
    }

    public SchedulerService getSchedulerService() { return schedulerService; }
}
