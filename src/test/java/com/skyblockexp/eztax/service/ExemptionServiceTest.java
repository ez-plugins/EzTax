package com.skyblockexp.eztax.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ExemptionServiceTest {
    private ServerMock server;
    private JavaPlugin plugin;
    private ExemptionService svc;

    public static class TestPlugin extends JavaPlugin {}

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(TestPlugin.class);
        svc = new ExemptionService(plugin);
        // ensure fresh state
        for (UUID id : svc.getExemptPlayers()) {
            svc.removeExemption(id);
        }
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void add_and_remove_exemption_updates_store() {
        UUID id = UUID.randomUUID();
        assertFalse(svc.isExempt(id));
        svc.addExemption(id);
        assertTrue(svc.isExempt(id));
        assertEquals(1, svc.getExemptCount());
        boolean removed = svc.removeExemption(id);
        assertTrue(removed);
        assertFalse(svc.isExempt(id));
        assertEquals(0, svc.getExemptCount());
    }

    @Test
    public void player_exemption_overloads_work() {
        var player = server.addPlayer("testplayer");
        UUID id = player.getUniqueId();
        svc.addExemption(player);
        assertTrue(svc.isExempt(id));
        assertTrue(svc.isExempt(player));
    }
}
