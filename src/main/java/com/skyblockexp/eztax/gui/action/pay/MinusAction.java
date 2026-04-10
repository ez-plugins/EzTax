package com.skyblockexp.eztax.gui.action.pay;

import com.skyblockexp.eztax.gui.IconClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import com.skyblockexp.eztax.gui.GUI;
import com.skyblockexp.eztax.gui.menu.PayGUI;

public class MinusAction implements IconClickAction {
    @Override
    public void onClick(Player player, ClickType click) {
        GUI g = GUI.fromInventory(player.getOpenInventory().getTopInventory());
        if (g instanceof PayGUI) {
            PayGUI pay = (PayGUI) g;
            double delta = (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) ? pay.getLargeIncrement() : pay.getSmallIncrement();
            pay.decreaseAmount(delta);
        } else {
            player.sendMessage("No pay GUI in context");
        }
    }
}
