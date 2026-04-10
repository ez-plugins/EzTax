package com.skyblockexp.eztax.gui;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.GuiConfig;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.TaxEngine;
import org.bukkit.event.inventory.ClickType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.bukkit.plugin.java.JavaPlugin;

import static org.junit.jupiter.api.Assertions.*;

public class TaxGUIInteractionTest {
    private ServerMock server;
    private EzTaxPlugin plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(EzTaxPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void opening_tax_gui_populates_items_and_handle_click_opens_paygui() {
        PlayerMock p = server.addPlayer("player1");
        TaxConfig cfg = new TaxConfig(plugin);
        StatsService stats = new StatsService(plugin, cfg);
        TaxEngine engine = plugin.getTaxEngine();
        GuiConfig gui = plugin.getGuiConfig();
        com.skyblockexp.eztax.gui.menu.TaxGUI guiObj = new com.skyblockexp.eztax.gui.menu.TaxGUI(plugin, stats, engine, gui, "t", 27);
        guiObj.open(p);
        assertNotNull(p.getOpenInventory());
        // clicking the pay slot (15) should be handled and open another inventory
        boolean handled = guiObj.handleClick(p, 15, ClickType.LEFT);
        assertTrue(handled);
        assertNotNull(p.getOpenInventory());
        guiObj.close(p);
    }
}
