package com.skyblockexp.eztax.command;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.command.execute.SetTaxRateExecute;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.Messages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

public class SetTaxRateExecuteTest {
    private ServerMock server;
    private EzTaxPlugin plugin;
    private TaxConfig config;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(EzTaxPlugin.class);
        config = new TaxConfig(plugin);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void set_global_fallback_rate_updates_config_and_reload() {
        PlayerMock sender = server.addPlayer("admin");
        SetTaxRateExecute exec = new SetTaxRateExecute(plugin, config, plugin.getMessages());
        boolean res = exec.execute(sender, new String[]{"settaxrate", "2.5"});
        assertTrue(res);
        assertEquals(2.5, plugin.getConfig().getDouble("group-taxes.fallback-rate"), 0.001);
        config.reload();
        assertEquals(2.5, config.getGroupTaxesFallbackRate(), 0.001);
    }

    @Test
    public void set_group_rate_writes_group_config() {
        PlayerMock sender = server.addPlayer("admin2");
        SetTaxRateExecute exec = new SetTaxRateExecute(plugin, config, plugin.getMessages());
        boolean res = exec.execute(sender, new String[]{"settaxrate", "1.0", "vip"});
        assertTrue(res);
        assertEquals(1.0, plugin.getConfig().getDouble("group-taxes.groups.vip.transaction-tax"), 0.001);
        config.reload();
        assertEquals(1.0, config.getTransactionTaxPercentForGroup("vip"), 0.001);
    }
}
