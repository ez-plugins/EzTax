package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.config.TaxConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import com.skyblockexp.eztax.EzTaxPlugin;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class StatsComponentStorageTest {
    private ServerMock server;
    private EzTaxPlugin plugin;

    public static class TestPlugin extends com.skyblockexp.eztax.EzTaxPlugin {
        @Override
        public void onEnable() {
            // prevent full bootstrap during this unit test
        }
    }

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
    public void mysql_missing_config_falls_back_to_yml() {
        // configure storage type but leave mysql host empty -> should fallback to YML
        plugin.getConfig().set("storage.type", "mysql");
        plugin.getConfig().set("storage.mysql.host", "");
        TaxConfig cfg = new TaxConfig(plugin);

        StatsComponent comp = new StatsComponent(plugin, cfg);
        comp.start();

        File f = new File(plugin.getDataFolder(), cfg.getStorageFile());
        assertTrue(f.exists(), "stats file should be created when falling back to YML");
    }
}
