package com.skyblockexp.eztax.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Represents an action when a GUI icon is clicked.
 */
@FunctionalInterface
public interface IconClickAction {
    void onClick(Player player, ClickType click);
}
