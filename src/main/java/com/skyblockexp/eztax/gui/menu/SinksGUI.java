package com.skyblockexp.eztax.gui.menu;

import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.TaxSink;
import com.skyblockexp.eztax.gui.GUI;
import com.skyblockexp.eztax.gui.IconBuilder;
import org.bukkit.Material;

import java.util.Map;

public class SinksGUI extends GUI {
    private final StatsService statsService;

    public SinksGUI(StatsService statsService, String title, int size) {
        super(size, title);
        this.statsService = statsService;
        build();
    }

    private void build() {
        int slot = 10;
        Map<TaxSink, Double> sinks = statsService.getSinkTotals();
        for (Map.Entry<TaxSink, Double> e : sinks.entrySet()) {
            setItem(slot++, new IconBuilder(Material.PAPER).name(e.getKey().name()).lore(java.util.List.of(String.format("Amount: %.2f", e.getValue()))).build());
            if (slot >= inventory.getSize()) break;
        }
    }
}
