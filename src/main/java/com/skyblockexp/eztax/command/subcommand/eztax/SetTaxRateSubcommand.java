package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class SetTaxRateSubcommand implements Subcommand {
    private final EzTaxPlugin plugin;
    private final TaxConfig config;
    private final MessageManager messageManager;

    public SetTaxRateSubcommand(EzTaxPlugin plugin, TaxConfig config, Messages messages) {
        this.plugin = plugin;
        this.config = config;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("eztax.command.settaxrate")) {
            messageManager.send(sender, "no-permission");
            return true;
        }
        if (args == null || args.length < 2) {
            messageManager.send(sender, "settaxrate-usage");
            return true;
        }

        try {
            double rate = Double.parseDouble(args[1]);
            if (rate < 0) {
                messageManager.send(sender, "invalid-rate");
                return true;
            }

            String group = args.length >= 3 ? args[2] : null;

            if (group == null) {
                plugin.getConfig().set("group-taxes.fallback-rate", rate);
                plugin.saveConfig();
                config.reload();
                messageManager.send(sender, "settaxrate-success",
                    Placeholder.parsed("rate", String.format(Locale.US, "%.2f", rate)),
                    Placeholder.parsed("target", "global fallback"));
            } else {
                plugin.getConfig().set("group-taxes.groups." + group + ".transaction-tax", rate);
                plugin.getConfig().set("group-taxes.groups." + group + ".wealth-tax", rate);
                plugin.saveConfig();
                config.reload();
                messageManager.send(sender, "settaxrate-success",
                    Placeholder.parsed("rate", String.format(Locale.US, "%.2f", rate)),
                    Placeholder.parsed("target", "group '" + group + "'"));
            }
            return true;
        } catch (NumberFormatException e) {
            messageManager.send(sender, "invalid-rate");
            return true;
        }
    }
}
