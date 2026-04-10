package com.skyblockexp.eztax.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;

/**
 * YML-backed StorageProvider. Uses Bukkit's YamlConfiguration and a File.
 */
public class YamlStorageProvider implements StorageProvider {
    private final File file;

    public YamlStorageProvider(File file) {
        this.file = file;
    }

    @Override
    public StatsData load() throws IOException {
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        StatsData d = new StatsData();
        d.setTotalRemoved(cfg.getDouble("totals.overall", 0.0D));
        d.setDailyRemoved(cfg.getDouble("totals.daily", 0.0D));
        d.setWeeklyRemoved(cfg.getDouble("totals.weekly", 0.0D));
        d.setTreasuryBalance(cfg.getDouble("totals.treasury", 0.0D));
        for (com.skyblockexp.eztax.service.TaxSink sink : com.skyblockexp.eztax.service.TaxSink.values()) {
            d.getSinkTotals().put(sink, cfg.getDouble("totals.sinks." + sink.name(), 0.0D));
        }
        String daily = cfg.getString("resets.daily");
        String weekly = cfg.getString("resets.weekly");
        try {
            d.setDailyResetDate(daily == null || daily.isEmpty() ? null : LocalDate.parse(daily));
        } catch (Exception ignored) {
            d.setDailyResetDate(null);
        }
        try {
            d.setWeeklyResetDate(weekly == null || weekly.isEmpty() ? null : LocalDate.parse(weekly));
        } catch (Exception ignored) {
            d.setWeeklyResetDate(null);
        }
        return d;
    }

    @Override
    public void save(StatsData data) throws IOException {
        FileConfiguration cfg = new YamlConfiguration();
        cfg.set("totals.overall", data.getTotalRemoved());
        cfg.set("totals.daily", data.getDailyRemoved());
        cfg.set("totals.weekly", data.getWeeklyRemoved());
        cfg.set("totals.treasury", data.getTreasuryBalance());
        for (java.util.Map.Entry<com.skyblockexp.eztax.service.TaxSink, Double> e : data.getSinkTotals().entrySet()) {
            cfg.set("totals.sinks." + e.getKey().name(), e.getValue());
        }
        cfg.set("resets.daily", data.getDailyResetDate() != null ? data.getDailyResetDate().toString() : null);
        cfg.set("resets.weekly", data.getWeeklyResetDate() != null ? data.getWeeklyResetDate().toString() : null);
        try {
            cfg.save(file);
        } catch (IOException ex) {
            throw ex;
        }
    }
}
