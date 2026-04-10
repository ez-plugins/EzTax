package com.skyblockexp.eztax.economy;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.StatsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;
import com.skyblockexp.eztax.service.TaxEngine;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class VaultHookTest {
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
    public void registerTaxedEconomy_wraps_internal_economy_and_registers_service() {
        VaultHook hook = new VaultHook(plugin);
        // initial hook will set internal economy
        assertTrue(hook.hook());
        TaxConfig config = new TaxConfig(plugin);
        StatsService stats = new StatsService(plugin, config);
        VaultHook vh = hook; // reuse
        java.util.logging.Logger logger = Logger.getAnonymousLogger();
        TaxEngine engine = new TaxEngine(config, stats, vh, logger);
        vh.registerTaxedEconomy(engine, config);
        assertNotNull(vh.getEconomy());
        assertTrue(vh.getEconomy() instanceof TaxedEconomy, "Economy should be wrapped as TaxedEconomy");
    }
}
