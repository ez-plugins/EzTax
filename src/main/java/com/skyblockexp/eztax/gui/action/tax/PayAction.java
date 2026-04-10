package com.skyblockexp.eztax.gui.action.tax;

import com.skyblockexp.eztax.gui.IconClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Opens the payment GUI when clicked.
 */
public class PayAction implements IconClickAction {
    @Override
    public void onClick(Player player, ClickType click) {
        player.sendMessage("Opening pay GUI... (not implemented)");
    }
}
