package com.skyblockexp.eztax.feature;

import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PayCommandCaptureTest extends AbstractEzTaxTest {

    @Test
    public void capture_commands_apply_on_withdraw() {
        // enable command-capture and ensure withdraw path is used
        plugin.getConfig().set("transaction-tax.capture-commands", true);
        plugin.getConfig().set("transaction-tax.apply-on-withdraw", true);
        plugin.getConfig().set("transaction-tax.apply-on-deposit", false);
        plugin.saveConfig();
        plugin.reloadEzTax();

        PlayerMock sender = server.addPlayer("alice");
        PlayerMock target = server.addPlayer("bob");
        Economy econ = plugin.getTaxEngine().getEconomy();

        econ.depositPlayer(sender, 500.0);

        double amount = 100.0;
        double expectedTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(sender.getPlayer(), amount);

        // fire preprocess event (simulates player typing /pay bob 100)
        server.getPluginManager().callEvent(new PlayerCommandPreprocessEvent(sender.getPlayer(), "/pay bob 100"));

        // sender should be charged amount + tax, recipient receives full amount
        assertEquals(500.0 - amount - expectedTax, econ.getBalance(sender), 0.001);
        assertEquals(amount, econ.getBalance(target), 0.001);

        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        assertEquals(expectedTax, recorded, 0.001);
    }

    @Test
    public void capture_commands_apply_on_deposit() {
        // enable command-capture and set deposit path
        plugin.getConfig().set("transaction-tax.capture-commands", true);
        plugin.getConfig().set("transaction-tax.apply-on-withdraw", false);
        plugin.getConfig().set("transaction-tax.apply-on-deposit", true);
        plugin.saveConfig();
        plugin.reloadEzTax();

        PlayerMock sender = server.addPlayer("carol");
        PlayerMock target = server.addPlayer("dave");
        Economy econ = plugin.getTaxEngine().getEconomy();

        econ.depositPlayer(sender, 200.0);

        double amount = 100.0;
        double expectedTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(sender.getPlayer(), amount);

        server.getPluginManager().callEvent(new PlayerCommandPreprocessEvent(sender.getPlayer(), "/pay dave 100"));

        // sender pays the full amount; recipient receives amount - tax; tax is recorded
        assertEquals(200.0 - amount, econ.getBalance(sender), 0.001);
        assertEquals(amount - expectedTax, econ.getBalance(target), 0.001);

        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        assertEquals(expectedTax, recorded, 0.001);
    }

    @Test
    public void capture_commands_ignored_when_disabled() {
        plugin.getConfig().set("transaction-tax.capture-commands", false);
        plugin.saveConfig();
        plugin.reloadEzTax();

        PlayerMock sender = server.addPlayer("eve");
        PlayerMock target = server.addPlayer("frank");
        Economy econ = plugin.getTaxEngine().getEconomy();
        econ.depositPlayer(sender, 100.0);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(sender.getPlayer(), "/pay frank 10");
        server.getPluginManager().callEvent(event);

        assertFalse(event.isCancelled(), "Event should not be cancelled when capture-commands is disabled");
        assertEquals(100.0, econ.getBalance(sender), 0.001);
        assertEquals(0.0, econ.getBalance(target), 0.001);
    }

    @Test
    public void capture_commands_invalid_amount_cancels_and_no_balance_change() {
        plugin.getConfig().set("transaction-tax.capture-commands", true);
        plugin.saveConfig();
        plugin.reloadEzTax();

        PlayerMock sender = server.addPlayer("gina");
        PlayerMock target = server.addPlayer("harry");
        Economy econ = plugin.getTaxEngine().getEconomy();
        econ.depositPlayer(sender, 100.0);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(sender.getPlayer(), "/pay harry abc");
        server.getPluginManager().callEvent(event);

        assertTrue(event.isCancelled());
        assertEquals(100.0, econ.getBalance(sender), 0.001);
        assertEquals(0.0, econ.getBalance(target), 0.001);
    }

    @Test
    public void capture_commands_target_not_found_cancels() {
        // API returns an OfflinePlayer for arbitrary names; listener will treat that as a valid deposit target
        plugin.getConfig().set("transaction-tax.capture-commands", true);
        plugin.saveConfig();
        plugin.reloadEzTax();

        PlayerMock sender = server.addPlayer("ivy");
        Economy econ = plugin.getTaxEngine().getEconomy();
        econ.depositPlayer(sender, 100.0);

        double amount = 10.0;
        double expectedTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(sender.getPlayer(), amount);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(sender.getPlayer(), "/pay nobody 10");
        server.getPluginManager().callEvent(event);

        // event is cancelled by the listener (it handled the command) and funds are transferred to the offline account
        assertTrue(event.isCancelled());
        assertEquals(100.0 - amount - expectedTax, econ.getBalance(sender), 0.001);

        var offline = server.getOfflinePlayer("nobody");
        assertEquals(amount, econ.getBalance(offline), 0.001);

        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        assertEquals(expectedTax, recorded, 0.001);
    }

    @Test
    public void capture_commands_insufficient_funds_cancels_and_no_transfer() {
        plugin.getConfig().set("transaction-tax.capture-commands", true);
        plugin.saveConfig();
        plugin.reloadEzTax();

        PlayerMock sender = server.addPlayer("jack");
        PlayerMock target = server.addPlayer("kate");
        Economy econ = plugin.getTaxEngine().getEconomy();
        econ.depositPlayer(sender, 5.0);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(sender.getPlayer(), "/pay kate 10");
        server.getPluginManager().callEvent(event);

        assertTrue(event.isCancelled());
        assertEquals(5.0, econ.getBalance(sender), 0.001);
        assertEquals(0.0, econ.getBalance(target), 0.001);
    }

    @Test
    public void capture_commands_respect_custom_command_names() {
        plugin.getConfig().set("transaction-tax.capture-commands", true);
        plugin.getConfig().set("transaction-tax.commands", java.util.List.of("payme"));
        plugin.saveConfig();
        plugin.reloadEzTax();

        PlayerMock sender = server.addPlayer("lucy");
        PlayerMock target = server.addPlayer("mike");
        Economy econ = plugin.getTaxEngine().getEconomy();
        econ.depositPlayer(sender, 200.0);

        double amount = 50.0;
        double expectedTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(sender.getPlayer(), amount);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(sender.getPlayer(), "/payme mike 50");
        server.getPluginManager().callEvent(event);

        assertEquals(200.0 - amount - expectedTax, econ.getBalance(sender), 0.001);
        assertEquals(amount, econ.getBalance(target), 0.001);

        File statsFile = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(statsFile.exists());
        FileConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
        double recorded = stats.getDouble("totals.sinks.TRANSACTION", 0.0);
        assertEquals(expectedTax, recorded, 0.001);
    }
}
