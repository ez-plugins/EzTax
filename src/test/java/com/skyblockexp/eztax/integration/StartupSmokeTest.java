package com.skyblockexp.eztax.integration;

import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests for plugin startup with a Vault economy present.
 * Every test here asserts a single observable post-condition of a successful
 * plugin bootstrap — if any test fails, the corresponding service or command
 * was not initialized correctly.
 */
public class StartupSmokeTest extends AbstractEzTaxTest {

    // ── plugin lifecycle ───────────────────────────────────────────────────

    @Test
    public void plugin_is_enabled() {
        assertTrue(plugin.isEnabled());
    }

    @Test
    public void data_folder_exists_after_startup() {
        assertTrue(plugin.getDataFolder().exists());
    }

    // ── service initialization ─────────────────────────────────────────────

    @Test
    public void messages_service_not_null() {
        assertNotNull(plugin.getMessages());
    }

    @Test
    public void exemption_service_not_null() {
        assertNotNull(plugin.getExemptionService());
    }

    @Test
    public void tax_engine_not_null_when_vault_present() {
        assertNotNull(plugin.getTaxEngine(),
                "TaxEngine must be initialized when a Vault economy is registered");
    }

    @Test
    public void gui_config_not_null() {
        assertNotNull(plugin.getGuiConfig());
    }

    // ── command registration ───────────────────────────────────────────────

    @Test
    public void eztax_command_registered_with_executor_and_tab_completer() {
        PluginCommand cmd = plugin.getCommand("eztax");
        assertNotNull(cmd, "/eztax must be registered in plugin.yml");
        assertNotNull(cmd.getExecutor(), "/eztax executor must be set by CommandComponent");
        assertNotNull(cmd.getTabCompleter(), "/eztax tab completer must be set by CommandComponent");
    }

    @Test
    public void tax_command_registered_with_executor() {
        PluginCommand cmd = plugin.getCommand("tax");
        assertNotNull(cmd, "/tax must be registered as a standalone command");
        assertNotNull(cmd.getExecutor(), "/tax executor must be set by CommandComponent");
    }

    @Test
    public void exempt_command_registered() {
        assertNotNull(plugin.getCommand("exempt"), "/exempt must be registered");
    }

    @Test
    public void transactiontax_command_registered() {
        assertNotNull(plugin.getCommand("transactiontax"), "/transactiontax must be registered");
    }

    // ── config defaults loaded ─────────────────────────────────────────────

    @Test
    public void config_has_non_negative_fallback_rate() {
        double rate = plugin.getConfig().getDouble("group-taxes.fallback-rate", -1);
        assertTrue(rate >= 0, "fallback-rate must be a valid non-negative value from config.yml");
    }
}
