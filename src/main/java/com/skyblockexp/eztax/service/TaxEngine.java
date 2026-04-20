package com.skyblockexp.eztax.service;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.economy.VaultHook;
import com.skyblockexp.eztax.repository.TaxHistoryRepository;
import com.skyblockexp.eztax.repository.TrackedPlayerRepository;
import com.skyblockexp.eztax.service.TaxSink;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.logging.Logger;

public class TaxEngine {
    private final TaxConfig config;
    private final StatsService statsService;
    private final VaultHook vaultHook;
    private Economy economy;
    private final Logger logger;
    private ExemptionService exemptionService;
    private TaxHistoryRepository taxHistoryRepository;
    private TrackedPlayerRepository trackedPlayerRepository;
        // --- Missing method stubs for compilation ---

        public void handleTaxPayment(org.bukkit.OfflinePlayer player) {
            // Example: Apply wealth tax and inactivity fee
            applyWealthTax(player);
            applyInactivityFee(player);
        }

        public java.util.Map<String, Double> getFineBreakdown(java.util.UUID playerId) {
            java.util.Map<String, Double> breakdown = new java.util.HashMap<>();
            org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(playerId);
            if (player != null) {
                breakdown.put("inactivity", calculateInactivityFee(player));
                breakdown.put("death", 0.0);
            }
            return breakdown;
        }

        public double getFineDue(java.util.UUID playerId) {
            // Example: Sum all fines (dummy)
            java.util.Map<String, Double> breakdown = getFineBreakdown(playerId);
            return breakdown.values().stream().mapToDouble(Double::doubleValue).sum();
        }

        public double getTaxDue(java.util.UUID playerId) {
            // Example: Sum all taxes (dummy)
            java.util.Map<String, Double> breakdown = getTaxBreakdown(playerId);
            return breakdown.values().stream().mapToDouble(Double::doubleValue).sum();
        }

        public boolean payTax(java.util.UUID playerId, double amount) {
            // Example: Withdraw from player economy
            org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(playerId);
            if (player == null || amount <= 0) return false;
            EconomyResponse response = economy.withdrawPlayer(player, amount);
            if (response.transactionSuccess()) {
                statsService.recordTax(TaxSink.TRANSACTION, amount);
                return true;
            }
            return false;
        }

        public boolean payFine(java.util.UUID playerId, double amount) {
            // Example: Withdraw from player economy
            org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(playerId);
            if (player == null || amount <= 0) return false;
            EconomyResponse response = economy.withdrawPlayer(player, amount);
            if (response.transactionSuccess()) {
                statsService.recordTax(TaxSink.INACTIVITY, amount);
                return true;
            }
            return false;
        }

        public java.util.Map<String, Double> getTaxBreakdown(java.util.UUID playerId) {
            java.util.Map<String, Double> breakdown = new java.util.HashMap<>();
            org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(playerId);
            if (player != null) {
                breakdown.put("wealth", calculateWealthTax(player));
                breakdown.put("transaction", 0.0);
            }
            return breakdown;
        }

        public double calculateProgressiveTax(double taxableAmount, java.util.List<Object> brackets, double defaultPercent, String type) {
            if (taxableAmount <= 0) return 0.0;
            if (brackets == null || brackets.isEmpty()) {
                return roundCurrency(taxableAmount * (defaultPercent / 100.0));
            }

            // Brackets expected to be instances of TaxConfig.WealthBracket (or maps).
            double remaining = taxableAmount;
            double prevLimit = 0.0;
            double tax = 0.0;

            for (Object o : brackets) {
                if (o == null) continue;
                double limit;
                double percent;
                if (o instanceof com.skyblockexp.eztax.config.TaxConfig.WealthBracket) {
                    com.skyblockexp.eztax.config.TaxConfig.WealthBracket wb = (com.skyblockexp.eztax.config.TaxConfig.WealthBracket) o;
                    limit = wb.getLimit();
                    percent = wb.getPercent();
                } else if (o instanceof java.util.Map) {
                    java.util.Map<?,?> m = (java.util.Map<?,?>) o;
                    Object l = m.get("limit");
                    Object p = m.get("percent");
                    limit = l instanceof Number ? ((Number) l).doubleValue() : -1.0;
                    percent = p instanceof Number ? ((Number) p).doubleValue() : defaultPercent;
                } else {
                    continue;
                }

                if (limit <= prevLimit) continue; // skip invalid

                double upper = Math.min(limit, taxableAmount);
                double inBracket = Math.max(0.0, upper - prevLimit);
                if (inBracket > 0) {
                    tax += inBracket * (percent / 100.0);
                    remaining -= inBracket;
                }
                prevLimit = limit;
                if (remaining <= 0) break;
            }

            // Any remaining amount above highest bracket is taxed at defaultPercent
            if (remaining > 0) {
                tax += remaining * (defaultPercent / 100.0);
            }

            return roundCurrency(tax);
        }

