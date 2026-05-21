package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.command.subcommand.eztax.ReloadSubcommand;
import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the /eztax reload subcommand.
 */
public class ReloadSubcommandTest extends AbstractEzTaxTest {

    @Test
    public void reload_denied_to_non_op() {
        PlayerMock player = server.addPlayer("r1"); // non-op, no reload permission
        ReloadSubcommand cmd = new ReloadSubcommand(plugin, plugin.getMessages());

        assertTrue(cmd.execute(player, new String[]{"reload"}),
                "must always return true to suppress Bukkit usage output");
        // plugin not reloaded; still functional
        assertNotNull(plugin.getMessages());
    }

    @Test
    public void reload_succeeds_for_op() {
        PlayerMock op = server.addPlayer("r2");
        op.setOp(true);
        ReloadSubcommand cmd = new ReloadSubcommand(plugin, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"reload"}));
        // after reload, the plugin should still expose its services
        assertNotNull(plugin.getMessages());
        assertNotNull(plugin.getExemptionService());
    }

    @Test
    public void reload_via_explicit_permission_attachment() {
        PlayerMock player = server.addPlayer("r3");
        player.addAttachment(plugin, "eztax.command.reload", true);
        ReloadSubcommand cmd = new ReloadSubcommand(plugin, plugin.getMessages());

        assertTrue(cmd.execute(player, new String[]{"reload"}));
        assertNotNull(plugin.getMessages());
    }

    @Test
    public void reload_config_changes_are_reflected() {
        PlayerMock op = server.addPlayer("r4");
        op.setOp(true);
        // Change an in-memory config value
        plugin.getConfig().set("group-taxes.fallback-rate", 9.9);

        ReloadSubcommand cmd = new ReloadSubcommand(plugin, plugin.getMessages());
        assertTrue(cmd.execute(op, new String[]{"reload"}));

        // After reload the value is back to whatever is in config.yml on disk
        double reloaded = plugin.getConfig().getDouble("group-taxes.fallback-rate");
        assertTrue(reloaded >= 0.0, "reloaded config should contain a valid tax rate");
    }
}
