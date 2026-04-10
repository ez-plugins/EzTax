package com.skyblockexp.eztax.test;

import com.skyblockexp.eztax.EzTaxPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import net.milkbowl.vault.permission.Permission;

/**
 * Common MockBukkit setup/teardown for EzTax tests.
 */
public abstract class AbstractEzTaxTest {
    protected ServerMock server;
    protected EzTaxPlugin plugin;

    @BeforeEach
    public void setUpBase() {
        server = MockBukkit.mock();
        // allow subclasses to install mock providers before plugin is loaded
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
