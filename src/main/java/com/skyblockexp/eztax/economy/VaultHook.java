package com.skyblockexp.eztax.economy;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.economy.InternalEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultHook {
    private final JavaPlugin plugin;
    private Economy economy;
    private Permission permission;
    private TaxedEconomy taxedEconomy;
    private boolean registered;

    public VaultHook(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hook() {
        RegisteredServiceProvider<Economy> registration = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null && registration != null && registration.getProvider() != null) {
            this.economy = registration.getProvider();
            plugin.getLogger().info("Using Vault economy provider: " + economy.getName());
        } else {
            plugin.getLogger().warning("Vault or Vault economy provider not found. Falling back to internal economy.");
            this.economy = new InternalEconomy(plugin);
            plugin.getLogger().info("Using internal EzTax economy provider: " + economy.getName());
        }
        
        // Try to hook permission system for group support
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null && permissionProvider.getProvider() != null) {
            this.permission = permissionProvider.getProvider();
            plugin.getLogger().info("Using Vault permission provider: " + permission.getName());
        } else {
            plugin.getLogger().info("No Vault permission provider found. Group-based taxes will not be available.");
        }
        
        return true;
    }

    public Economy getEconomy() {
        return economy;
    }
    
    public Permission getPermission() {
        return permission;
    }
    
    public String getPrimaryGroup(Player player) {
        if (permission == null) {
            return null;
        }
        try {
            return permission.getPrimaryGroup(player);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get primary group for " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }

    public void registerTaxedEconomy(TaxEngine taxEngine, TaxConfig config) {
        if (registered || economy == null) return;
        // Avoid wrapping if already a TaxedEconomy
        if (economy instanceof TaxedEconomy) {
            this.taxedEconomy = (TaxedEconomy) economy;
            // ensure the existing wrapper uses our TaxEngine instance
            if (taxEngine != null) {
                this.taxedEconomy.setTaxEngine(taxEngine);
                taxEngine.setEconomy(this.economy);
            }
            registered = true;
            return;
        }
        this.taxedEconomy = new TaxedEconomy(economy, taxEngine, config, plugin.getLogger());
        plugin.getServer().getServicesManager().register(Economy.class, taxedEconomy, plugin, ServicePriority.Highest);
        // Update internal reference so callers can obtain the taxed wrapper after registration
        this.economy = this.taxedEconomy;
        registered = true;
        if (taxEngine != null) {
            // ensure TaxEngine sees the new taxed wrapper
            this.taxedEconomy.setTaxEngine(taxEngine);
            taxEngine.setEconomy(this.economy);
        }
        plugin.getLogger().info("EzTax registered an economy wrapper for transaction taxes.");
    }

    public void unregisterTaxedEconomy() {
        if (!registered || taxedEconomy == null) {
            return;
        }
        plugin.getServer().getServicesManager().unregister(Economy.class, taxedEconomy);
        registered = false;
        plugin.getLogger().info("EzTax unregistered its Vault economy wrapper.");
    }
}
