package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.repository.TaxHistoryRepository;
import com.skyblockexp.eztax.repository.TrackedPlayerRepository;
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
    private final TaxHistoryRepository taxHistoryRepository;
    private final TrackedPlayerRepository trackedPlayerRepository;
    private TaxEngine taxEngine;

    public TaxEngineComponent(EzTaxPlugin plugin, TaxConfig taxConfig, StatsService statsService,
                               ExemptionService exemptionService, VaultHook vaultHook,
                               TaxHistoryRepository taxHistoryRepository,
                               TrackedPlayerRepository trackedPlayerRepository) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
        this.statsService = statsService;
        this.exemptionService = exemptionService;
        this.vaultHook = vaultHook;
        this.taxHistoryRepository = taxHistoryRepository;
        this.trackedPlayerRepository = trackedPlayerRepository;
    }

    @Override
    public void start() {
        this.taxEngine = new TaxEngine(taxConfig, statsService, vaultHook, plugin.getLogger());
        this.taxEngine.setExemptionService(exemptionService);
        this.taxEngine.setTaxHistoryRepository(taxHistoryRepository);
        this.taxEngine.setTrackedPlayerRepository(trackedPlayerRepository);
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
