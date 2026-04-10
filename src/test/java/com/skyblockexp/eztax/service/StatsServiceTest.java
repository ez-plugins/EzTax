package com.skyblockexp.eztax.service;

import com.skyblockexp.eztax.config.TaxConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class StatsServiceTest {
    private ServerMock server;
    private JavaPlugin plugin;
    private StatsService stats;
    private TaxConfig config;

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
    public void recordTax_updates_totals_and_sinks_and_treasury() {
        double beforeTotal = stats.getTotalRemoved();
        stats.recordTax(TaxSink.TRANSACTION, 50.0, true);
        assertEquals(beforeTotal + 50.0, stats.getTotalRemoved(), 0.001);
        assertTrue(stats.getSinkTotals().get(TaxSink.TRANSACTION) >= 50.0);
        if (config.isTreasuryEnabled()) {
            assertTrue(stats.getTreasuryBalance() >= 50.0);
        }
        File f = new File(plugin.getDataFolder(), "stats.yml");
        assertTrue(f.exists());
    }
}
