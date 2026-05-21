package com.skyblockexp.eztax.service;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.TaxInterval;
import com.skyblockexp.eztax.scheduler.PluginScheduler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SchedulerService {
    private final JavaPlugin plugin;
    private final TaxConfig config;
    private final TaxEngine taxEngine;
    private final PluginScheduler scheduler;

    private Runnable wealthTaskCancel;
    private Runnable inactivityTaskCancel;

    public SchedulerService(JavaPlugin plugin, TaxConfig config, TaxEngine taxEngine) {
        this.plugin = plugin;
        this.config = config;
        this.taxEngine = taxEngine;
        this.scheduler = new PluginScheduler(plugin);
    }

    public void start() {
        scheduleWealthTax();
        scheduleInactivityFee();
    }

    public void stop() {
        cancelTask(wealthTaskCancel);
        wealthTaskCancel = null;
        cancelTask(inactivityTaskCancel);
        inactivityTaskCancel = null;
    }

    public void reload() {
        stop();
        start();
    }

    private void scheduleWealthTax() {
        if (!config.isWealthTaxEnabled()) {
            return;
        }
        TaxInterval interval = config.getWealthTaxInterval();
        wealthTaskCancel = scheduler.runAsyncTimer(() -> {
            try {
                runWealthTaxAsync();
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Wealth tax run failed; skipping this cycle.", ex);
            }
        }, interval.getTicks(), interval.getTicks());
    }

    private void scheduleInactivityFee() {
        if (!config.isInactivityFeeEnabled()) {
            return;
        }
        long dailyTicks = TaxInterval.DAILY.getTicks();
        inactivityTaskCancel = scheduler.runAsyncTimer(() -> {
            try {
                runInactivityFeeAsync();
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Inactivity fee run failed; skipping this cycle.", ex);
            }
        }, dailyTicks, dailyTicks);
    }

    private void runWealthTaxAsync() {
        List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
        for (OfflinePlayer player : players) {
            if (player == null || !player.hasPlayedBefore()) {
                continue;
            }
            UUID uuid = player.getUniqueId();
            scheduler.runSync(() -> {
                OfflinePlayer syncPlayer = Bukkit.getOfflinePlayer(uuid);
                taxEngine.applyWealthTax(syncPlayer);
            });
        }
    }

    private void runInactivityFeeAsync() {
        List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
        int inactiveDays = config.getInactivityDays();
        Instant now = Instant.now();
        for (OfflinePlayer player : players) {
            if (player == null || !player.hasPlayedBefore()) {
                continue;
            }
            long lastPlayed = player.getLastPlayed();
            if (lastPlayed <= 0) {
                continue;
            }
            long days = Duration.between(Instant.ofEpochMilli(lastPlayed), now).toDays();
            if (days < inactiveDays) {
                continue;
            }
            UUID uuid = player.getUniqueId();
            scheduler.runSync(() -> {
                OfflinePlayer syncPlayer = Bukkit.getOfflinePlayer(uuid);
                taxEngine.applyInactivityFee(syncPlayer);
            });
        }
    }

    private void cancelTask(Runnable cancel) {
        if (cancel != null) {
            cancel.run();
        }
    }
}
