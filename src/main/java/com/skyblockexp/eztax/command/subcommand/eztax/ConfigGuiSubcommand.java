package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.gui.menu.AdminConfigGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigGuiSubcommand implements Subcommand {
    private final EzTaxPlugin plugin;

    public ConfigGuiSubcommand(EzTaxPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command may only be run by a player.");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("eztax.command.config")) {
            p.sendMessage("You do not have permission to open the config GUI.");
            return true;
        }
        new AdminConfigGUI(plugin).open(p);
        return true;
    }
}
