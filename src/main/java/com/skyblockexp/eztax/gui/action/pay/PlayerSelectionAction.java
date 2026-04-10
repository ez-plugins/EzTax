package com.skyblockexp.eztax.gui.action.pay;

import com.skyblockexp.eztax.gui.IconClickAction;
import com.skyblockexp.eztax.gui.GUI;
import com.skyblockexp.eztax.gui.menu.PayGUI;
import com.skyblockexp.eztax.gui.menu.PlayerSelectionGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

public class PlayerSelectionAction implements IconClickAction {
    private final PayGUI payGUI;

    public PlayerSelectionAction(PayGUI payGUI) {
        this.payGUI = Objects.requireNonNull(payGUI);
    }

    @Override
    public void onClick(Player player, ClickType click) {
        PlayerSelectionGUI gui = new PlayerSelectionGUI("Select Player", 54, selected -> {
            // set target on the PayGUI that opened the selector
            payGUI.setTarget(selected);
            // re-open the pay GUI for the player so they see the updated target
            payGUI.open(player);
        });
        gui.open(player);
    }
}
