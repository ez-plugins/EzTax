package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.command.subcommand.exempt.ExemptSubcommand;
import com.skyblockexp.eztax.command.subcommand.exempt.ExemptionsSubcommand;
import com.skyblockexp.eztax.command.subcommand.exempt.UnexemptSubcommand;
import com.skyblockexp.eztax.service.ExemptionService;
import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the exempt / unexempt / exemptions subcommands, covering
 * permission checks, argument validation, and state transitions.
 */
public class ExemptSubcommandTest extends AbstractEzTaxTest {

    // ── ExemptSubcommand ───────────────────────────────────────────────────

    @Test
    public void exempt_denied_without_permission() {
        PlayerMock sender = server.addPlayer("sender_e1"); // non-op
        PlayerMock target = server.addPlayer("target_e1");
        ExemptSubcommand cmd = new ExemptSubcommand(plugin, plugin.getExemptionService(), plugin.getMessages());

        assertTrue(cmd.execute(sender, new String[]{"exempt", target.getName()}));
        assertFalse(plugin.getExemptionService().isExempt(target.getUniqueId()),
                "non-op sender must not be able to add exemptions");
    }

    @Test
    public void exempt_missing_target_arg_returns_true() {
        PlayerMock op = server.addPlayer("sender_e2");
        op.setOp(true);
        ExemptSubcommand cmd = new ExemptSubcommand(plugin, plugin.getExemptionService(), plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"exempt"}));
        // no exception; usage message is sent to sender
    }

    @Test
    public void exempt_unknown_player_returns_true_without_adding_exemption() {
        PlayerMock op = server.addPlayer("sender_e3");
        op.setOp(true);
        ExemptionService svc = plugin.getExemptionService();
        int before = svc.getExemptCount();
        ExemptSubcommand cmd = new ExemptSubcommand(plugin, svc, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"exempt", "ghost_player_xyz"}));
        assertEquals(before, svc.getExemptCount(), "unknown player must not be added");
    }

    @Test
    public void exempt_online_player_adds_exemption() {
        PlayerMock op     = server.addPlayer("sender_e4");
        PlayerMock target = server.addPlayer("target_e4");
        op.setOp(true);
        ExemptionService svc = plugin.getExemptionService();
        ExemptSubcommand cmd = new ExemptSubcommand(plugin, svc, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"exempt", target.getName()}));
        assertTrue(svc.isExempt(target.getUniqueId()), "online target should now be exempt");
    }

    @Test
    public void exempt_already_exempt_player_returns_true_without_error() {
        PlayerMock op     = server.addPlayer("sender_e5");
        PlayerMock target = server.addPlayer("target_e5");
        op.setOp(true);
        ExemptionService svc = plugin.getExemptionService();
        svc.addExemption(target.getUniqueId());
        ExemptSubcommand cmd = new ExemptSubcommand(plugin, svc, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"exempt", target.getName()}));
        // player remains exempt; no exception thrown
        assertTrue(svc.isExempt(target.getUniqueId()));
    }

    // ── UnexemptSubcommand ────────────────────────────────────────────────

    @Test
    public void unexempt_denied_without_permission() {
        PlayerMock sender = server.addPlayer("sender_u1");
        PlayerMock target = server.addPlayer("target_u1");
        ExemptionService svc = plugin.getExemptionService();
        svc.addExemption(target.getUniqueId());
        UnexemptSubcommand cmd = new UnexemptSubcommand(plugin, svc, plugin.getMessages());

        assertTrue(cmd.execute(sender, new String[]{"unexempt", target.getName()}));
        assertTrue(svc.isExempt(target.getUniqueId()), "non-op must not remove exemptions");
    }

    @Test
    public void unexempt_removes_existing_exemption() {
        PlayerMock op     = server.addPlayer("sender_u2");
        PlayerMock target = server.addPlayer("target_u2");
        op.setOp(true);
        ExemptionService svc = plugin.getExemptionService();
        svc.addExemption(target.getUniqueId());
        UnexemptSubcommand cmd = new UnexemptSubcommand(plugin, svc, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"unexempt", target.getName()}));
        assertFalse(svc.isExempt(target.getUniqueId()), "exemption should be removed");
    }

    @Test
    public void unexempt_non_exempt_player_returns_true_without_error() {
        PlayerMock op     = server.addPlayer("sender_u3");
        PlayerMock target = server.addPlayer("target_u3");
        op.setOp(true);
        ExemptionService svc = plugin.getExemptionService();
        UnexemptSubcommand cmd = new UnexemptSubcommand(plugin, svc, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"unexempt", target.getName()}));
        // sends "not-exempt" message, but no exception
    }

    // ── ExemptionsSubcommand ───────────────────────────────────────────────

    @Test
    public void exemptions_denied_without_permission() {
        PlayerMock sender = server.addPlayer("sender_ex1");
        ExemptionsSubcommand cmd = new ExemptionsSubcommand(plugin, plugin.getExemptionService(), plugin.getMessages());

        assertTrue(cmd.execute(sender, new String[]{"exemptions"}));
    }

    @Test
    public void exemptions_empty_list_returns_true() {
        PlayerMock op = server.addPlayer("sender_ex2");
        op.setOp(true);
        ExemptionsSubcommand cmd = new ExemptionsSubcommand(plugin, plugin.getExemptionService(), plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"exemptions"}));
    }

    @Test
    public void exemptions_lists_exempt_players() {
        PlayerMock op     = server.addPlayer("sender_ex3");
        PlayerMock target = server.addPlayer("target_ex3");
        op.setOp(true);
        ExemptionService svc = plugin.getExemptionService();
        svc.addExemption(target.getUniqueId());
        ExemptionsSubcommand cmd = new ExemptionsSubcommand(plugin, svc, plugin.getMessages());

        assertTrue(cmd.execute(op, new String[]{"exemptions"}));
        assertEquals(1, svc.getExemptCount());
    }
}
