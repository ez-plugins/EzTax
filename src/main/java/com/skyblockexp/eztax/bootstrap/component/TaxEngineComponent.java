package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.ExemptionService;
import com.skyblockexp.eztax.economy.VaultHook;
import com.skyblockexp.eztax.config.TaxConfig;

public class TaxEngineComponent implements Component {
    private final EzTaxPlugin plugin;
    private final TaxConfig taxConfig;
    private final StatsService statsService;
    private final ExemptionService exemptionService;
    private final VaultHook vaultHook;
    private TaxEngine taxEngine;

    public TaxEngineComponent(EzTaxPlugin plugin, TaxConfig taxConfig, StatsService statsService, ExemptionService exemptionService, VaultHook vaultHook) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
        this.statsService = statsService;
        this.exemptionService = exemptionService;
        this.vaultHook = vaultHook;
    }

    @Override
    public void start() {
        this.taxEngine = new TaxEngine(taxConfig, statsService, vaultHook, plugin.getLogger());
        this.taxEngine.setExemptionService(exemptionService);
    }

    @Override
    public void stop() {
        // nothing
    }

    @Override
    public void reload() {
        // nothing
    }

    public TaxEngine getTaxEngine() { return taxEngine; }
}
