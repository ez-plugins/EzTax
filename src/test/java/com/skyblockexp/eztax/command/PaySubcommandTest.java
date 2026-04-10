package com.skyblockexp.eztax.command;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.command.subcommand.eztax.PaySubcommand;
import com.skyblockexp.eztax.config.GuiConfig;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaySubcommandTest extends AbstractEzTaxTest {

    static class TaxEngineStub extends TaxEngine {
        private double due = 0.0;
        private boolean payCalled = false;

        public TaxEngineStub(TaxConfig cfg, StatsService stats, Object vh) {
            super(cfg, stats, null, java.util.logging.Logger.getAnonymousLogger());
        }

        @Override
        public double getTaxDue(UUID player) {
            return due;
        }

        @Override
        public boolean payTax(UUID player, double amount) {
            this.payCalled = true;
            return amount > 0 && due > 0;
        }

        @Override
        public String format(double amount) {
            return String.format("%.2f", amount);
        }
    }

    @Test
    public void player_without_args_opens_pay_gui() {
        PlayerMock p = server.addPlayer("payer1");
        TaxConfig cfg = new TaxConfig(plugin);
        StatsService stats = new StatsService(plugin, cfg);
        TaxEngineStub stub = new TaxEngineStub(cfg, stats, null);
        PaySubcommand cmd = new PaySubcommand(plugin, stub, plugin.getMessages(), plugin.getGuiConfig());

        boolean res = cmd.execute(p, new String[]{"pay"});
        assertTrue(res);
        assertNotNull(p.getOpenInventory());
    }

    @Test
    public void target_with_no_taxes_does_not_attempt_payment() {
        PlayerMock payer = server.addPlayer("payer2");
        PlayerMock target = server.addPlayer("target2");
        TaxConfig cfg = new TaxConfig(plugin);
        StatsService stats = new StatsService(plugin, cfg);
        TaxEngineStub stub = new TaxEngineStub(cfg, stats, null);
        stub.due = 0.0;

        PaySubcommand cmd = new PaySubcommand(plugin, stub, plugin.getMessages(), plugin.getGuiConfig());
        boolean res = cmd.execute(payer, new String[]{"pay", target.getName()});
        assertTrue(res);
        assertFalse(stub.payCalled);
    }

    @Test
    public void successful_pay_calls_engine_and_reports_success() {
        PlayerMock payer = server.addPlayer("payer3");
        PlayerMock target = server.addPlayer("target3");
        TaxConfig cfg = new TaxConfig(plugin);
        StatsService stats = new StatsService(plugin, cfg);
        TaxEngineStub stub = new TaxEngineStub(cfg, stats, null);
        stub.due = 15.0;

        PaySubcommand cmd = new PaySubcommand(plugin, stub, plugin.getMessages(), plugin.getGuiConfig());
        boolean res = cmd.execute(payer, new String[]{"pay", target.getName()});
        assertTrue(res);
        assertTrue(stub.payCalled);
    }

    @Test
    public void failed_pay_attempt_records_attempt_and_reports_failure() {
        PlayerMock payer = server.addPlayer("payer4");
        PlayerMock target = server.addPlayer("target4");
        TaxConfig cfg = new TaxConfig(plugin);
        StatsService stats = new StatsService(plugin, cfg);
        TaxEngineStub stub = new TaxEngineStub(cfg, stats, null);
        stub.due = 20.0;

        // override payTax to simulate failure
        TaxEngine failing = new TaxEngine(cfg, stats, null, java.util.logging.Logger.getAnonymousLogger()) {
            @Override
            public boolean payTax(UUID player, double amount) {
                return false;
            }

            @Override
            public double getTaxDue(UUID player) { return 20.0; }

            @Override
            public String format(double amount) { return String.format("%.2f", amount); }
        };

        PaySubcommand cmd = new PaySubcommand(plugin, failing, plugin.getMessages(), plugin.getGuiConfig());
        boolean res = cmd.execute(payer, new String[]{"pay", target.getName()});
        assertTrue(res);
    }
}
