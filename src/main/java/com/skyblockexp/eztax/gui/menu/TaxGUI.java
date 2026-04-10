package com.skyblockexp.eztax.gui.menu;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.gui.GUI;
import com.skyblockexp.eztax.gui.IconBuilder;
import com.skyblockexp.eztax.config.GuiConfig;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.TaxEngine;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.ArrayList;
import java.util.List;

public class TaxGUI extends GUI {
    private final EzTaxPlugin plugin;
    private final StatsService statsService;
    private final TaxEngine taxEngine;
    private final GuiConfig guiConfig;

    public TaxGUI(EzTaxPlugin plugin, StatsService statsService, TaxEngine taxEngine, GuiConfig guiConfig, String title, int size) {
        super(size, title);
        this.plugin = plugin;
        this.statsService = statsService;
        this.taxEngine = taxEngine;
        this.guiConfig = guiConfig;
        build();
    }

    private void build() {
        // Total removed
        ItemStack total = new IconBuilder(Material.PAPER)
                .name("Total Collected")
                .build();
        List<String> lore = new ArrayList<>();
        lore.add("Total: " + String.format("%.2f", statsService.getTotalRemoved()));
        lore.add("Daily: " + String.format("%.2f", statsService.getDailyRemoved()));
        lore.add("Weekly: " + String.format("%.2f", statsService.getWeeklyRemoved()));
        // apply lore
        setItem(11, new IconBuilder(Material.PAPER).name("Totals").lore(lore).build());

        // Treasury
        setItem(13, new IconBuilder(Material.CHEST).name("Treasury").lore(List.of("Balance: " + String.format("%.2f", statsService.getTreasuryBalance()))).build());

        // Pay button opens PayGUI
        setItem(15, new IconBuilder(Material.EMERALD).name("Pay Taxes").build());
        registerAction(15, (Player p, ClickType click) -> {
            var cfg = guiConfig.getConfig().getConfigurationSection("menus").getConfigurationSection("pay");
            String title = cfg.getString("title", "EzTax - Pay Tax");
            int size = cfg.getInt("size", 27);
            new com.skyblockexp.eztax.gui.menu.PayGUI(plugin, title, size).open(p);
        });
    }
}
