package com.skyblockexp.eztax.gui;

import com.skyblockexp.eztax.compat.PlatformAdapterFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.ClickType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base GUI structure. Subclasses should populate the inventory and register slot actions.
 */
public abstract class GUI {
    private static final Map<Inventory, GUI> REGISTRY = new ConcurrentHashMap<>();

    protected final Inventory inventory;
    protected final Map<Integer, IconClickAction> actions = new ConcurrentHashMap<>();

    protected GUI(int size, String title) {
        this.inventory = PlatformAdapterFactory.get().createInventory(null, size, title);
    }

    protected void registerAction(int slot, IconClickAction action) {
        actions.put(slot, action);
    }

    protected void setItem(int slot, org.bukkit.inventory.ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void open(Player player) {
        REGISTRY.put(inventory, this);
        player.openInventory(inventory);
    }

    public void close(Player player) {
        player.closeInventory();
        REGISTRY.remove(inventory);
    }

    public boolean handleClick(Player player, int slot, ClickType click) {
        IconClickAction action = actions.get(slot);
        if (action != null) {
            try {
                action.onClick(player, click);
            } catch (Exception ignored) {}
            return true;
        }
        return false;
    }

    public static GUI fromInventory(Inventory inv) {
        return REGISTRY.get(inv);
    }

}
