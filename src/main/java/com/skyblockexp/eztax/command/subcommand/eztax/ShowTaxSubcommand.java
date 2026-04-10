package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.service.TaxEngine;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowTaxSubcommand implements Subcommand {
    private final EzTaxPlugin plugin;
    private final TaxEngine taxEngine;
    private final MessageManager messageManager;

    public ShowTaxSubcommand(EzTaxPlugin plugin, TaxEngine taxEngine, Messages messages) {
        this.plugin = plugin;
        this.taxEngine = taxEngine;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        OfflinePlayer target;
        if (args != null && args.length >= 2) {
            @SuppressWarnings("deprecation")
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            if (p == null || (p.getName() == null && !p.isOnline())) {
                messageManager.send(sender, "player-not-found", Placeholder.parsed("player", args[1]));
                return true;
            }
            target = p;
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            messageManager.send(sender, "no-permission");
            return true;
        }

        var breakdown = taxEngine.getTaxBreakdown(target.getUniqueId());
        messageManager.send(sender, "showtax-header");
        for (var e : breakdown.entrySet()) {
            messageManager.send(sender, "showtax-line",
                Placeholder.parsed("type", e.getKey()),
                Placeholder.parsed("amount", taxEngine.format(e.getValue())));
        }
        return true;
    }
}
