package com.skyblockexp.eztax.integration;

import com.skyblockexp.eztax.EzTaxPlugin;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that EzTax degrades gracefully when Vault is absent (standby mode).
 * Uses a raw MockBukkit environment — intentionally no TestEconomyPlugin loaded.
 */
public class StartupStandbyTest {

    private ServerMock server;
    private EzTaxPlugin plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        // Load EzTaxPlugin WITHOUT registering any Vault economy
        plugin = MockBukkit.load(EzTaxPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    // ── plugin must survive startup ────────────────────────────────────────

    @Test
    public void plugin_is_still_enabled_in_standby_mode() {
        assertTrue(plugin.isEnabled(),
                "Plugin must enable (standby mode) even when Vault is absent");
    }

    // ── graceful degradation of economy-dependent features ────────────────

    @Test
    public void tax_engine_is_null_without_vault() {
        assertNull(plugin.getTaxEngine(),
                "TaxEngine must be null when no Vault economy is available");
    }

    // ── non-economy services still initialize ─────────────────────────────

    @Test
    public void messages_service_available_in_standby() {
        assertNotNull(plugin.getMessages());
    }

    @Test
    public void exemption_service_available_in_standby() {
        assertNotNull(plugin.getExemptionService());
    }

    @Test
    public void gui_config_available_in_standby() {
        assertNotNull(plugin.getGuiConfig());
    }

    // ── commands still registered in standby ──────────────────────────────

    @Test
    public void eztax_command_registered_in_standby() {
        PluginCommand cmd = plugin.getCommand("eztax");
        assertNotNull(cmd, "/eztax must remain registered in standby mode");
        assertNotNull(cmd.getExecutor(), "Executor must be set even in standby mode");
    }

    @Test
    public void tax_command_registered_in_standby() {
        PluginCommand cmd = plugin.getCommand("tax");
        assertNotNull(cmd, "/tax must remain registered in standby mode");
        assertNotNull(cmd.getExecutor());
    }
}
