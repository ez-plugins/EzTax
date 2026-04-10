package com.skyblockexp.eztax.registry;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.economy.VaultHook;
import com.skyblockexp.eztax.service.ExemptionService;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.MessageManager;

public class Registry {
    private final CommandRegistry commandRegistry;
    private final ListenerRegistry listenerRegistry;
    private final MetricsRegistry metricsRegistry;
    private final TaxEngine taxEngine;
    private final MessageManager messageManager;

    public Registry(EzTaxPlugin plugin, TaxConfig taxConfig, StatsService statsService, TaxEngine taxEngine, MessageManager messageManager, VaultHook vaultHook, ExemptionService exemptionService, Messages messages) {
        this.taxEngine = taxEngine;
        this.messageManager = messageManager;
        this.commandRegistry = new CommandRegistry(plugin, taxConfig, statsService, taxEngine, messageManager, vaultHook, exemptionService, messages);
        this.listenerRegistry = new ListenerRegistry(plugin, taxConfig, taxEngine);
        this.metricsRegistry = new MetricsRegistry(plugin, 28537);
    }

    public void loadAll() {
        listenerRegistry.registerListeners();
        commandRegistry.registerCommands();
        metricsRegistry.registerMetrics();
    }
    
    public TaxConfig getTaxConfig() {
        return commandRegistry != null ? commandRegistry.taxConfig : null;
    }

    public Messages getMessages() {
        return commandRegistry != null ? commandRegistry.messages : null;
    }
}
