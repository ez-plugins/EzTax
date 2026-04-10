package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.TaxSink;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.Map;

public class SinksSubcommand implements Subcommand {
    private final EzTaxPlugin plugin;
    private final StatsService statsService;
    private final MessageManager messageManager;

    public SinksSubcommand(EzTaxPlugin plugin, StatsService statsService, Messages messages) {
        this.plugin = plugin;
        this.statsService = statsService;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("eztax.command.sinks")) {
            messageManager.send(sender, "no-permission");
            return true;
        }
        if (sender instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
            var cfg = plugin.getGuiConfig().getConfig().getConfigurationSection("menus").getConfigurationSection("tax");
            String title = cfg.getString("title", "EzTax - Tax Overview");
            int size = cfg.getInt("size", 27);
            new com.skyblockexp.eztax.gui.menu.SinksGUI(statsService, title, size).open(p);
            return true;
        }

        messageManager.send(sender, "sinks-header");
        Map<TaxSink, Double> sinks = statsService.getSinkTotals();
        for (Map.Entry<TaxSink, Double> entry : sinks.entrySet()) {
            messageManager.send(sender, "sinks-line",
                    Placeholder.parsed("type", entry.getKey().name()),
                    Placeholder.parsed("amount", String.format(Locale.US, "%.2f", entry.getValue())));
        }
        return true;
    }
}
