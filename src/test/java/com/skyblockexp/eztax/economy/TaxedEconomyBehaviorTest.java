package com.skyblockexp.eztax.economy;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.TaxSink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TaxedEconomyBehaviorTest {
    private ServerMock server;
    private JavaPlugin plugin;

    public static class TestPlugin extends JavaPlugin {}

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(TestPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void withdraw_applies_transaction_tax_and_records_stats() {
        TaxConfig config = new TaxConfig(plugin);
        // configure transaction tax
        plugin.getConfig().set("transaction-tax.enabled", true);
        plugin.getConfig().set("transaction-tax.apply-on-withdraw", true);
        plugin.getConfig().set("transaction-tax.percentage", 10.0);
        plugin.saveConfig();
        config.reload();

        StatsService stats = new StatsService(plugin, config);
        VaultHook vh = new VaultHook(plugin);
        vh.hook();
        TaxEngineStub engine = new TaxEngineStub(config, stats, vh);

        // use internal economy as delegate
        InternalEconomy delegate = new InternalEconomy(plugin);
        TaxedEconomy taxed = new TaxedEconomy(delegate, engine, config, plugin.getLogger());

        PlayerMock p = server.addPlayer("payer");
        // deposit via delegate
        delegate.createPlayerAccount(p);
        delegate.depositPlayer(p, 100.0);

        // withdraw 50 via taxed wrapper; expect 10% tax -> tax = 5.0, total withdrawn from delegate = 55
        var resp = taxed.withdrawPlayer(p, 50.0);
        assertTrue(resp.transactionSuccess());
        double bal = delegate.getBalance(p);
        assertEquals(45.0, bal, 0.001);

        Map<TaxSink, Double> sinks = stats.getSinkTotals();
        assertTrue(sinks.get(TaxSink.TRANSACTION) >= 5.0);
    }

    @Test
    public void deposit_applies_transaction_tax_and_records_stats_20percent() {
        TaxConfig config = new TaxConfig(plugin);
        // configure transaction tax on deposit
        plugin.getConfig().set("transaction-tax.enabled", true);
        plugin.getConfig().set("transaction-tax.apply-on-deposit", true);
        plugin.getConfig().set("transaction-tax.percentage", 10.0);
        plugin.saveConfig();
        config.reload();

        StatsService stats = new StatsService(plugin, config);
        VaultHook vh = new VaultHook(plugin);
        vh.hook();
        TaxEngineStub engine = new TaxEngineStub(config, stats, vh);

        InternalEconomy delegate = new InternalEconomy(plugin);
        TaxedEconomy taxed = new TaxedEconomy(delegate, engine, config, plugin.getLogger());

        PlayerMock p = server.addPlayer("depositor");

        // deposit 50 via taxed wrapper; expect 10% tax -> tax = 5.0, net deposited = 45
        var resp = taxed.depositPlayer(p, 50.0);
        assertTrue(resp.transactionSuccess());
        double bal = delegate.getBalance(p);
        assertEquals(45.0, bal, 0.001);

        Map<TaxSink, Double> sinks = stats.getSinkTotals();
        assertTrue(sinks.get(TaxSink.TRANSACTION) >= 5.0);
    }



    @Test
    public void deposit_applies_transaction_tax_and_records_stats() {
        TaxConfig config = new TaxConfig(plugin);
        // configure transaction tax on deposit
        plugin.getConfig().set("transaction-tax.enabled", true);
        plugin.getConfig().set("transaction-tax.apply-on-deposit", true);
        plugin.getConfig().set("transaction-tax.percentage", 20.0);
        plugin.saveConfig();
        config.reload();

        StatsService stats = new StatsService(plugin, config);
        VaultHook vh = new VaultHook(plugin);
        vh.hook();
        TaxEngineStub engine = new TaxEngineStub(config, stats, vh);

        InternalEconomy delegate = new InternalEconomy(plugin);
        TaxedEconomy taxed = new TaxedEconomy(delegate, engine, config, plugin.getLogger());

        PlayerMock p = server.addPlayer("depositor");
        delegate.createPlayerAccount(p);
        // deposit 50 via taxed wrapper; expect tax 20% -> tax = 10, net deposited = 40
        var resp = taxed.depositPlayer(p, 50.0);
        assertTrue(resp.transactionSuccess());
        double bal = delegate.getBalance(p);
        assertEquals(40.0, bal, 0.001);

        Map<TaxSink, Double> sinks = stats.getSinkTotals();
        assertTrue(sinks.get(TaxSink.TRANSACTION) >= 10.0);
    }

    @Test
    public void deposit_too_small_for_tax_fails() {
        TaxConfig config = new TaxConfig(plugin);
        plugin.getConfig().set("transaction-tax.enabled", true);
        plugin.getConfig().set("transaction-tax.apply-on-deposit", true);
        plugin.getConfig().set("transaction-tax.percentage", 100.0);
        plugin.saveConfig();
        config.reload();

        StatsService stats = new StatsService(plugin, config);
        VaultHook vh = new VaultHook(plugin);
        vh.hook();
        TaxEngineStub engine = new TaxEngineStub(config, stats, vh);

        InternalEconomy delegate = new InternalEconomy(plugin);
        TaxedEconomy taxed = new TaxedEconomy(delegate, engine, config, plugin.getLogger());

        PlayerMock p = server.addPlayer("tiny");
        delegate.createPlayerAccount(p);
        // deposit 1 with 90% tax -> net 0.1 <= 0 after rounding? Should fail deposit
        var resp = taxed.depositPlayer(p, 1.0);
        assertFalse(resp.transactionSuccess());
    }

    static class TaxEngineStub extends com.skyblockexp.eztax.service.TaxEngine {
        public TaxEngineStub(TaxConfig config, StatsService stats, VaultHook vh) {
            super(config, stats, vh, java.util.logging.Logger.getAnonymousLogger());
        }
    }
}
