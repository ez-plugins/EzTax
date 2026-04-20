package com.skyblockexp.eztax.economy;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.StatsService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
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
        // Register InternalEconomy as a Vault economy service
        server.getServicesManager().register(Economy.class, new InternalEconomy(plugin), plugin, ServicePriority.Normal);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void registerTaxedEconomy_wraps_internal_economy_and_registers_service() {
        VaultHook hook = new VaultHook(plugin);
        assertTrue(hook.hook());
        TaxConfig config = new TaxConfig(plugin);
        StatsService stats = new StatsService(plugin, config);
        java.util.logging.Logger logger = Logger.getAnonymousLogger();
        TaxEngine engine = new TaxEngine(config, stats, hook, logger);
        hook.registerTaxedEconomy(engine, config);
        assertNotNull(hook.getEconomy());
        assertTrue(hook.getEconomy() instanceof TaxedEconomy, "Economy should be wrapped as TaxedEconomy");
    }
}
