package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.config.GuiConfig;
import com.skyblockexp.eztax.gui.menu.PayGUI;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.service.TaxEngine;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PaySubcommand implements Subcommand {
    private final EzTaxPlugin plugin;
    private final TaxEngine taxEngine;
    private final MessageManager messageManager;
    private final GuiConfig guiConfig;

    public PaySubcommand(EzTaxPlugin plugin, TaxEngine taxEngine, Messages messages, GuiConfig guiConfig) {
        this.plugin = plugin;
        this.taxEngine = taxEngine;
        this.messageManager = new MessageManager(plugin, messages);
        this.guiConfig = guiConfig;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        OfflinePlayer target;
        // If player executed without target, open GUI
        if (sender instanceof org.bukkit.entity.Player && (args == null || args.length < 2)) {
            org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
            var cfg = guiConfig.getConfig().getConfigurationSection("menus").getConfigurationSection("pay");
            String title = cfg.getString("title", "EzTax - Pay Tax");
            int size = cfg.getInt("size", 27);
            new PayGUI(plugin, title, size).open(p);
            return true;
        }

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

        double due = taxEngine.getTaxDue(target.getUniqueId());
        if (due <= 0) {
            messageManager.send(sender, "no-taxes-due");
            return true;
        }

        boolean ok = taxEngine.payTax(target.getUniqueId(), due);
        if (ok) {
            messageManager.send(sender, "paytax-success", Placeholder.parsed("amount", taxEngine.format(due)));
        } else {
            messageManager.send(sender, "paytax-failed", Placeholder.parsed("player", target.getName()));
        }
        return true;
    }
}
