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

public class TaxEdgeFeatureTest extends com.skyblockexp.eztax.test.AbstractEzTaxTest {

    @Test
    public void exemption_prevents_transaction_and_wealth_tax() {
        PlayerMock player = server.addPlayer("exemptplayer");
        Economy econ = plugin.getTaxEngine().getEconomy();

        // make player exempt via plugin service
        plugin.getExemptionService().addExemption(player.getUniqueId());

        econ.depositPlayer(player, 1000.0);
        double withdrawAmount = 100.0;
        var response = econ.withdrawPlayer(player, withdrawAmount);
        assertTrue(response.transactionSuccess());

        // stats should show zero transaction/wealth
        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double tx = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        double wealth = stats.getDouble("totals.sinks.WEALTH", 0.0);
        assertEquals(0.0, tx, 0.001, "Transaction tax should not be recorded for exempt player");
        assertEquals(0.0, wealth, 0.001, "Wealth tax should not be recorded for exempt player");
    }

    @Test
    public void insufficient_funds_does_not_record_tax() {
        PlayerMock player = server.addPlayer("poorplayer");
        Economy econ = plugin.getTaxEngine().getEconomy();

        // deposit less than withdraw+tax
        econ.depositPlayer(player, 5.0);
        double withdrawAmount = 10.0;
        var response = econ.withdrawPlayer(player, withdrawAmount);
        assertFalse(response.transactionSuccess());

        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        // stats file may or may not exist; if exists, ensure no transaction recorded
        if (statsFile.exists()) {
            FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
            double tx = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
            assertEquals(0.0, tx, 0.001, "No transaction tax should be recorded when withdraw fails");
        }
    }

    @Test
    public void deposit_tax_toggle_applies_or_skips() {
        PlayerMock player = server.addPlayer("deposittest");
        // Ensure deposit-tax is disabled initially
        plugin.getConfig().set("transaction-tax.apply-on-deposit", false);
        plugin.saveConfig();
        plugin.reloadEzTax();
        Economy econ = plugin.getTaxEngine().getEconomy();

        econ.depositPlayer(player, 100.0);
        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double tx = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        // with deposit-tax disabled, recorded should be zero (or unchanged)
        assertEquals(0.0, tx, 0.001);

        // remember balance after first (untaxed) deposit
        double balanceAfterFirst = econ.getBalance(player);
        assertEquals(100.0, balanceAfterFirst, 0.001);

        // enable deposit-tax and repeat
        plugin.getConfig().set("transaction-tax.apply-on-deposit", true);
        plugin.saveConfig();
        plugin.reloadEzTax();
        econ = plugin.getTaxEngine().getEconomy();

        double depositAmount = 100.0;
        double expectedTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(player.getPlayer(), depositAmount);

        econ.depositPlayer(player, depositAmount);
        stats = YamlConfiguration.loadConfiguration(statsFile);
        double tx2 = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        // only the second deposit should have produced a tax entry
        assertEquals(expectedTax, tx2, 0.001, "Deposit tax should equal calculated tax when enabled");

        double expectedFinalBalance = balanceAfterFirst + (depositAmount - expectedTax);
        assertEquals(expectedFinalBalance, econ.getBalance(player), 0.001, "Player balance should reflect net deposit after tax");
    }
}
