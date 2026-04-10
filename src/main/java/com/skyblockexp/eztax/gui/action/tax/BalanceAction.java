package com.skyblockexp.eztax.gui.action.tax;

import com.skyblockexp.eztax.gui.IconClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Shows player's balance in chat when clicked.
 */
public class BalanceAction implements IconClickAction {
    @Override
    public void onClick(Player player, ClickType click) {
        player.sendMessage("Your balance: " + "(balance lookup not implemented)");
    }
}
