package com.skyblockexp.eztax.command;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.command.execute.ExemptExecute;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.service.ExemptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.bukkit.plugin.java.JavaPlugin;

import static org.junit.jupiter.api.Assertions.*;

public class ExemptExecuteTest {
    private ServerMock server;
    private JavaPlugin plugin;
    private ExemptionService svc;

    public static class TestPlugin extends JavaPlugin {}

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        // load the real plugin so commands and messages are available
        plugin = MockBukkit.load(com.skyblockexp.eztax.EzTaxPlugin.class);
        svc = ((com.skyblockexp.eztax.EzTaxPlugin) plugin).getExemptionService();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void execute_adds_exemption_for_existing_player() {
        PlayerMock player = server.addPlayer("target");
        Messages messages = ((com.skyblockexp.eztax.EzTaxPlugin) plugin).getMessages();
        ExemptExecute exec = new ExemptExecute((com.skyblockexp.eztax.EzTaxPlugin) plugin, svc, messages);

        // sender will be the online player issuing the command
        boolean result = exec.execute(player, new String[]{"exempt", "target"});
        assertTrue(result);
        assertTrue(svc.isExempt(player.getUniqueId()));
    }
}
