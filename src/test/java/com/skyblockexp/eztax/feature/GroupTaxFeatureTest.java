package com.skyblockexp.eztax.feature;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.test.AbstractEzTaxTest;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.economy.Economy;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class GroupTaxFeatureTest extends AbstractEzTaxTest {

    public static class MockPermPlugin extends JavaPlugin {
        @Override
        public void onEnable() {
            Permission provider = new Permission() {
                @Override public String getName() { return "MockPerm"; }
                @Override public boolean isEnabled() { return true; }
                @Override public boolean hasSuperPermsCompat() { return false; }
                @Override public boolean playerHas(String world, String player, String permission) { return false; }
                @Override public boolean playerAdd(String world, String player, String permission) { return false; }
                @Override public boolean playerRemove(String world, String player, String permission) { return false; }
                @Override public boolean groupHas(String world, String group, String permission) { return false; }
                @Override public boolean groupAdd(String world, String group, String permission) { return false; }
                @Override public boolean groupRemove(String world, String group, String permission) { return false; }
                @Override public boolean playerInGroup(String world, String player, String group) { return getPrimaryGroup(world, player).equals(group); }
                @Override public boolean playerAddGroup(String world, String player, String group) { return false; }
                @Override public boolean playerRemoveGroup(String world, String player, String group) { return false; }
                @Override public String[] getPlayerGroups(String world, String player) { return new String[]{getPrimaryGroup(world, player)}; }
                @Override public String getPrimaryGroup(String world, String player) { return player.startsWith("vip") ? "vip" : "default"; }
                @Override public String[] getGroups() { return new String[]{"default","vip"}; }
                @Override public boolean hasGroupSupport() { return true; }
            };
            getServer().getServicesManager().register(Permission.class, provider, this, ServicePriority.Normal);
        }
    }

    @Override
    protected void onBeforePluginLoad() {
        // load permission plugin first so EzTax hooks it
        MockBukkit.load(MockPermPlugin.class);
    }

    @Test
    public void group_based_transaction_tax_applies_different_rate() {
        // configure group rates in plugin config
        plugin.getConfig().set("transaction-tax.percentage", 5.0);
        plugin.getConfig().set("group-taxes.enabled", true);
        plugin.getConfig().set("group-taxes.fallback-rate", -1.0);
        // set group-specific percent for vip
        plugin.getConfig().set("group-taxes.groups.vip.transaction-tax", 1.0);
        // ensure default group is higher so regular players pay more
        plugin.getConfig().set("group-taxes.groups.default.transaction-tax", 5.0);
        plugin.saveConfig();
        plugin.saveConfig();
        
        plugin.reloadEzTax();
        

        PlayerMock vip = server.addPlayer("vipAlice");
        PlayerMock regular = server.addPlayer("bob");
        Economy econ = plugin.getTaxEngine().getEconomy();

        econ.depositPlayer(vip, 1000.0);
        econ.depositPlayer(regular, 1000.0);

        double withdraw = 100.0;
        var r1 = econ.withdrawPlayer(vip, withdraw);
        var r2 = econ.withdrawPlayer(regular, withdraw);
        assertTrue(r1.transactionSuccess());
        assertTrue(r2.transactionSuccess());
            // compute transaction tax amounts via TaxEngine to assert group-based rates
            double vipTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(vip.getPlayer(), withdraw);
            double bobTax = plugin.getTaxEngine().calculateTransactionTaxForPlayer(regular.getPlayer(), withdraw);
            
            // debug: check provider-reported groups
            Permission perm = server.getServicesManager().getRegistration(Permission.class).getProvider();
            
            assertTrue(vipTax < bobTax, "VIP should be charged lower transaction tax than regular player");
            // Also verify balances reflect this
            double vipBal = econ.getBalance(vip);
            double bobBal = econ.getBalance(regular);
            assertTrue(vipBal > bobBal, "VIP should have a higher balance after withdraw (lower tax)");
    }
}
