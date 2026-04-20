package com.skyblockexp.eztax.integration;

import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.skyblockexp.eztax.EzTaxPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TaxIntegrationTest extends AbstractEzTaxTest {

    @Test
    public void transactionTax_isApplied_and_statsRecorded() {
        PlayerMock player = server.addPlayer("taxplayer");
        Economy econ = plugin.getTaxEngine().getEconomy();

        // Ensure account exists and deposit
        econ.depositPlayer(player, 1000.0);

        // Perform a withdraw which should incur transaction tax (config default: apply-on-withdraw true)
        double withdrawAmount = 100.0;
        double expectedTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(player.getPlayer(), withdrawAmount);

        var response = econ.withdrawPlayer(player, withdrawAmount);
        assertTrue(response.transactionSuccess());

        // Load stats.yml and check transaction sink recorded amount equals expected tax
        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists(), "stats.yml should exist after transactions");
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("eztax_stats.stats.sink_transaction", 0.0);
        assertEquals(expectedTax, recorded, 0.001, "Transaction tax recorded should equal calculated tax");

        // Verify player's balance reduced by withdraw + exact tax
        double balance = econ.getBalance(player);
        double expectedBalance = 1000.0 - withdrawAmount - expectedTax;
        assertEquals(expectedBalance, balance, 0.001, "Balance should be reduced by withdraw amount plus transaction tax");
    }

    @Test
    public void wealthTax_appliesUsingConfigBrackets_and_statsRecorded() {
        PlayerMock player = server.addPlayer("wealthplayer");
        // Lower the wealth tax threshold to make testable
        plugin.getConfig().set("wealth-tax.threshold", 100.0);
        plugin.getConfig().set("wealth-tax.percentage", 10.0);
        // add simple bracket: up to 200 taxed at 5%
        plugin.getConfig().set("wealth-tax.brackets.low.limit", 200.0);
        plugin.getConfig().set("wealth-tax.brackets.low.percent", 5.0);
        plugin.saveConfig();
        // reload plugin configuration into services
        plugin.reloadEzTax();

        Economy econ = plugin.getTaxEngine().getEconomy();

        // Deposit amount above threshold
        econ.depositPlayer(player, 300.0);

        // Apply wealth tax via TaxEngine
        plugin.getTaxEngine().applyWealthTax(player);

        // Check stats file for WEALTH sink
        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("eztax_stats.stats.sink_wealth", 0.0);
        assertTrue(recorded > 0.0, "Wealth tax should be recorded in stats");

        // Balance should be reduced
        double balance = econ.getBalance(player);
        assertTrue(balance < 300.0, "Balance should be reduced by wealth tax");
    }
}
