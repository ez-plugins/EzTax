package com.skyblockexp.eztax.economy;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.TaxEngine;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.UUID;

public class TaxedEconomy implements Economy {
    private final Economy delegate;
    private final TaxConfig config;
    private final Logger logger;

    // taxEngine may be null initially (defensive). Use setter to update when available.
    private TaxEngine mutableTaxEngine;

    public TaxedEconomy(Economy delegate, TaxEngine taxEngine, TaxConfig config, Logger logger) {
        this.delegate = delegate;
        this.mutableTaxEngine = taxEngine;
        this.config = config;
        this.logger = logger;
    }

    /**
     * Update the TaxEngine instance used by this wrapper. Safe to call when wrapping an existing economy provider.
     */
    public void setTaxEngine(TaxEngine taxEngine) {
        this.mutableTaxEngine = taxEngine;
    }

    @Override
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return delegate.hasBankSupport();
    }

    @Override
    public int fractionalDigits() {
        return delegate.fractionalDigits();
    }

    @Override
    public String format(double amount) {
        return delegate.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return delegate.currencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return delegate.currencyNameSingular();
    }

    @Override
    public boolean hasAccount(String playerName) {
        return delegate.hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return delegate.hasAccount(player);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return delegate.hasAccount(playerName, worldName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return delegate.hasAccount(player, worldName);
    }

    @Override
    public double getBalance(String playerName) {
        return delegate.getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return delegate.getBalance(player);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return delegate.getBalance(playerName, world);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return delegate.getBalance(player, world);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return delegate.has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return delegate.has(player, amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return delegate.has(playerName, worldName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return delegate.has(player, worldName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawWithTax(playerName, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawWithTax(player, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawWithTax(playerName, worldName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawWithTax(player, worldName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositWithTax(playerName, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositWithTax(player, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositWithTax(playerName, worldName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositWithTax(player, worldName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return delegate.createBank(name, player);
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return delegate.createBank(name, player);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return delegate.deleteBank(name);
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return delegate.bankBalance(name);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return delegate.bankHas(name, amount);
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return delegate.bankWithdraw(name, amount);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return delegate.bankDeposit(name, amount);
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return delegate.isBankOwner(name, playerName);
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return delegate.isBankOwner(name, player);
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return delegate.isBankMember(name, playerName);
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return delegate.isBankMember(name, player);
    }

    @Override
    public List<String> getBanks() {
        return delegate.getBanks();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return delegate.createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return delegate.createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return delegate.createPlayerAccount(playerName, worldName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return delegate.createPlayerAccount(player, worldName);
    }

    private EconomyResponse withdrawWithTax(String playerName, String worldName, double amount) {
        if (amount <= 0) {
            return worldName == null
                ? delegate.withdrawPlayer(playerName, amount)
                : delegate.withdrawPlayer(playerName, worldName, amount);
        }
        if (config == null || !config.isTransactionTaxEnabled() || !config.isTransactionTaxApplyOnWithdraw() || mutableTaxEngine == null) {
            return worldName == null
                ? delegate.withdrawPlayer(playerName, amount)
                : delegate.withdrawPlayer(playerName, worldName, amount);
        }
        double tax = mutableTaxEngine.calculateTransactionTax(amount);
        if (tax <= 0) {
            return worldName == null
                ? delegate.withdrawPlayer(playerName, amount)
                : delegate.withdrawPlayer(playerName, worldName, amount);
        }
        double total = amount + tax;
        double balance = worldName == null ? delegate.getBalance(playerName) : delegate.getBalance(playerName, worldName);
        if (balance < total) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds for tax.");
        }
        EconomyResponse response = worldName == null
            ? delegate.withdrawPlayer(playerName, total)
            : delegate.withdrawPlayer(playerName, worldName, total);
        if (response.transactionSuccess()) {
            if (mutableTaxEngine != null) mutableTaxEngine.recordTransactionTax(playerName, tax);
            debug(playerName, amount, tax);
            return new EconomyResponse(amount, response.balance, ResponseType.SUCCESS, response.errorMessage);
        }
        return response;
    }

    private EconomyResponse withdrawWithTax(OfflinePlayer player, String worldName, double amount) {
        if (amount <= 0) {
            return worldName == null
                ? delegate.withdrawPlayer(player, amount)
                : delegate.withdrawPlayer(player, worldName, amount);
        }
        if (config == null || !config.isTransactionTaxEnabled() || !config.isTransactionTaxApplyOnWithdraw() || mutableTaxEngine == null) {
            return worldName == null
                ? delegate.withdrawPlayer(player, amount)
                : delegate.withdrawPlayer(player, worldName, amount);
        }
        
        // Use group-based tax if player is online
        if (mutableTaxEngine == null) {
            return worldName == null
                ? delegate.withdrawPlayer(player, amount)
                : delegate.withdrawPlayer(player, worldName, amount);
        }
        double tax;
        if (player.isOnline() && player.getPlayer() != null) {
            tax = mutableTaxEngine.calculateTransactionTaxForPlayer(player.getPlayer(), amount);
        } else {
            tax = mutableTaxEngine.calculateTransactionTax(amount);
        }
        
        if (tax <= 0) {
            return worldName == null
                ? delegate.withdrawPlayer(player, amount)
                : delegate.withdrawPlayer(player, worldName, amount);
        }
        double total = amount + tax;
        double balance = worldName == null ? delegate.getBalance(player) : delegate.getBalance(player, worldName);
        if (balance < total) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds for tax.");
        }
        EconomyResponse response = worldName == null
            ? delegate.withdrawPlayer(player, total)
            : delegate.withdrawPlayer(player, worldName, total);
        if (response.transactionSuccess()) {
            UUID uuid = player.getUniqueId();
            if (mutableTaxEngine != null) mutableTaxEngine.recordTransactionTax(uuid, tax);
            debug(player.getName(), amount, tax);
            return new EconomyResponse(amount, response.balance, ResponseType.SUCCESS, response.errorMessage);
        }
        return response;
    }

    private EconomyResponse depositWithTax(String playerName, String worldName, double amount) {
        if (amount <= 0) {
            return worldName == null
                ? delegate.depositPlayer(playerName, amount)
                : delegate.depositPlayer(playerName, worldName, amount);
        }
        if (config == null || !config.isTransactionTaxEnabled() || !config.isTransactionTaxApplyOnDeposit() || mutableTaxEngine == null) {
            return worldName == null
                ? delegate.depositPlayer(playerName, amount)
                : delegate.depositPlayer(playerName, worldName, amount);
        }
        double tax = mutableTaxEngine.calculateTransactionTax(amount);
        if (tax <= 0) {
            return worldName == null
                ? delegate.depositPlayer(playerName, amount)
                : delegate.depositPlayer(playerName, worldName, amount);
        }
        double netAmount = amount - tax;
        if (netAmount <= 0) {
            double balance = worldName == null ? delegate.getBalance(playerName) : delegate.getBalance(playerName, worldName);
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Deposit amount too small for tax.");
        }
        EconomyResponse response = worldName == null
            ? delegate.depositPlayer(playerName, netAmount)
            : delegate.depositPlayer(playerName, worldName, netAmount);
        if (response.transactionSuccess()) {
            if (mutableTaxEngine != null) mutableTaxEngine.recordTransactionTax(playerName, tax);
            debug(playerName, amount, tax);
            return new EconomyResponse(amount, response.balance, ResponseType.SUCCESS, response.errorMessage);
        }
        return response;
    }

    private EconomyResponse depositWithTax(OfflinePlayer player, String worldName, double amount) {
        if (amount <= 0) {
            return worldName == null
                ? delegate.depositPlayer(player, amount)
                : delegate.depositPlayer(player, worldName, amount);
        }
        if (config == null || !config.isTransactionTaxEnabled() || !config.isTransactionTaxApplyOnDeposit() || mutableTaxEngine == null) {
            return worldName == null
                ? delegate.depositPlayer(player, amount)
                : delegate.depositPlayer(player, worldName, amount);
        }
        
        // Use group-based tax if player is online
        if (mutableTaxEngine == null) {
            return worldName == null
                ? delegate.depositPlayer(player, amount)
                : delegate.depositPlayer(player, worldName, amount);
        }
        double tax;
        if (player.isOnline() && player.getPlayer() != null) {
            tax = mutableTaxEngine.calculateTransactionTaxForPlayer(player.getPlayer(), amount);
        } else {
            tax = mutableTaxEngine.calculateTransactionTax(amount);
        }
        
        if (tax <= 0) {
            return worldName == null
                ? delegate.depositPlayer(player, amount)
                : delegate.depositPlayer(player, worldName, amount);
        }
        double netAmount = amount - tax;
        if (netAmount <= 0) {
            double balance = worldName == null ? delegate.getBalance(player) : delegate.getBalance(player, worldName);
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Deposit amount too small for tax.");
        }
        EconomyResponse response = worldName == null
            ? delegate.depositPlayer(player, netAmount)
            : delegate.depositPlayer(player, worldName, netAmount);
        if (response.transactionSuccess()) {
            if (mutableTaxEngine != null) mutableTaxEngine.recordTransactionTax(player.getUniqueId(), tax);
            debug(player.getName(), amount, tax);
            return new EconomyResponse(amount, response.balance, ResponseType.SUCCESS, response.errorMessage);
        }
        return response;
    }

    private void debug(String playerName, double amount, double tax) {
        if (config.isDebug()) {
            logger.fine(String.format(Locale.US, "Transaction tax applied to %s: amount=%.2f tax=%.2f", playerName, amount, tax));
        }
    }
}
