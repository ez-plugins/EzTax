package com.skyblockexp.eztax.service;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.repository.JaloquentStatsRepository;
import com.skyblockexp.eztax.repository.StatsRepository;
import com.skyblockexp.eztax.storage.BukkitYamlDataStore;
import com.skyblockexp.eztax.storage.StatsData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Service that records tax statistics. Persistence is handled via a StatsRepository
 * (which in turn delegates to a StorageProvider). Default behaviour remains YML-backed
 * (same `stats.yml` file) so existing tests and configuration are unchanged.
 */
public class StatsService {
    private final JavaPlugin plugin;
    private final TaxConfig config;
    private final File statsFile;

    private final StatsRepository repository;

    private final Map<TaxSink, Double> sinkTotals = new EnumMap<>(TaxSink.class);
    private double totalRemoved;
    private double dailyRemoved;
    private double weeklyRemoved;
    private double treasuryBalance;
    private LocalDate dailyResetDate;
    private LocalDate weeklyResetDate;

    /**
     * Default constructor — YAML-backed via Jaloquent ({@link BukkitYamlDataStore}).
     */
    public StatsService(JavaPlugin plugin, TaxConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.statsFile = new File(plugin.getDataFolder(), "stats.yml");
        BukkitYamlDataStore store = new BukkitYamlDataStore(statsFile, plugin.getLogger());
        this.repository = new JaloquentStatsRepository(store, "eztax_stats");
        load();
    }

    /**
     * Constructor that allows injecting a custom StatsRepository (for MySQL, tests, etc.).
     */
    public StatsService(JavaPlugin plugin, TaxConfig config, StatsRepository repository, File statsFile) {
        this.plugin = plugin;
        this.config = config;
        this.statsFile = statsFile != null ? statsFile : new File(plugin.getDataFolder(), "stats.yml");
        this.repository = repository;
        load();
    }

    public void reload() {
        load();
    }

    public synchronized void recordTax(TaxSink sink, double amount) {
        recordTax(sink, amount, config.isTreasuryEnabled());
    }

    public synchronized void recordTax(TaxSink sink, double amount, boolean addToTreasury) {
        if (amount <= 0) {
            return;
        }
        plugin.getLogger().info("Recording tax: " + sink.name() + " amount=" + amount);
        ensureResets();
        totalRemoved += amount;
        dailyRemoved += amount;
        weeklyRemoved += amount;
        sinkTotals.put(sink, sinkTotals.getOrDefault(sink, 0.0D) + amount);
        if (addToTreasury) {
            treasuryBalance += amount;
        }
        save();
    }

    public synchronized double getTotalRemoved() {
        return totalRemoved;
    }

    public synchronized double getDailyRemoved() {
        ensureResets();
        return dailyRemoved;
    }

    public synchronized double getWeeklyRemoved() {
        ensureResets();
        return weeklyRemoved;
    }

    public synchronized double getTreasuryBalance() {
        return treasuryBalance;
    }

    public synchronized Map<TaxSink, Double> getSinkTotals() {
        return new EnumMap<>(sinkTotals);
    }

    /**
     * Persist the current in-memory stats via the repository.
     */
    public synchronized void save() {
        StatsData data = new StatsData();
        data.setTotalRemoved(totalRemoved);
        data.setDailyRemoved(dailyRemoved);
        data.setWeeklyRemoved(weeklyRemoved);
        data.setTreasuryBalance(treasuryBalance);
        data.setDailyResetDate(dailyResetDate);
        data.setWeeklyResetDate(weeklyResetDate);
        data.getSinkTotals().putAll(sinkTotals);
        try {
            if (repository != null) repository.save(data);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to save EzTax stats.", exception);
        }
    }

    private void load() {
        // load from repository (falls back to defaults)
        StatsData data = null;
        try {
            if (repository != null) {
                data = repository.load();
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to load EzTax stats.", ex);
        }
        if (data == null) {
            data = new StatsData();
        }

        totalRemoved = data.getTotalRemoved();
        dailyRemoved = data.getDailyRemoved();
        weeklyRemoved = data.getWeeklyRemoved();
        treasuryBalance = data.getTreasuryBalance();
        sinkTotals.clear();
        sinkTotals.putAll(data.getSinkTotals());
        dailyResetDate = data.getDailyResetDate();
        weeklyResetDate = data.getWeeklyResetDate();

        ensureResets();
        save();
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void ensureResets() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        if (dailyResetDate == null || !dailyResetDate.equals(today)) {
            dailyResetDate = today;
            dailyRemoved = 0.0D;
        }
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        if (weeklyResetDate == null || !weeklyResetDate.equals(weekStart)) {
            weeklyResetDate = weekStart;
            weeklyRemoved = 0.0D;
        }
    }
}
