package com.skyblockexp.eztax.economy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

public class InternalEconomyTest {
    private ServerMock server;
    private JavaPlugin plugin;
    private InternalEconomy econ;

    public static class TestPlugin extends JavaPlugin {}

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(TestPlugin.class);
        econ = new InternalEconomy(plugin);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void deposit_and_withdraw_change_balance() {
        PlayerMock p = server.addPlayer("payer");
        var deposit = econ.depositPlayer(p, 100.0);
        assertEquals(100.0, deposit.amount, 0.001);
        assertEquals(100.0, econ.getBalance(p), 0.001);

        var withdraw = econ.withdrawPlayer(p, 40.0);
        assertEquals(40.0, withdraw.amount, 0.001);
        assertEquals(60.0, econ.getBalance(p), 0.001);
    }

    @Test
    public void withdraw_insufficient_funds_returns_failure() {
        PlayerMock p = server.addPlayer("poor");
        var r = econ.withdrawPlayer(p, 5.0);
        assertFalse(r.transactionSuccess());
        assertEquals(0.0, econ.getBalance(p), 0.001);
    }

    @Test
    public void create_player_account_initializes_balance() {
        PlayerMock p = server.addPlayer("newplayer");
        boolean created = econ.createPlayerAccount(p);
        assertTrue(created);
        assertEquals(0.0, econ.getBalance(p), 0.001);
    }
}
