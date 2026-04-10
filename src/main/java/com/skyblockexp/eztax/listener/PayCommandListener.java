package com.skyblockexp.eztax.listener;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.TaxEngine;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PayCommandListener implements Listener {
    private final TaxConfig config;
    private final TaxEngine taxEngine;

    public PayCommandListener(TaxConfig config, TaxEngine taxEngine) {
        this.config = config;
        this.taxEngine = taxEngine;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if (message == null) return;
        String trimmed = message.trim();
        String lower = trimmed.toLowerCase();

        // Build command match set from config (allow aliases)
        java.util.List<String> commands = config.getTransactionChatCommands();
        boolean matches = false;
        for (String cmd : commands) {
            if (cmd == null || cmd.isEmpty()) continue;
            String c = cmd.toLowerCase();
            if (lower.equals("/" + c) || lower.startsWith("/" + c + " ")) {
                matches = true;
                break;
            }
        }
        if (!matches) return;

        String[] parts = trimmed.split("\\s+");
        if (parts.length < 3) return; // not enough args, let normal handler open GUI or fail

        Player sender = event.getPlayer();
        String targetName = parts[1];
        String amountStr = parts[2];
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException ex) {
            sender.sendMessage("Invalid amount: " + amountStr);
            event.setCancelled(true);
            return;
        }
        if (amount <= 0) {
            sender.sendMessage("Amount must be positive.");
            event.setCancelled(true);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || (target.getName() == null && !target.isOnline())) {
            sender.sendMessage("Player not found: " + targetName);
            event.setCancelled(true);
            return;
        }

        Economy econ = taxEngine.getEconomy();
        if (econ == null) {
            sender.sendMessage("Economy unavailable.");
            return;
        }

        // Decide where to apply tax: withdraw preferred, else deposit
        if (config.isTransactionTaxApplyOnWithdraw()) {
            double tax = taxEngine.calculateTransactionTaxForPlayer(sender, amount);
            double total = amount + tax;
            EconomyResponse resp = econ.withdrawPlayer(sender, total);
            if (!resp.transactionSuccess()) {
                sender.sendMessage("Payment failed: " + resp.errorMessage);
                event.setCancelled(true);
                return;
            }
            econ.depositPlayer(target, amount);
            taxEngine.recordTransactionTax(sender.getUniqueId(), tax);
            sender.sendMessage(String.format("Paid %s to %s (tax %.2f)", taxEngine.format(amount), targetName, tax));
            if (target.isOnline() && target.getPlayer() != null) {
                target.getPlayer().sendMessage(String.format("Received %s from %s", taxEngine.format(amount), sender.getName()));
            }
            event.setCancelled(true);
            return;
        }

        if (config.isTransactionTaxApplyOnDeposit()) {
            EconomyResponse resp = econ.withdrawPlayer(sender, amount);
            if (!resp.transactionSuccess()) {
                sender.sendMessage("Payment failed: " + resp.errorMessage);
                event.setCancelled(true);
                return;
            }
            double tax = taxEngine.calculateTransactionTaxForPlayer(sender, amount);
            double net = amount - tax;
            if (net <= 0) {
                sender.sendMessage("Payment amount too small after tax.");
                event.setCancelled(true);
                return;
            }
            econ.depositPlayer(target, net);
            taxEngine.recordTransactionTax(sender.getUniqueId(), tax);
            sender.sendMessage(String.format("Paid %s to %s (tax %.2f)", taxEngine.format(net), targetName, tax));
            if (target.isOnline() && target.getPlayer() != null) {
                target.getPlayer().sendMessage(String.format("Received %s from %s", taxEngine.format(net), sender.getName()));
            }
            event.setCancelled(true);
        }
    }
}
