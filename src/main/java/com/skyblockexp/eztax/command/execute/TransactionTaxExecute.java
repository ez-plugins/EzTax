package com.skyblockexp.eztax.command.execute;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.CmdExecutor;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class TransactionTaxExecute extends CmdExecutor {
    private final MessageManager messageManager;
    private final TaxConfig config;
    private final EzTaxPlugin plugin;

    public TransactionTaxExecute(EzTaxPlugin plugin, TaxConfig config, Messages messages) {
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
            case "view":
                double value = config.getTransactionTaxPercent();
                messageManager.send(sender, "transactiontax-show", Placeholder.parsed("rate", String.valueOf(value)));
                return true;
            case "setpercent":
                if (args.length < 3) {
                    messageManager.send(sender, "transactiontax-usage");
                    return true;
                }
                try {
                    double rate = Double.parseDouble(args[2]);
                    plugin.getConfig().set("transaction-tax.percent", rate);
                    plugin.saveConfig();
                    config.reload();
                    messageManager.send(sender, "transactiontax-set", Placeholder.parsed("rate", String.valueOf(rate)));
                    return true;
                } catch (NumberFormatException e) {
                    messageManager.send(sender, "invalid-rate");
                    return true;
                }
            default:
                messageManager.send(sender, "transactiontax-usage");
                return true;
        }
    }
}
