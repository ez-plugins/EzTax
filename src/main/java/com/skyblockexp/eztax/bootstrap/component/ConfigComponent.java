package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.GuiConfig;
import com.skyblockexp.eztax.config.Messages;

public class ConfigComponent implements Component {
    private final EzTaxPlugin plugin;
    private TaxConfig taxConfig;
    private GuiConfig guiConfig;
    private Messages messages;

    public ConfigComponent(EzTaxPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        plugin.saveDefaultConfig();
        this.taxConfig = new TaxConfig(plugin);
        this.guiConfig = new GuiConfig(plugin);
        this.messages = new Messages(plugin);
    }

    @Override
    public void stop() {
        // nothing
    }

    @Override
    public void reload() {
        stop();
        start();
    }

    public TaxConfig getTaxConfig() { return taxConfig; }
    public GuiConfig getGuiConfig() { return guiConfig; }
    public Messages getMessages() { return messages; }
}
