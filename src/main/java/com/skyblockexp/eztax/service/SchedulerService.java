package com.skyblockexp.eztax.service;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.config.TaxInterval;
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

    private int wealthTaskId = -1;
    private int inactivityTaskId = -1;

    public SchedulerService(JavaPlugin plugin, TaxConfig config, TaxEngine taxEngine) {
        this.plugin = plugin;
        this.config = config;
        this.taxEngine = taxEngine;
    }

    public void start() {
        scheduleWealthTax();
        scheduleInactivityFee();
    }

    public void stop() {
        cancelTask(wealthTaskId);
        cancelTask(inactivityTaskId);
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
        wealthTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                runWealthTaxAsync();
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Wealth tax run failed; skipping this cycle.", ex);
            }
        }, interval.getTicks(), interval.getTicks()).getTaskId();
    }

    private void scheduleInactivityFee() {
        if (!config.isInactivityFeeEnabled()) {
            return;
        }
        long dailyTicks = TaxInterval.DAILY.getTicks();
        inactivityTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                runInactivityFeeAsync();
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Inactivity fee run failed; skipping this cycle.", ex);
            }
        }, dailyTicks, dailyTicks).getTaskId();
    }

    private void runWealthTaxAsync() {
        List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
        for (OfflinePlayer player : players) {
            if (player == null || !player.hasPlayedBefore()) {
                continue;
            }
            UUID uuid = player.getUniqueId();
            Bukkit.getScheduler().runTask(plugin, () -> {
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
            Bukkit.getScheduler().runTask(plugin, () -> {
                OfflinePlayer syncPlayer = Bukkit.getOfflinePlayer(uuid);
                taxEngine.applyInactivityFee(syncPlayer);
            });
        }
    }

    private void cancelTask(int taskId) {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