    public TaxEngine(TaxConfig config, StatsService statsService, VaultHook vaultHook, Logger logger) {
        this.config = config;
        this.statsService = statsService;
        this.vaultHook = vaultHook;
        this.economy = vaultHook != null ? vaultHook.getEconomy() : null;
        this.logger = logger;
    }

    /**
     * Update the underlying economy implementation (used to switch to the taxed wrapper after
     * registration in testing/runtime initialization).
     */
    public void setEconomy(Economy economy) {
        this.economy = economy;
    }
    
    public void setExemptionService(ExemptionService exemptionService) {
        this.exemptionService = exemptionService;
    }

    public void setTaxHistoryRepository(TaxHistoryRepository taxHistoryRepository) {
        this.taxHistoryRepository = taxHistoryRepository;
    }

    public void setTrackedPlayerRepository(TrackedPlayerRepository trackedPlayerRepository) {
        this.trackedPlayerRepository = trackedPlayerRepository;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void recordTransactionTax(String playerName, double amount) {
        if (amount <= 0) {
            return;
        }
        statsService.recordTax(TaxSink.TRANSACTION, amount);
        debug(String.format(Locale.US, "Transaction tax recorded for %s: %.2f", playerName, amount));
    }

    public void recordTransactionTax(UUID playerId, double amount) {
        recordTransactionTax(playerId.toString(), amount);
    }

    public void applyWealthTax(OfflinePlayer player) {
        logger.info("Wealth tax enabled=" + config.isWealthTaxEnabled() + " threshold=" + config.getWealthTaxThreshold() + " percent=" + config.getWealthTaxPercent());
        logger.info("Wealth brackets count=" + (config.getWealthTaxBrackets() != null ? config.getWealthTaxBrackets().size() : 0));
        if (!config.isWealthTaxEnabled()) {
            logger.info("Wealth tax disabled, skipping");
            return;
        }
        
        // Check exemption
        if (exemptionService != null && exemptionService.isExempt(player.getUniqueId())) {
            debug("Wealth tax skipped for " + player.getName() + " (exempt)");
            return;
        }
        
        double balance = economy.getBalance(player);
        logger.info(String.format(java.util.Locale.US, "Player %s balance=%.2f threshold=%.2f", player.getName(), balance, config.getWealthTaxThreshold()));
        if (balance <= config.getWealthTaxThreshold()) {
            return;
        }
        double taxableAmount = balance - config.getWealthTaxThreshold();
        
        // Get tax percentage based on group if enabled
        double taxPercent = config.getWealthTaxPercent();
        if (config.isGroupTaxesEnabled() && player.isOnline() && player.getPlayer() != null) {
            String group = vaultHook.getPrimaryGroup(player.getPlayer());
            taxPercent = config.getWealthTaxPercentForGroup(group);
            if (group != null) {
                debug(String.format(Locale.US, "Using group '%s' wealth tax rate: %.2f%%", group, taxPercent));
            } else {
                debug("No group found for player, using fallback wealth tax rate: " + taxPercent + "%");
            }
        }
        
        java.util.List<Object> brackets = config.getWealthTaxBrackets();
        double tax;
        if (brackets != null && !brackets.isEmpty()) {
            tax = calculateProgressiveTax(taxableAmount, brackets, taxPercent, "wealth");
        } else {
            tax = roundCurrency(taxableAmount * (taxPercent / 100.0D));
        }
        if (tax <= 0) {
            return;
        }
        logger.info(String.format(java.util.Locale.US, "Applying wealth tax to %s: tax=%.2f, balance=%.2f", player.getName(), tax, economy != null ? economy.getBalance(player) : 0.0));
        withdrawSafely(player, tax, TaxSink.WEALTH);
    }

    public void applyInactivityFee(OfflinePlayer player) {
        if (!config.isInactivityFeeEnabled()) {
            return;
        }
        
        // Check exemption
        if (exemptionService != null && exemptionService.isExempt(player.getUniqueId())) {
            debug("Inactivity fee skipped for " + player.getName() + " (exempt)");
            return;
        }
        
        double balance = economy.getBalance(player);
        if (balance <= 0) {
            return;
        }
        double fee;
        if (config.getInactivityPercentage() > 0) {
            fee = roundCurrency(balance * (config.getInactivityPercentage() / 100.0D));
        } else {
            fee = config.getInactivityFlatFee();
        }
        if (fee <= 0) {
            return;
        }
        withdrawSafely(player, fee, TaxSink.INACTIVITY);
    }

    public void applyDeathFee(Player player) {
        if (!config.isDeathFeeEnabled()) {
            return;
        }
        
        // Check exemption
        if (exemptionService != null && exemptionService.isExempt(player.getUniqueId())) {
            debug("Death fee skipped for " + player.getName() + " (exempt)");
            return;
        }
        
        if (config.getDeathFeeDisabledWorlds().contains(player.getWorld().getName())) {
            return;
        }
        double balance = economy.getBalance(player);
        if (balance <= 0) {
            return;
        }
        double fee = roundCurrency(balance * (config.getDeathFeePercent() / 100.0D));
        if (config.getDeathFeeMaxLoss() > 0) {
            fee = Math.min(fee, config.getDeathFeeMaxLoss());
        }
        if (fee <= 0) {
            return;
        }
        withdrawSafely(player, fee, TaxSink.DEATH);
    }

    private double calculateWealthTax(OfflinePlayer player) {
        if (!config.isWealthTaxEnabled()) {
            return 0.0;
        }
        
        // Check exemption
        if (exemptionService != null && exemptionService.isExempt(player.getUniqueId())) {
            return 0.0;
        }
        
        double balance = economy.getBalance(player);
        if (balance <= config.getWealthTaxThreshold()) {
            return 0.0;
        }
        double taxableAmount = balance - config.getWealthTaxThreshold();
        
        // Get tax percentage based on group if enabled
        double taxPercent = config.getWealthTaxPercent();
        if (config.isGroupTaxesEnabled() && player.isOnline() && player.getPlayer() != null) {
            String group = vaultHook.getPrimaryGroup(player.getPlayer());
            taxPercent = config.getWealthTaxPercentForGroup(group);
        }
        
        java.util.List<Object> brackets = config.getWealthTaxBrackets();
        if (brackets != null && !brackets.isEmpty()) {
            return calculateProgressiveTax(taxableAmount, brackets, taxPercent, "wealth");
        }
        return roundCurrency(taxableAmount * (taxPercent / 100.0D));
    }

    private double calculateInactivityFee(OfflinePlayer player) {
        if (!config.isInactivityFeeEnabled()) {
            return 0.0;
        }
        
        // Check exemption
        if (exemptionService != null && exemptionService.isExempt(player.getUniqueId())) {
            return 0.0;
        }
        
        double balance = economy.getBalance(player);
        if (balance <= 0) {
            return 0.0;
        }
        double fee;
        if (config.getInactivityPercentage() > 0) {
            fee = roundCurrency(balance * (config.getInactivityPercentage() / 100.0D));
        } else {
            fee = config.getInactivityFlatFee();
        }
        return fee;
    }

    public String format(double amount) {
        return economy != null ? economy.format(amount) : String.format(Locale.US, "%.2f", amount);
    }

    public double calculateTransactionTax(double withdrawalAmount) {
        double percentFee = withdrawalAmount * (config.getTransactionTaxPercent() / 100.0D);
        double tax = Math.max(percentFee, config.getTransactionTaxMinimumFee());
        return roundCurrency(tax);
    }
    
    public double calculateTransactionTaxForPlayer(Player player, double withdrawalAmount) {
        // Check exemption
        if (exemptionService != null && exemptionService.isExempt(player.getUniqueId())) {
            debug("Transaction tax skipped for " + player.getName() + " (exempt)");
            return 0.0;
        }
        
        if (!config.isGroupTaxesEnabled() || player == null) {
            return calculateTransactionTax(withdrawalAmount);
        }
        
        String group = vaultHook.getPrimaryGroup(player);
        double taxPercent = config.getTransactionTaxPercentForGroup(group);
        
        if (group != null) {
            debug(String.format(Locale.US, "Using group '%s' transaction tax rate: %.2f%%", group, taxPercent));
        } else {
            debug("No group found for player, using fallback transaction tax rate: " + taxPercent + "%");
        }
        
        double percentFee = withdrawalAmount * (taxPercent / 100.0D);
        double tax = Math.max(percentFee, config.getTransactionTaxMinimumFee());
        return roundCurrency(tax);
    }

    private void withdrawSafely(OfflinePlayer player, double amount, TaxSink sink) {
        double balance = economy.getBalance(player);
        if (amount > balance) {
            debug(String.format(Locale.US, "%s tax skipped for %s (insufficient balance).", sink.name(), player.getUniqueId()));
            return;
        }
        logger.info(String.format(Locale.US, "Attempting withdraw for %s: amount=%.2f balance=%.2f", player.getName(), amount, balance));
        EconomyResponse response = economy.withdrawPlayer(player, amount);
        logger.info(String.format(Locale.US, "Withdraw response for %s: success=%s, balance=%.2f, msg=%s", player.getName(), response.transactionSuccess(), response.balance, response.errorMessage));
        if (!response.transactionSuccess()) {
            debug(String.format(Locale.US, "%s tax failed for %s (%s).", sink.name(), player.getUniqueId(), response.errorMessage));
            return;
        }
        logger.info(String.format(Locale.US, "Recording sink %s amount=%.2f", sink.name(), amount));
        statsService.recordTax(sink, amount);
        if (taxHistoryRepository != null) {
            try {
                taxHistoryRepository.record(player.getUniqueId(), player.getName(), sink, amount, balance);
            } catch (Exception e) {
                logger.warning("[EzTax] Failed to record tax history: " + e.getMessage());
            }
        }
        if (trackedPlayerRepository != null) {
            try {
                trackedPlayerRepository.upsert(player.getUniqueId(), player.getName(), amount);
            } catch (Exception e) {
                logger.warning("[EzTax] Failed to update tracked player: " + e.getMessage());
            }
        }
        distributeTax(amount);
        debug(String.format(Locale.US, "%s tax applied to %s: %.2f", sink.name(), player.getUniqueId(), amount));
    }

    /**
     * Route the collected {@code amount} to the configured {@link SinkDestination}.
     *
     * <ul>
     *   <li>{@code burn}    — money is already removed; nothing extra happens.</li>
     *   <li>{@code player}  — money is deposited into the named player's account.</li>
     *   <li>{@code pool}    — money is split equally among all currently online players.</li>
     *   <li>{@code command} — a console command is executed with {@code %amount%} replaced.</li>
     * </ul>
     */
    private void distributeTax(double amount) {
        if (amount <= 0) {
            return;
        }
        SinkDestination dest = SinkDestination.fromString(config.getSinkDestination());
        switch (dest) {
            case BURN:
                // Money is already withdrawn; nothing else to do.
                break;
            case PLAYER: {
                String name = config.getSinkTargetPlayer();
                if (name != null && !name.isEmpty()) {
                    @SuppressWarnings("deprecation")
                    OfflinePlayer target = Bukkit.getOfflinePlayer(name);
                    economy.depositPlayer(target, amount);
                    debug(String.format(Locale.US, "Tax sink PLAYER: deposited %.2f to %s", amount, name));
                }
                break;
            }
            case POOL: {
                java.util.Collection<? extends Player> online = Bukkit.getOnlinePlayers();
                if (!online.isEmpty()) {
                    double share = roundCurrency(amount / online.size());
                    for (Player p : online) {
                        economy.depositPlayer(p, share);
                    }
                    debug(String.format(Locale.US, "Tax sink POOL: distributed %.2f across %d players (%.2f each)",
                            amount, online.size(), share));
                }
                break;
            }
            case COMMAND: {
                String cmd = config.getSinkCommand();
                if (cmd != null && !cmd.isEmpty()) {
                    String formatted = cmd.replace("%amount%", String.format(Locale.US, "%.2f", amount));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted);
                    debug(String.format(Locale.US, "Tax sink COMMAND: executed '%s'", formatted));
                }
                break;
            }
            default:
                break;
        }
    }

    private double roundCurrency(double amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private void debug(String message) {
        if (config.isDebug()) {
            logger.fine(message);
        }
    }
}
