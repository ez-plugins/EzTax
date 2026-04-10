package com.skyblockexp.eztax.feature;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.skyblockexp.eztax.EzTaxPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


import com.skyblockexp.eztax.test.AbstractEzTaxTest;

public class TaxFeatureTest extends AbstractEzTaxTest {
    

    @Test
    public void transactionTax_isApplied_and_statsRecorded_feature() {
        PlayerMock player = server.addPlayer("taxplayer_feature");
        Economy econ = plugin.getTaxEngine().getEconomy();

        econ.depositPlayer(player, 1000.0);

        double withdrawAmount = 100.0;
        // calculate expected tax using TaxEngine (honours group/exemption rules and rounding)
        double expectedTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(player.getPlayer(), withdrawAmount);

        var response = econ.withdrawPlayer(player, withdrawAmount);
        assertTrue(response.transactionSuccess());

        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists(), "stats.yml should exist after transactions");
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        assertEquals(expectedTax, recorded, 0.001, "Recorded transaction tax should match calculated tax");

        double balance = econ.getBalance(player);
        double expectedBalance = 1000.0 - withdrawAmount - expectedTax;
        assertEquals(expectedBalance, balance, 0.001, "Balance should be reduced by withdraw amount plus recorded tax");
    }

    @Test
    public void transactionTax_minimumFee_applies_feature() {
        PlayerMock player = server.addPlayer("minfeepayer");
        Economy econ = plugin.getTaxEngine().getEconomy();

        // deposit a small amount where percentage-based fee would be below the configured minimum
        econ.depositPlayer(player, 50.0);
        double withdrawAmount = 10.0;

        double expectedTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(player.getPlayer(), withdrawAmount);
        // default config has minimum-fee = 1.0 so small withdrawals should hit the minimum
        assertEquals(1.0, expectedTax, 0.001, "Expected minimum transaction fee to apply for small withdrawals");

        var response = econ.withdrawPlayer(player, withdrawAmount);
        assertTrue(response.transactionSuccess());

        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        assertEquals(expectedTax, recorded, 0.001, "Recorded transaction tax should equal the minimum fee");

        double expectedBalance = 50.0 - withdrawAmount - expectedTax;
        assertEquals(expectedBalance, econ.getBalance(player), 0.001, "Balance should be reduced by withdraw + minimum tax");
    }

    @Test
    public void wealthTax_appliesUsingConfigBrackets_and_statsRecorded_feature() {
        PlayerMock player = server.addPlayer("wealthplayer_feature");

        plugin.getConfig().set("wealth-tax.threshold", 100.0);
        plugin.getConfig().set("wealth-tax.percentage", 10.0);
        plugin.getConfig().set("wealth-tax.brackets.low.limit", 200.0);
        plugin.getConfig().set("wealth-tax.brackets.low.percent", 5.0);
        plugin.saveConfig();
        plugin.reloadEzTax();

        Economy econ = plugin.getTaxEngine().getEconomy();
        econ.depositPlayer(player, 300.0);

        plugin.getTaxEngine().applyWealthTax(player);

        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("totals.sinks.WEALTH", 0.0);
        assertTrue(recorded > 0.0, "Wealth tax should be recorded in stats");

        double balance = econ.getBalance(player);
        assertTrue(balance < 300.0, "Balance should be reduced by wealth tax");
    }
}
