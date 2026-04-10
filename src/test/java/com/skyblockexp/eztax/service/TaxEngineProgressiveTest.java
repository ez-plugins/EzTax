package com.skyblockexp.eztax.service;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.economy.VaultHook;
import net.milkbowl.vault.economy.Economy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaxEngineProgressiveTest {

    private static final double EPS = 0.001;

    private static Economy dummyEconomy() {
        return new Economy() {
            @Override public boolean isEnabled() { return true; }
            @Override public String getName() { return "dummy"; }
            @Override public boolean hasBankSupport() { return false; }
            @Override public int fractionalDigits() { return 2; }
            @Override public String format(double amount) { return String.format("%.2f", amount); }
            @Override public String currencyNamePlural() { return "Coins"; }
            @Override public String currencyNameSingular() { return "Coin"; }
            @Override public boolean hasAccount(String playerName) { return false; }
            @Override public boolean hasAccount(org.bukkit.OfflinePlayer player) { return false; }
            @Override public boolean hasAccount(String playerName, String worldName) { return false; }
            @Override public boolean hasAccount(org.bukkit.OfflinePlayer player, String worldName) { return false; }
            @Override public double getBalance(String playerName) { return 0; }
            @Override public double getBalance(org.bukkit.OfflinePlayer player) { return 0; }
            @Override public double getBalance(String playerName, String world) { return 0; }
            @Override public double getBalance(org.bukkit.OfflinePlayer player, String world) { return 0; }
            @Override public boolean has(String playerName, double amount) { return false; }
            @Override public boolean has(org.bukkit.OfflinePlayer player, double amount) { return false; }
            @Override public boolean has(String playerName, String worldName, double amount) { return false; }
            @Override public boolean has(org.bukkit.OfflinePlayer player, String worldName, double amount) { return false; }
            @Override public net.milkbowl.vault.economy.EconomyResponse withdrawPlayer(String playerName, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse withdrawPlayer(org.bukkit.OfflinePlayer player, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse withdrawPlayer(org.bukkit.OfflinePlayer player, String worldName, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse depositPlayer(String playerName, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse depositPlayer(org.bukkit.OfflinePlayer player, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse depositPlayer(String playerName, String worldName, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse depositPlayer(org.bukkit.OfflinePlayer player, String worldName, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse createBank(String name, String player) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse createBank(String name, org.bukkit.OfflinePlayer player) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse deleteBank(String name) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse bankBalance(String name) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse bankHas(String name, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse bankWithdraw(String name, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse bankDeposit(String name, double amount) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse isBankOwner(String name, String playerName) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse isBankOwner(String name, org.bukkit.OfflinePlayer player) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse isBankMember(String name, String playerName) { return null; }
            @Override public net.milkbowl.vault.economy.EconomyResponse isBankMember(String name, org.bukkit.OfflinePlayer player) { return null; }
            @Override public java.util.List<String> getBanks() { return java.util.Collections.emptyList(); }
            @Override public boolean createPlayerAccount(String playerName) { return false; }
            @Override public boolean createPlayerAccount(org.bukkit.OfflinePlayer player) { return false; }
            @Override public boolean createPlayerAccount(String playerName, String worldName) { return false; }
            @Override public boolean createPlayerAccount(org.bukkit.OfflinePlayer player, String worldName) { return false; }
        };
    }

    @Test
    public void testNoBracketsUsesFlatPercent() {
        VaultHook vh = new VaultHook(null) {
            @Override public Economy getEconomy() { return dummyEconomy(); }
        };
        TaxEngine engine = new TaxEngine(null, null, vh, Logger.getLogger("test"));

        double taxable = 10000.0;
        double percent = 2.5; // 2.5%
        double expected = Math.round(taxable * (percent/100.0) * 100.0)/100.0;
        double calc = engine.calculateProgressiveTax(taxable, new ArrayList<>(), percent, "wealth");
        assertEquals(expected, calc, EPS);
    }

    @Test
    public void testProgressiveBrackets() {
        VaultHook vh = new VaultHook(null) {
            @Override public Economy getEconomy() { return dummyEconomy(); }
        };
        TaxEngine engine = new TaxEngine(null, null, vh, Logger.getLogger("test"));

        List<Object> brackets = new ArrayList<>();
        brackets.add(new TaxConfig.WealthBracket(5000, 1.0));
        brackets.add(new TaxConfig.WealthBracket(10000, 2.0));
        double taxable = 12000.0; // 5000 @1% = 50, 5000 @2% = 100, 2000 @3%(default) = 60 => total 210
        double expected = 210.00;
        double calc = engine.calculateProgressiveTax(taxable, brackets, 3.0, "wealth");
        assertEquals(expected, calc, EPS);
    }

    @Test
    public void testPartialBracket() {
        VaultHook vh = new VaultHook(null) {
            @Override public Economy getEconomy() { return dummyEconomy(); }
        };
        TaxEngine engine = new TaxEngine(null, null, vh, Logger.getLogger("test"));

        List<Object> brackets = new ArrayList<>();
        brackets.add(new TaxConfig.WealthBracket(3000, 5.0));
        double taxable = 2000.0; // entirely in first bracket: 2000 @5% = 100
        double expected = 100.00;
        double calc = engine.calculateProgressiveTax(taxable, brackets, 10.0, "wealth");
        assertEquals(expected, calc, EPS);
    }
}
