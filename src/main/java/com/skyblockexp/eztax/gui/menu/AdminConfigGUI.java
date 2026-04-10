package com.skyblockexp.eztax.gui.menu;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.gui.GUI;
import com.skyblockexp.eztax.gui.IconBuilder;
import com.skyblockexp.eztax.gui.IconClickAction;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class AdminConfigGUI extends GUI {
    private final EzTaxPlugin plugin;
    private final FileConfiguration cfg;

    public AdminConfigGUI(EzTaxPlugin plugin) {
        super(54, "EzTax - Admin Config");
        this.plugin = plugin;
        this.cfg = plugin.getConfig();

        buildItems();
    }

    private void buildItems() {
        // Transaction tax enabled toggle
        boolean txEnabled = cfg.getBoolean("transaction-tax.enabled", true);
        setItem(10, new IconBuilder(txEnabled ? Material.GREEN_WOOL : Material.RED_WOOL)
                .name("Transaction Tax: " + (txEnabled ? "Enabled" : "Disabled"))
                .lore(List.of("Click to toggle"))
                .build());
        registerAction(10, (player, click) -> {
            boolean cur = plugin.getConfig().getBoolean("transaction-tax.enabled", true);
            plugin.getConfig().set("transaction-tax.enabled", !cur);
            plugin.saveConfig();
            plugin.reloadEzTax();
            boolean now = !cur;
            setItem(10, new IconBuilder(now ? Material.GREEN_WOOL : Material.RED_WOOL)
                    .name("Transaction Tax: " + (now ? "Enabled" : "Disabled"))
                    .lore(List.of("Click to toggle"))
                    .build());
            player.sendMessage("Transaction tax " + (now ? "enabled" : "disabled"));
        });

        // Transaction tax percent (adjust)
        double txPercent = cfg.getDouble("transaction-tax.percentage", 0.0D);
        setItem(12, new IconBuilder(Material.PAPER)
                .name("Transaction Tax %: " + String.format("%.2f", txPercent))
                .lore(List.of("Left click: +0.50%", "Right click: -0.50%"))
                .build());
        registerAction(12, (player, click) -> {
            double cur = plugin.getConfig().getDouble("transaction-tax.percentage", 0.0D);
            double delta = click == ClickType.LEFT ? 0.5D : -0.5D;
            double next = Math.max(0.0D, Math.round((cur + delta) * 100.0D) / 100.0D);
            plugin.getConfig().set("transaction-tax.percentage", next);
            plugin.saveConfig();
            plugin.reloadEzTax();
            setItem(12, new IconBuilder(Material.PAPER)
                    .name("Transaction Tax %: " + String.format("%.2f", next))
                    .lore(List.of("Left click: +0.50%", "Right click: -0.50%"))
                    .build());
            player.sendMessage("Transaction tax percent set to " + String.format("%.2f", next));
        });

        // Wealth tax enabled toggle
        boolean wealthEnabled = cfg.getBoolean("wealth-tax.enabled", false);
        setItem(14, new IconBuilder(wealthEnabled ? Material.GREEN_WOOL : Material.RED_WOOL)
                .name("Wealth Tax: " + (wealthEnabled ? "Enabled" : "Disabled"))
                .lore(List.of("Click to toggle"))
                .build());
        registerAction(14, (player, click) -> {
            boolean cur = plugin.getConfig().getBoolean("wealth-tax.enabled", false);
            plugin.getConfig().set("wealth-tax.enabled", !cur);
            plugin.saveConfig();
            plugin.reloadEzTax();
            boolean now = !cur;
            setItem(14, new IconBuilder(now ? Material.GREEN_WOOL : Material.RED_WOOL)
                    .name("Wealth Tax: " + (now ? "Enabled" : "Disabled"))
                    .lore(List.of("Click to toggle"))
                    .build());
            player.sendMessage("Wealth tax " + (now ? "enabled" : "disabled"));
        });

        // Wealth percent adjust
        double wealthPercent = cfg.getDouble("wealth-tax.percentage", 0.0D);
        setItem(16, new IconBuilder(Material.PAPER)
                .name("Wealth Tax %: " + String.format("%.2f", wealthPercent))
                .lore(List.of("Left click: +0.50%", "Right click: -0.50%"))
                .build());
        registerAction(16, (player, click) -> {
            double cur = plugin.getConfig().getDouble("wealth-tax.percentage", 0.0D);
            double delta = click == ClickType.LEFT ? 0.5D : -0.5D;
            double next = Math.max(0.0D, Math.round((cur + delta) * 100.0D) / 100.0D);
            plugin.getConfig().set("wealth-tax.percentage", next);
            plugin.saveConfig();
            plugin.reloadEzTax();
            setItem(16, new IconBuilder(Material.PAPER)
                    .name("Wealth Tax %: " + String.format("%.2f", next))
                    .lore(List.of("Left click: +0.50%", "Right click: -0.50%"))
                    .build());
            player.sendMessage("Wealth tax percent set to " + String.format("%.2f", next));
        });

        // Treasury enabled
        boolean treas = cfg.getBoolean("server-treasury.enabled", true);
        setItem(28, new IconBuilder(treas ? Material.CHEST : Material.BARRIER)
                .name("Server Treasury: " + (treas ? "Enabled" : "Disabled"))
                .lore(List.of("Click to toggle"))
                .build());
        registerAction(28, (player, click) -> {
            boolean cur = plugin.getConfig().getBoolean("server-treasury.enabled", true);
            plugin.getConfig().set("server-treasury.enabled", !cur);
            plugin.saveConfig();
            plugin.reloadEzTax();
            boolean now = !cur;
            setItem(28, new IconBuilder(now ? Material.CHEST : Material.BARRIER)
                    .name("Server Treasury: " + (now ? "Enabled" : "Disabled"))
                    .lore(List.of("Click to toggle"))
                    .build());
            player.sendMessage("Server treasury " + (now ? "enabled" : "disabled"));
        });

        // Storage type toggle
        String storage = cfg.getString("storage.type", "yml");
        setItem(30, new IconBuilder(Material.CHEST)
                .name("Storage: " + storage)
                .lore(List.of("Click to toggle between yml/mysql"))
                .build());
        registerAction(30, (player, click) -> {
            String cur = plugin.getConfig().getString("storage.type", "yml");
            String next = "yml".equalsIgnoreCase(cur) ? "mysql" : "yml";
            plugin.getConfig().set("storage.type", next);
            plugin.saveConfig();
            plugin.reloadEzTax();
            setItem(30, new IconBuilder(Material.CHEST)
                    .name("Storage: " + next)
                    .lore(List.of("Click to toggle between yml/mysql"))
                    .build());
            player.sendMessage("Storage set to " + next + ". Restart or reconfigure MySQL settings if needed.");
        });

        // Reload / close actions
        setItem(49, new IconBuilder(Material.BARRIER).name("Close").lore(List.of("Close this menu")).build());
        registerAction(49, (player, click) -> close(player));

        setItem(52, new IconBuilder(Material.EMERALD).name("Reload EzTax").lore(List.of("Click to reload plugin config")).build());
        registerAction(52, (player, click) -> {
            plugin.reloadEzTax();
            player.sendMessage("EzTax configuration reloaded.");
        });
    }
}
