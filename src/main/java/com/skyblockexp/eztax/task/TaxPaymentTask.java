package com.skyblockexp.eztax.task;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.MessageManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

public class TaxPaymentTask implements Runnable {
    private final EzTaxPlugin plugin;
    private final TaxEngine taxEngine;
    private final MessageManager messageManager;
    private final CommandSender sender;

    public TaxPaymentTask(EzTaxPlugin plugin, TaxEngine taxEngine, MessageManager messageManager, CommandSender sender) {
        this.plugin = plugin;
        this.taxEngine = taxEngine;
        this.messageManager = messageManager;
        this.sender = sender;
    }

    @Override
    public void run() {
        int count = 0;
        for (OfflinePlayer playerObj : Bukkit.getOfflinePlayers()) {
            if (playerObj != null && playerObj.hasPlayedBefore()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> taxEngine.handleTaxPayment(playerObj));
                count++;
            }
        }
        if (count == 0) {
            messageManager.send(sender, "no-players");
        } else {
            messageManager.send(sender, "tax-payment-manual", net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed("count", String.valueOf(count)));
        }
    }
}
