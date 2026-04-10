package com.skyblockexp.eztax.gui.action.pay;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.gui.GUI;
import com.skyblockexp.eztax.gui.IconClickAction;
import com.skyblockexp.eztax.gui.menu.PayGUI;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.UUID;

public class ConfirmAction implements IconClickAction {
    @Override
    public void onClick(Player player, ClickType click) {
        GUI g = GUI.fromInventory(player.getOpenInventory().getTopInventory());
        if (!(g instanceof PayGUI)) {
            player.sendMessage("No pay GUI in context");
            return;
        }
        PayGUI pay = (PayGUI) g;
        double amount = pay.getAmount();
        UUID targetUuid = pay.getTarget();
        if (targetUuid == null) {
            targetUuid = player.getUniqueId();
        }

        EzTaxPlugin plugin = EzTaxPlugin.getPlugin(EzTaxPlugin.class);
        MessageManager mm = new MessageManager(plugin, plugin.getMessages());

        // Permission checks: paying self vs paying others
        if (targetUuid.equals(player.getUniqueId())) {
            if (!player.hasPermission("eztax.command.paytax")) {
                mm.send(player, "no-permission");
                return;
            }
        } else {
            if (!player.hasPermission("eztax.command.paytax.others")) {
                mm.send(player, "no-permission");
                return;
            }
        }

        boolean ok = plugin.getTaxEngine().payTax(targetUuid, amount);
        OfflinePlayer target = org.bukkit.Bukkit.getOfflinePlayer(targetUuid);
        if (ok) {
            mm.send(player, "paytax-success", Placeholder.parsed("amount", plugin.getTaxEngine().format(amount)));
        } else {
            mm.send(player, "paytax-failed", Placeholder.parsed("player", target.getName() == null ? targetUuid.toString() : target.getName()));
        }
        pay.close(player);
    }
}
