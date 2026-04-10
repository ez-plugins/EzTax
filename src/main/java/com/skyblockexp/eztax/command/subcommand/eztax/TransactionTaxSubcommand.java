package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class TransactionTaxSubcommand implements Subcommand {
    private final MessageManager messageManager;
    private final TaxConfig config;
    private final EzTaxPlugin plugin;

    public TransactionTaxSubcommand(EzTaxPlugin plugin, TaxConfig config, Messages messages) {
        this.plugin = plugin;
        this.config = config;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args == null || args.length < 2) {
            messageManager.send(sender, "transactiontax-usage");
            return true;
        }

        String action = args[1].toLowerCase(java.util.Locale.US);
        switch (action) {
            case "view": {
                double percent = config.getTransactionTaxPercent();
                double minimum = config.getTransactionTaxMinimumFee();
                boolean enabled = config.isTransactionTaxEnabled();
                messageManager.send(sender, "transactiontax-status");
                messageManager.send(sender, "transactiontax-show", Placeholder.parsed("rate", String.valueOf(percent)));
                messageManager.send(sender, "transactiontax-setmin-success", Placeholder.parsed("minimum", String.valueOf(minimum)));
                messageManager.send(sender, enabled ? "transactiontax-enabled" : "transactiontax-disabled");
                return true;
            }
            case "setpercent": {
                if (!sender.hasPermission("eztax.command.transactiontax.manage")) {
                    messageManager.send(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    messageManager.send(sender, "transactiontax-setpercent-usage");
                    return true;
                }
                try {
                    double rate = Double.parseDouble(args[2]);
                    plugin.getConfig().set("transaction-tax.percentage", rate);
                    plugin.saveConfig();
                    config.reload();
                    messageManager.send(sender, "transactiontax-setpercent-success", Placeholder.parsed("percent", String.valueOf(rate)));
                    return true;
                } catch (NumberFormatException e) {
                    messageManager.send(sender, "invalid-rate");
                    return true;
                }
            }
            case "setmin": {
                if (!sender.hasPermission("eztax.command.transactiontax.manage")) {
                    messageManager.send(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    messageManager.send(sender, "transactiontax-setmin-usage");
                    return true;
                }
                try {
                    double min = Double.parseDouble(args[2]);
                    plugin.getConfig().set("transaction-tax.minimum-fee", min);
                    plugin.saveConfig();
                    config.reload();
                    messageManager.send(sender, "transactiontax-setmin-success", Placeholder.parsed("minimum", String.valueOf(min)));
                    return true;
                } catch (NumberFormatException e) {
                    messageManager.send(sender, "invalid-rate");
                    return true;
                }
            }
            case "enable": {
                if (!sender.hasPermission("eztax.command.transactiontax.manage")) {
                    messageManager.send(sender, "no-permission");
                    return true;
                }
                plugin.getConfig().set("transaction-tax.enabled", true);
                plugin.saveConfig();
                config.reload();
                messageManager.send(sender, "transactiontax-enabled");
                return true;
            }
            case "disable": {
                if (!sender.hasPermission("eztax.command.transactiontax.manage")) {
                    messageManager.send(sender, "no-permission");
                    return true;
                }
                plugin.getConfig().set("transaction-tax.enabled", false);
                plugin.saveConfig();
                config.reload();
                messageManager.send(sender, "transactiontax-disabled");
                return true;
            }
            default:
                messageManager.send(sender, "transactiontax-unknown");
                return true;
        }
    }
}
