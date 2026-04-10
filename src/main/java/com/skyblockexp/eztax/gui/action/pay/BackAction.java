package com.skyblockexp.eztax.gui.action.pay;

import com.skyblockexp.eztax.gui.IconClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BackAction implements IconClickAction {
    @Override
    public void onClick(Player player, ClickType click) {
        player.closeInventory();
    }
}
