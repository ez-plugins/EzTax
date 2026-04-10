package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.service.StatsService;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class StatsSubcommand implements Subcommand {
    private final EzTaxPlugin plugin;
    private final StatsService statsService;
    private final MessageManager messageManager;

    public StatsSubcommand(EzTaxPlugin plugin, StatsService statsService, Messages messages) {
        this.plugin = plugin;
        this.statsService = statsService;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("eztax.command.stats")) {
            messageManager.send(sender, "no-permission");
            return true;
        }
        if (sender instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
            var cfg = plugin.getGuiConfig().getConfig().getConfigurationSection("menus").getConfigurationSection("tax");
            String title = cfg.getString("title", "EzTax - Tax Overview");
            int size = cfg.getInt("size", 27);
            new com.skyblockexp.eztax.gui.menu.TaxGUI(plugin, statsService, plugin == null ? null : plugin.getTaxEngine(), plugin.getGuiConfig(), title, size).open(p);
            return true;
        }

        messageManager.send(sender, "stats-header");
        messageManager.send(sender, "stats-line", Placeholder.parsed("type", "Total"), Placeholder.parsed("amount", String.format(Locale.US, "%.2f", statsService.getTotalRemoved())));
        messageManager.send(sender, "stats-line", Placeholder.parsed("type", "Daily"), Placeholder.parsed("amount", String.format(Locale.US, "%.2f", statsService.getDailyRemoved())));
        messageManager.send(sender, "stats-line", Placeholder.parsed("type", "Weekly"), Placeholder.parsed("amount", String.format(Locale.US, "%.2f", statsService.getWeeklyRemoved())));
        messageManager.send(sender, "stats-line", Placeholder.parsed("type", "Treasury"), Placeholder.parsed("amount", String.format(Locale.US, "%.2f", statsService.getTreasuryBalance())));
        return true;
    }
}
