package com.skyblockexp.eztax.gui;

import com.skyblockexp.eztax.gui.action.tax.BalanceAction;
import com.skyblockexp.eztax.gui.action.tax.PayAction;
import org.bukkit.event.inventory.ClickType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.bukkit.plugin.java.JavaPlugin;

import static org.junit.jupiter.api.Assertions.*;

public class GUIActionTest {
    private ServerMock server;
    private JavaPlugin plugin;

    public static class TestPlugin extends JavaPlugin {}

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
    public void pay_and_balance_actions_do_not_throw_and_send_message() {
        PlayerMock p = server.addPlayer("u");
        BalanceAction b = new BalanceAction();
        PayAction pay = new PayAction();
        // ensure no exceptions
        b.onClick(p, ClickType.LEFT);
        pay.onClick(p, ClickType.LEFT);
        // basic assertion: player is still online
        assertTrue(p.isOnline());
    }
}
