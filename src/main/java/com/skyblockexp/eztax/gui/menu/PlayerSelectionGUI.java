package com.skyblockexp.eztax.gui.menu;

import com.skyblockexp.eztax.compat.PlatformAdapterFactory;
import com.skyblockexp.eztax.gui.GUI;
import com.skyblockexp.eztax.gui.IconBuilder;
import com.skyblockexp.eztax.gui.IconClickAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Iterator;
import java.util.function.Consumer;

public class PlayerSelectionGUI extends GUI {
    private final Consumer<Player> onSelect;

    public PlayerSelectionGUI(String title, int size, Consumer<Player> onSelect) {
        super(size, title);
        this.onSelect = onSelect;
        build();
    }

    private void build() {
        int slot = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (slot >= inventory.getSize()) break;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                PlatformAdapterFactory.get().setDisplayName(meta, p.getName());
                meta.setOwningPlayer(p);
                head.setItemMeta(meta);
            }
            setItem(slot, head);
            final int thisSlot = slot;
            registerAction(thisSlot, new IconClickAction() {
                @Override
                public void onClick(Player viewer, org.bukkit.event.inventory.ClickType click) {
                    try {
                        onSelect.accept(p);
                    } catch (Exception ignored) {}
                    close(viewer);
                }
            });
            slot++;
        }
        // If no players, show placeholder
        if (slot == 0) {
            setItem(4, new IconBuilder(Material.PAPER).name("No players online").build());
        }
    }
}
