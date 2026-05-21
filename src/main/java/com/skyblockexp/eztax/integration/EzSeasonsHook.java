package com.skyblockexp.eztax.integration;

import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.lifesteal.seasons.api.SeasonsApi;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

/**
 * Hooks into EzSeasons at startup and registers an {@link EzSeasonsIntegration}
 * so EzTax receives season-reset callbacks.
 *
 * <p>Uses reflection to call {@code EzSeasonsPlugin#getSeasonsApi()} to avoid
 * a compile-time dependency on the EzSeasons plugin module; only the
 * {@code ezseasons-api} jar is required at compile time.
 */
public class EzSeasonsHook {

    private final JavaPlugin plugin;
    private final StatsService statsService;
    private EzSeasonsIntegration integration;
    private SeasonsApi api;

    public EzSeasonsHook(JavaPlugin plugin, StatsService statsService) {
        this.plugin = plugin;
        this.statsService = statsService;
    }

    public boolean hook() {
        Plugin ezSeasons = plugin.getServer().getPluginManager().getPlugin("EzSeasons");
        if (ezSeasons == null || !ezSeasons.isEnabled()) {
            return false;
        }

        try {
            Method getSeasonsApi = ezSeasons.getClass().getMethod("getSeasonsApi");
            Object apiObj = getSeasonsApi.invoke(ezSeasons);
            if (!(apiObj instanceof SeasonsApi)) {
                plugin.getLogger().warning("EzSeasons is present but getSeasonsApi() returned an unexpected type — integration disabled.");
                return false;
            }
            api = (SeasonsApi) apiObj;
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("Could not access EzSeasons API: " + e.getMessage());
            return false;
        }

        integration = new EzSeasonsIntegration(statsService, plugin.getLogger());
        api.registerIntegration(integration);
        plugin.getLogger().info("EzSeasons integration enabled (v" + ezSeasons.getDescription().getVersion() + ").");
        return true;
    }

    public void unhook() {
        if (integration == null || api == null) return;
        try {
            api.unregisterIntegration(integration);
        } catch (Exception e) {
            plugin.getLogger().warning("Error while unregistering EzSeasons integration: " + e.getMessage());
        }
        integration = null;
        api = null;
    }

    public boolean isHooked() {
        return integration != null && api != null;
    }
}
