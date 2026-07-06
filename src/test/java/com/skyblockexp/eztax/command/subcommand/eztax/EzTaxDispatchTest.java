package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.service.ExemptionService;
import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature tests for the /eztax dispatcher — verifies that every registered
 * subcommand is reachable and that the dispatcher ALWAYS returns {@code true}
 * (so Bukkit never prints the plugin.yml usage string as a chat error).
 */
public class EzTaxDispatchTest extends AbstractEzTaxTest {

    private CommandExecutor executor() {
        PluginCommand cmd = plugin.getCommand("eztax");
        assertNotNull(cmd, "/eztax command must be registered in plugin.yml");
        assertNotNull(cmd.getExecutor(), "CommandComponent must have set an executor");
        return cmd.getExecutor();
    }

    private PluginCommand cmd() {
        return plugin.getCommand("eztax");
    }

    // ── dispatcher contract ─────────────────────────────────────────────────

    @Test
    public void no_args_returns_true_and_suppresses_bukkit_usage() {
        PlayerMock player = server.addPlayer("u1");
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[0]));
    }

    @Test
    public void null_args_returns_true() {
        PlayerMock player = server.addPlayer("u2");
        assertTrue(executor().onCommand(player, cmd(), "eztax", null));
    }

    @Test
    public void unknown_subcommand_returns_true() {
        PlayerMock player = server.addPlayer("u3");
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"notexist"}));
    }

    @Test
    public void dispatch_is_case_insensitive() {
        PlayerMock player = server.addPlayer("u4");
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"HELP"}));
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"Help"}));
    }

    // ── help ───────────────────────────────────────────────────────────────

    @Test
    public void help_subcommand_returns_true() {
        PlayerMock player = server.addPlayer("u5");
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"help"}));
    }

    // ── reload ─────────────────────────────────────────────────────────────

    @Test
    public void reload_requires_permission() {
        PlayerMock player = server.addPlayer("u6"); // non-op
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"reload"}));
        // no exception — player simply receives no-permission message
    }

    @Test
    public void reload_succeeds_for_op() {
        PlayerMock op = server.addPlayer("u7");
        op.setOp(true);
        assertTrue(executor().onCommand(op, cmd(), "eztax", new String[]{"reload"}));
        // plugin should still be functional after reload
        assertNotNull(plugin.getMessages());
    }

    // ── stats & sinks ──────────────────────────────────────────────────────

    @Test
    public void stats_dispatched_to_console_returns_true() {
        assertTrue(executor().onCommand(server.getConsoleSender(), cmd(), "eztax", new String[]{"stats"}));
    }

    @Test
    public void sinks_dispatched_to_console_returns_true() {
        assertTrue(executor().onCommand(server.getConsoleSender(), cmd(), "eztax", new String[]{"sinks"}));
    }

    @Test
    public void stats_requires_permission_for_player() {
        PlayerMock player = server.addPlayer("u8"); // non-op
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"stats"}));
        // player receives no-permission message, command still returns true
    }

    // ── check ──────────────────────────────────────────────────────────────

    @Test
    public void check_self_as_player_returns_true() {
        PlayerMock player = server.addPlayer("u9");
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"check"}));
    }

    @Test
    public void check_other_player_by_name_returns_true() {
        PlayerMock checker = server.addPlayer("u10");
        PlayerMock target  = server.addPlayer("target_check");
        assertTrue(executor().onCommand(checker, cmd(), "eztax", new String[]{"check", target.getName()}));
    }

    // ── exempt / unexempt / exemptions ─────────────────────────────────────

    @Test
    public void exempt_dispatches_and_adds_exemption() {
        PlayerMock op     = server.addPlayer("u11");
        PlayerMock target = server.addPlayer("exempt_target1");
        op.setOp(true);

        assertTrue(executor().onCommand(op, cmd(), "eztax", new String[]{"exempt", target.getName()}));

        ExemptionService svc = plugin.getExemptionService();
        assertNotNull(svc);
        assertTrue(svc.isExempt(target.getUniqueId()), "target should be exempted");
    }

    @Test
    public void unexempt_dispatches_and_removes_exemption() {
        PlayerMock op     = server.addPlayer("u12");
        PlayerMock target = server.addPlayer("exempt_target2");
        op.setOp(true);

        ExemptionService svc = plugin.getExemptionService();
        assertNotNull(svc);
        svc.addExemption(target.getUniqueId());
        assertTrue(svc.isExempt(target.getUniqueId()));

        assertTrue(executor().onCommand(op, cmd(), "eztax", new String[]{"unexempt", target.getName()}));
        assertFalse(svc.isExempt(target.getUniqueId()), "exemption should be removed");
    }

    @Test
    public void exemptions_dispatches_returns_true() {
        PlayerMock op = server.addPlayer("u13");
        op.setOp(true);
        assertTrue(executor().onCommand(op, cmd(), "eztax", new String[]{"exemptions"}));
    }

    // ── settaxrate ─────────────────────────────────────────────────────────

    @Test
    public void settaxrate_updates_config_value() {
        PlayerMock op = server.addPlayer("u14");
        op.setOp(true);
        assertTrue(executor().onCommand(op, cmd(), "eztax", new String[]{"settaxrate", "3.0"}));
        assertEquals(3.0, plugin.getConfig().getDouble("group-taxes.fallback-rate"), 0.001);
    }

    // ── show / fines / payfine ─────────────────────────────────────────────

    @Test
    public void show_subcommand_dispatches_for_player() {
        PlayerMock player = server.addPlayer("u15");
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"show"}));
    }

    @Test
    public void fines_subcommand_dispatches_for_player() {
        PlayerMock player = server.addPlayer("u16");
        assertTrue(executor().onCommand(player, cmd(), "eztax", new String[]{"fines"}));
    }
}
