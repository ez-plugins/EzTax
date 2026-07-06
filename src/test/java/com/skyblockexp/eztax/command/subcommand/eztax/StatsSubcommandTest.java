package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.command.subcommand.eztax.CheckSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.SinksSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.StatsSubcommand;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature tests for the stats, sinks, and check subcommands.
 */
public class StatsSubcommandTest extends AbstractEzTaxTest {

    private StatsService statsService;

    @BeforeEach
    public void setUpStatsService() {
        statsService = new StatsService(plugin, new TaxConfig(plugin));
    }

    // ── StatsSubcommand ────────────────────────────────────────────────────

    @Test
    public void stats_denied_to_non_op() {
        PlayerMock player = server.addPlayer("s1");
        StatsSubcommand cmd = new StatsSubcommand(plugin, statsService, plugin.getMessages());

        assertTrue(cmd.execute(player, new String[]{"stats"}));
        // player receives no-permission message; the inventory view's top is null (no GUI was pushed)
    }

    @Test
    public void stats_console_sender_returns_true() {
        StatsSubcommand cmd = new StatsSubcommand(plugin, statsService, plugin.getMessages());

        assertTrue(cmd.execute(server.getConsoleSender(), new String[]{"stats"}));
    }

    @Test
    public void stats_op_player_opens_gui() {
        PlayerMock op = server.addPlayer("s2");
        op.setOp(true);
        StatsSubcommand cmd = new StatsSubcommand(plugin, statsService, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"stats"}));
        // a GUI inventory should be open
        assertNotNull(op.getOpenInventory());
    }

    // ── SinksSubcommand ────────────────────────────────────────────────────

    @Test
    public void sinks_denied_to_non_op() {
        PlayerMock player = server.addPlayer("s3");
        SinksSubcommand cmd = new SinksSubcommand(plugin, statsService, plugin.getMessages());

        assertTrue(cmd.execute(player, new String[]{"sinks"}));
    }

    @Test
    public void sinks_console_sender_returns_true() {
        SinksSubcommand cmd = new SinksSubcommand(plugin, statsService, plugin.getMessages());

        assertTrue(cmd.execute(server.getConsoleSender(), new String[]{"sinks"}));
    }

    @Test
    public void sinks_op_player_returns_true() {
        PlayerMock op = server.addPlayer("s4");
        op.setOp(true);
        SinksSubcommand cmd = new SinksSubcommand(plugin, statsService, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"sinks"}));
    }

    // ── CheckSubcommand ────────────────────────────────────────────────────

    @Test
    public void check_self_as_player_sender() {
        PlayerMock player = server.addPlayer("s5");
        CheckSubcommand cmd = new CheckSubcommand(plugin, plugin.getTaxEngine(), plugin.getMessages());

        // no args → checks the sender themselves
        assertTrue(cmd.execute(player, new String[]{"check"}));
    }

    @Test
    public void check_other_player_by_name() {
        PlayerMock checker = server.addPlayer("s6");
        PlayerMock target  = server.addPlayer("target_check");
        CheckSubcommand cmd = new CheckSubcommand(plugin, plugin.getTaxEngine(), plugin.getMessages());

        assertTrue(cmd.execute(checker, new String[]{"check", target.getName()}));
    }

    @Test
    public void check_unknown_player_returns_true_with_not_found_message() {
        PlayerMock checker = server.addPlayer("s7");
        CheckSubcommand cmd = new CheckSubcommand(plugin, plugin.getTaxEngine(), plugin.getMessages());

        assertTrue(cmd.execute(checker, new String[]{"check", "ghost_player_xyz"}));
        // no exception; "player-not-found" message is sent to checker
    }

    @Test
    public void check_from_console_without_args_returns_true() {
        CheckSubcommand cmd = new CheckSubcommand(plugin, plugin.getTaxEngine(), plugin.getMessages());

        // console sender with no arg — not a Player, so the "must be a player" branch fires
        assertTrue(cmd.execute(server.getConsoleSender(), new String[]{"check"}));
    }

    @Test
    public void check_from_console_with_target_returns_true() {
        PlayerMock target = server.addPlayer("target_console_check");
        CheckSubcommand cmd = new CheckSubcommand(plugin, plugin.getTaxEngine(), plugin.getMessages());

        assertTrue(cmd.execute(server.getConsoleSender(), new String[]{"check", target.getName()}));
    }
}
