package com.skyblockexp.eztax.gui.listener;

import com.skyblockexp.eztax.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

public class TaxGUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent ev) {
        if (ev.getClickedInventory() == null) return;
        GUI gui = GUI.fromInventory(ev.getClickedInventory());
        if (gui == null) return;
        ev.setCancelled(true);
        if (ev.getWhoClicked() instanceof Player) {
            Player p = (Player) ev.getWhoClicked();
            gui.handleClick(p, ev.getSlot(), ev.getClick());
        }
    }
}
