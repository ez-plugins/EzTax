package com.skyblockexp.eztax;

import com.skyblockexp.eztax.bootstrap.PluginBootstrap;
import com.skyblockexp.eztax.config.GuiConfig;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.service.ExemptionService;
import com.skyblockexp.eztax.service.TaxEngine;
import org.bukkit.plugin.java.JavaPlugin;

public class EzTaxPlugin extends JavaPlugin {
    private PluginBootstrap bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new PluginBootstrap(this);
        bootstrap.start();
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) bootstrap.stop();
    }

    public void reloadEzTax() {
        if (bootstrap != null) bootstrap.reload();
    }

    public GuiConfig getGuiConfig() {
        return bootstrap != null ? bootstrap.getTaxConfig() != null ? bootstrap.getConfigComponent().getGuiConfig() : null : null;
    }

    public Messages getMessages() {
        return bootstrap != null ? bootstrap.getMessages() : null;
    }

    public ExemptionService getExemptionService() {
        return bootstrap != null ? bootstrap.getExemptionComponent() != null ? bootstrap.getExemptionComponent().getExemptionService() : null : null;
    }

    public TaxEngine getTaxEngine() {
        return bootstrap != null ? (bootstrap.getTaxEngineComponent() != null ? bootstrap.getTaxEngineComponent().getTaxEngine() : null) : null;
    }

    private void registerListeners() {
        // no-op; listeners are registered by bootstrap components
    }

    private void registerCommands() {
        // commands are registered by CommandComponent
    }
}
