package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.registry.CommandRegistry;
import com.skyblockexp.eztax.repository.TaxHistoryRepository;
import com.skyblockexp.eztax.repository.TrackedPlayerRepository;

public class CommandComponent implements Component {
    private final EzTaxPlugin plugin;
    private final com.skyblockexp.eztax.config.TaxConfig taxConfig;
    private final com.skyblockexp.eztax.service.StatsService statsService;
    private final com.skyblockexp.eztax.service.TaxEngine taxEngine;
    private final com.skyblockexp.eztax.economy.VaultHook vaultHook;
    private final com.skyblockexp.eztax.config.Messages messages;
    private final com.skyblockexp.eztax.service.ExemptionService exemptionService;
    private final TaxHistoryRepository taxHistoryRepository;
    private final TrackedPlayerRepository trackedPlayerRepository;

    public CommandComponent(EzTaxPlugin plugin, com.skyblockexp.eztax.config.TaxConfig taxConfig, com.skyblockexp.eztax.service.StatsService statsService, com.skyblockexp.eztax.service.TaxEngine taxEngine, com.skyblockexp.eztax.economy.VaultHook vaultHook, com.skyblockexp.eztax.config.Messages messages, com.skyblockexp.eztax.service.ExemptionService exemptionService, TaxHistoryRepository taxHistoryRepository, TrackedPlayerRepository trackedPlayerRepository) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
        this.statsService = statsService;
        this.taxEngine = taxEngine;
        this.vaultHook = vaultHook;
        this.messages = messages;
        this.exemptionService = exemptionService;
        this.taxHistoryRepository = taxHistoryRepository;
        this.trackedPlayerRepository = trackedPlayerRepository;
    }

    @Override
    public void start() {
        // Create main executor and set executor/tab completer for primary commands
        com.skyblockexp.eztax.command.execute.EzTaxExecute executor = new com.skyblockexp.eztax.command.execute.EzTaxExecute(plugin, taxConfig, statsService, taxEngine, vaultHook, messages, exemptionService, taxHistoryRepository, trackedPlayerRepository);
        org.bukkit.command.PluginCommand cmd = plugin.getCommand("eztax");
        if (cmd != null) {
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }
        org.bukkit.command.PluginCommand alias = plugin.getCommand("tax");
        if (alias != null) {
            alias.setExecutor(executor);
            alias.setTabCompleter(executor);
        }
        org.bukkit.command.PluginCommand helpAlias = plugin.getCommand("taxhelp");
        if (helpAlias != null) {
            helpAlias.setExecutor((sender, c, label, args) -> {
                executor.onCommand(sender, c, label, new String[]{"help"});
                return true;
            });
        }

        // Register remaining commands via CommandRegistry
        com.skyblockexp.eztax.MessageManager messageManager = new com.skyblockexp.eztax.MessageManager(plugin, messages == null ? new com.skyblockexp.eztax.config.Messages(plugin) : messages);
        com.skyblockexp.eztax.registry.CommandRegistry registry = new com.skyblockexp.eztax.registry.CommandRegistry(plugin, taxConfig, statsService, taxEngine, messageManager, vaultHook, exemptionService, messages == null ? new com.skyblockexp.eztax.config.Messages(plugin) : messages);
        registry.registerCommands();
    }

    @Override
    public void stop() {
        // nothing
    }

    @Override
    public void reload() {
        // nothing
    }
}
