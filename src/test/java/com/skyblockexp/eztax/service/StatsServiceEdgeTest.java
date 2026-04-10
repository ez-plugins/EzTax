package com.skyblockexp.eztax.service;

import com.skyblockexp.eztax.config.TaxConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;

import static org.junit.jupiter.api.Assertions.*;

public class StatsServiceEdgeTest {
    private ServerMock server;
    private JavaPlugin plugin;
    private TaxConfig config;
    private StatsService stats;

    public static class TestPlugin extends JavaPlugin {}

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(TestPlugin.class);
        config = new TaxConfig(plugin);
        stats = new StatsService(plugin, config);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void recording_zero_or_negative_amount_is_ignored() {
        double before = stats.getTotalRemoved();
        stats.recordTax(TaxSink.TRANSACTION, 0.0);
        stats.recordTax(TaxSink.TRANSACTION, -5.0);
        assertEquals(before, stats.getTotalRemoved(), 0.0001);
    }

    @Test
    public void multiple_records_update_sinks_and_totals() {
        double before = stats.getTotalRemoved();
        stats.recordTax(TaxSink.WEALTH, 25.0, false);
        stats.recordTax(TaxSink.WEALTH, 75.0, true);
        assertEquals(before + 100.0, stats.getTotalRemoved(), 0.001);
        assertEquals(100.0, stats.getSinkTotals().get(TaxSink.WEALTH), 0.001);
        if (config.isTreasuryEnabled()) {
            assertTrue(stats.getTreasuryBalance() >= 75.0);
        }
    }
}
