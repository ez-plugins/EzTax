package com.skyblockexp.eztax.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;

import static org.junit.jupiter.api.Assertions.*;

public class TaxConfigStorageTest {
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
    public void reads_storage_settings_from_config() {
        plugin.getConfig().set("storage.type", "mysql");
        plugin.getConfig().set("storage.file", "other-stats.yml");
        plugin.getConfig().set("storage.mysql.host", "db.example");
        plugin.getConfig().set("storage.mysql.port", 3307);
        plugin.getConfig().set("storage.mysql.database", "prod_db");
        plugin.getConfig().set("storage.mysql.user", "dbuser");
        plugin.getConfig().set("storage.mysql.password", "secret");
        plugin.getConfig().set("storage.mysql.table", "table_stats");

        TaxConfig cfg = new TaxConfig(plugin);
        assertEquals("mysql", cfg.getStorageType());
        assertEquals("other-stats.yml", cfg.getStorageFile());
        assertEquals("db.example", cfg.getMysqlHost());
        assertEquals(3307, cfg.getMysqlPort());
        assertEquals("prod_db", cfg.getMysqlDatabase());
        assertEquals("dbuser", cfg.getMysqlUser());
        assertEquals("secret", cfg.getMysqlPassword());
        assertEquals("table_stats", cfg.getMysqlTable());
    }
}
