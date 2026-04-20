package com.skyblockexp.eztax.bootstrap;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.bootstrap.component.*;
import com.skyblockexp.eztax.integration.EzSeasonsHook;

public class PluginBootstrap {
    private final EzTaxPlugin plugin;

    // components
    private ConfigComponent configComponent;
    private MetricsComponent metricsComponent;
    private EconomyComponent economyComponent;
    private StatsComponent statsComponent;
    private ExemptionComponent exemptionComponent;
    private TaxEngineComponent taxEngineComponent;
    private SchedulerComponent schedulerComponent;
    private CommandComponent commandComponent;
    private ListenerComponent listenerComponent;
    private EzSeasonsHook ezSeasonsHook;

    public PluginBootstrap(EzTaxPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // create components in order
        configComponent = new ConfigComponent(plugin);
        configComponent.start();

        metricsComponent = new MetricsComponent(plugin);
        metricsComponent.start();

        statsComponent = new StatsComponent(plugin, configComponent.getTaxConfig());
        statsComponent.start();

        exemptionComponent = new ExemptionComponent(plugin);
        exemptionComponent.start();

        economyComponent = new EconomyComponent(plugin, configComponent.getTaxConfig());
        economyComponent.start();

        if (economyComponent.isHooked()) {
            taxEngineComponent = new TaxEngineComponent(plugin, configComponent.getTaxConfig(), statsComponent.getStatsService(), exemptionComponent.getExemptionService(), economyComponent.getVaultHook(), statsComponent.getTaxHistoryRepository(), statsComponent.getTrackedPlayerRepository());
            taxEngineComponent.start();

            economyComponent.registerTaxEngine(taxEngineComponent.getTaxEngine());

            // Ensure TaxEngine holds the current economy implementation (taxed wrapper)
            if (taxEngineComponent.getTaxEngine() != null && economyComponent.getVaultHook() != null) {
                taxEngineComponent.getTaxEngine().setEconomy(economyComponent.getVaultHook().getEconomy());
            }

            listenerComponent = new ListenerComponent(plugin, configComponent.getTaxConfig(), taxEngineComponent.getTaxEngine());
            listenerComponent.start();

            schedulerComponent = new SchedulerComponent(plugin, configComponent.getTaxConfig(), taxEngineComponent.getTaxEngine());
            schedulerComponent.start();

            ezSeasonsHook = new EzSeasonsHook(plugin, statsComponent.getStatsService());
            ezSeasonsHook.hook();
        } else {
            plugin.getLogger().warning("Vault economy was not detected. EzTax will run in standby mode.");
        }

        commandComponent = new CommandComponent(plugin, configComponent.getTaxConfig(), statsComponent.getStatsService(), taxEngineComponent != null ? taxEngineComponent.getTaxEngine() : null, economyComponent.getVaultHook(), configComponent.getMessages(), exemptionComponent.getExemptionService(), statsComponent.getTaxHistoryRepository(), statsComponent.getTrackedPlayerRepository());
        commandComponent.start();

        plugin.getLogger().info("EzTax v" + plugin.getPluginMeta().getVersion() + " enabled.");
    }

    public void stop() {
        if (ezSeasonsHook != null) ezSeasonsHook.unhook();
        if (schedulerComponent != null) schedulerComponent.stop();
        if (listenerComponent != null) listenerComponent.stop();
        if (taxEngineComponent != null) taxEngineComponent.stop();
        if (economyComponent != null) economyComponent.stop();
        if (exemptionComponent != null) exemptionComponent.stop();
        if (statsComponent != null) statsComponent.stop();
        if (metricsComponent != null) metricsComponent.stop();
        if (configComponent != null) configComponent.stop();
        if (commandComponent != null) commandComponent.stop();
    }

    public void reload() {
        // naive reload: stop then start
        stop();
        start();
    }

    // getters for components (useful for tests)
    public ConfigComponent getConfigComponent() { return configComponent; }
    public StatsComponent getStatsComponent() { return statsComponent; }
    public EconomyComponent getEconomyComponent() { return economyComponent; }
    public TaxEngineComponent getTaxEngineComponent() { return taxEngineComponent; }
    public ExemptionComponent getExemptionComponent() { return exemptionComponent; }
    public SchedulerComponent getSchedulerComponent() { return schedulerComponent; }
    public CommandComponent getCommandComponent() { return commandComponent; }
    public ListenerComponent getListenerComponent() { return listenerComponent; }
    public MetricsComponent getMetricsComponent() { return metricsComponent; }
    public EzTaxPlugin getPlugin() { return plugin; }

    // convenience getters for common objects
    public com.skyblockexp.eztax.config.TaxConfig getTaxConfig() {
        return configComponent != null ? configComponent.getTaxConfig() : null;
    }

    public com.skyblockexp.eztax.config.Messages getMessages() {
        return configComponent != null ? configComponent.getMessages() : new com.skyblockexp.eztax.config.Messages(plugin);
    }

    public com.skyblockexp.eztax.service.StatsService getStatsService() {
        return statsComponent != null ? statsComponent.getStatsService() : null;
    }

    public com.skyblockexp.eztax.economy.VaultHook getVaultHook() {
        return economyComponent != null ? economyComponent.getVaultHook() : null;
    }
}
