package com.skyblockexp.eztax.registry;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.command.execute.TransactionTaxExecute;
import com.skyblockexp.eztax.command.execute.ExemptExecute;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.economy.VaultHook;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.ExemptionService;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.MessageManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.CommandExecutor;

public class CommandRegistry {
    private final EzTaxPlugin plugin;
    public final TaxConfig taxConfig;
    private final StatsService statsService;
    private final TaxEngine taxEngine;
    private final MessageManager messageManager;
    private final VaultHook vaultHook;
    private final ExemptionService exemptionService;
    public final Messages messages;

    public CommandRegistry(EzTaxPlugin plugin, TaxConfig taxConfig, StatsService statsService, TaxEngine taxEngine, MessageManager messageManager, VaultHook vaultHook, ExemptionService exemptionService, Messages messages) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
        this.statsService = statsService;
        this.taxEngine = taxEngine;
        this.messageManager = messageManager;
        this.vaultHook = vaultHook;
        this.exemptionService = exemptionService;
        this.messages = messages;
    }

    public void registerCommands() {
        // Note: the main /eztax command is registered with its full executor in CommandComponent.
        // This registry handles only the standalone convenience commands below.

        // Keep core top-level commands for convenience (wrap Subcommands into CommandExecutors)
        register("exempt", new com.skyblockexp.eztax.command.CmdExecutor() {
            private final com.skyblockexp.eztax.command.Subcommand delegate = new ExemptExecute(plugin, exemptionService, messages);
            @Override
            public boolean execute(org.bukkit.command.CommandSender sender, String[] args) {
                return delegate.execute(sender, args == null ? new String[0] : args);
            }
        });

        register("transactiontax", new com.skyblockexp.eztax.command.CmdExecutor() {
            private final com.skyblockexp.eztax.command.Subcommand delegate = new TransactionTaxExecute(plugin, taxConfig, messages);
            @Override
            public boolean execute(org.bukkit.command.CommandSender sender, String[] args) {
                return delegate.execute(sender, args == null ? new String[0] : args);
            }
        });
    }

    private void register(String name, CommandExecutor executor) {
        PluginCommand cmd = plugin.getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
        }
    }
}
