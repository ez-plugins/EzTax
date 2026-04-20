package com.skyblockexp.eztax.test;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.economy.InternalEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/**
 * Common MockBukkit setup/teardown for EzTax tests.
 */
public abstract class AbstractEzTaxTest {
    protected ServerMock server;
    protected EzTaxPlugin plugin;

    /** Minimal plugin used only to own the registered Economy service. */
    public static class TestEconomyPlugin extends JavaPlugin {
        @Override
        public void onEnable() {
            InternalEconomy economy = new InternalEconomy(this);
            getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.Normal);
        }
    }

    @BeforeEach
    public void setUpBase() {
        server = MockBukkit.mock();
        // Register InternalEconomy as a Vault economy service so VaultHook.hook() succeeds
        MockBukkit.load(TestEconomyPlugin.class);
        // allow subclasses to install additional mock providers before plugin is loaded
        onBeforePluginLoad();
        plugin = MockBukkit.load(EzTaxPlugin.class);
        onAfterPluginLoad();
    }

    /**
     * Hook to run before EzTax plugin is loaded (e.g., load mock perm provider)
     */
    protected void onBeforePluginLoad() {
    }

    /**
     * Hook to run after EzTax plugin is loaded.
     */
    protected void onAfterPluginLoad() {
    }

    @AfterEach
    public void tearDownBase() {
        MockBukkit.unmock();
    }

    protected Permission getPermissionProvider() {
        var reg = server.getServicesManager().getRegistration(Permission.class);
        return reg != null ? reg.getProvider() : null;
    }
}
