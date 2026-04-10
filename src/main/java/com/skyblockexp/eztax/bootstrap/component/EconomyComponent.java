package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.economy.VaultHook;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.config.TaxConfig;

public class EconomyComponent implements Component {
    private final EzTaxPlugin plugin;
    private VaultHook vaultHook;
    private final TaxConfig taxConfig;
    private boolean hooked = false;

    public EconomyComponent(EzTaxPlugin plugin, TaxConfig taxConfig) { this.plugin = plugin; this.taxConfig = taxConfig; }

    @Override
    public void start() {
        this.vaultHook = new VaultHook(plugin);
        this.hooked = vaultHook.hook();
    }

    @Override
    public void stop() {
        if (vaultHook != null) vaultHook.unregisterTaxedEconomy();
    }

    @Override
    public void reload() {
        // nothing
    }

    public boolean isHooked() { return hooked; }

    public void registerTaxEngine(TaxEngine taxEngine) {
        if (vaultHook != null && hooked) {
            // If config requests command-capture for transaction tax, do not register the taxed economy wrapper.
            if (taxConfig != null && taxConfig.isTransactionTaxCaptureCommands()) {
                plugin.getLogger().info("Transaction tax will be handled by command capture (not registering taxed economy wrapper).");
                return;
            }
            vaultHook.registerTaxedEconomy(taxEngine, taxConfig);
        }
    }

    public VaultHook getVaultHook() { return vaultHook; }
}
