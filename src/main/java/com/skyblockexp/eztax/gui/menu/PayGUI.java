package com.skyblockexp.eztax.gui.menu;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.gui.GUI;
import com.skyblockexp.eztax.gui.IconBuilder;
import com.skyblockexp.eztax.gui.action.pay.BackAction;
import com.skyblockexp.eztax.gui.action.pay.ConfirmAction;
import com.skyblockexp.eztax.gui.action.pay.MinusAction;
import com.skyblockexp.eztax.gui.action.pay.PlusAction;
import com.skyblockexp.eztax.gui.action.pay.PlayerSelectionAction;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PayGUI extends GUI {
    private UUID target;
    private double amount = 0.0;
    private final double smallIncrement;
    private final double largeIncrement;

    private final EzTaxPlugin plugin;

    public PayGUI(EzTaxPlugin plugin, String title, int size) {
        super(size, title);
        this.plugin = plugin;
        // load increments and labels from gui config
        var cfg = plugin.getGuiConfig().getConfig();
        this.smallIncrement = cfg.getDouble("increments.small", 1.0);
        this.largeIncrement = cfg.getDouble("increments.large", 10.0);
        build();
    }

    private void build() {
        // player selection slot
        var guiCfg = plugin.getGuiConfig();
        String targetNone = guiCfg.getLabel("labels.target-none");
        setItem(10, new IconBuilder(Material.PLAYER_HEAD).name(targetNone).build());
        registerAction(10, new PlayerSelectionAction(this));

        setItem(12, new IconBuilder(Material.REDSTONE).name("-").build());
        registerAction(12, new MinusAction());

        String amountFmt = guiCfg.getLabel("labels.amount-format");
        if (amountFmt == null || amountFmt.isEmpty()) amountFmt = "Amount: %s";
        setItem(13, new IconBuilder(Material.GOLD_INGOT).name(String.format(amountFmt, plugin.getTaxEngine().format(0.0))).build());

        setItem(14, new IconBuilder(Material.GREEN_WOOL).name("+").build());
        registerAction(14, new PlusAction());

        String confirmLabel = guiCfg.getLabel("labels.confirm");
        if (confirmLabel == null || confirmLabel.isEmpty()) confirmLabel = "Confirm";
        setItem(26, new IconBuilder(Material.LIME_CONCRETE).name(confirmLabel).build());
        registerAction(26, new ConfirmAction());

        setItem(18, new IconBuilder(Material.ARROW).name("Back").build());
        registerAction(18, new BackAction());
    }

    public void setTarget(OfflinePlayer player) {
        if (player == null) {
            this.target = null;
                String label = plugin.getGuiConfig().getLabel("labels.target-none");
            setItem(10, new IconBuilder(Material.PLAYER_HEAD).name(label).build());
        } else {
            this.target = player.getUniqueId();
            String label = plugin.getGuiConfig().getLabel("labels.target-format", java.util.Map.of("target", player.getName()));
            setItem(10, new IconBuilder(Material.PLAYER_HEAD).name(label).build());
        }
    }

    public UUID getTarget() {
        return target;
    }

    public double getAmount() {
        return amount;
    }

    public double getSmallIncrement() {
        return smallIncrement;
    }

    public double getLargeIncrement() {
        return largeIncrement;
    }

    public void increaseAmount(double delta) {
        this.amount = Math.max(0.0, round(amount + delta));
        updateAmountDisplay();
    }

    public void decreaseAmount(double delta) {
        this.amount = Math.max(0.0, round(amount - delta));
        updateAmountDisplay();
    }

    private void updateAmountDisplay() {
        String formatted = plugin.getTaxEngine().format(amount);
        var guiCfg = plugin.getGuiConfig();
        String label = guiCfg.getLabel("labels.amount-format", java.util.Map.of("amount", formatted));
        setItem(13, new IconBuilder(Material.GOLD_INGOT).name(label).build());
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
