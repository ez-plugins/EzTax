package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.config.Messages;
import org.bukkit.command.CommandSender;

public class ReloadSubcommand implements Subcommand {
    private final EzTaxPlugin plugin;
    private final MessageManager messageManager;

    public ReloadSubcommand(EzTaxPlugin plugin, Messages messages) {
        this.plugin = plugin;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("eztax.command.reload")) {
            messageManager.send(sender, "no-permission");
            return true;
        }
        plugin.reloadEzTax();
        messageManager.send(sender, "reload-success");
        return true;
    }
}
