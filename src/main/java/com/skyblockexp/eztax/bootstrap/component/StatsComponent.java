package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.config.TaxConfig;

public class StatsComponent implements Component {
    private final EzTaxPlugin plugin;
    private final TaxConfig taxConfig;
    private StatsService statsService;

    public StatsComponent(EzTaxPlugin plugin, TaxConfig taxConfig) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
    }

    @Override
    public void start() {
        // Construct storage-backed repository based on config
        String storage = taxConfig.getStorageType();
        com.skyblockexp.eztax.repository.StatsRepository repository = null;
        java.io.File statsFile = new java.io.File(plugin.getDataFolder(), taxConfig.getStorageFile());
        if ("mysql".equalsIgnoreCase(storage)) {
            // attempt to create a simple DataSource from config; if it fails/credentials empty fall back to YML
            String host = taxConfig.getMysqlHost();
            int port = taxConfig.getMysqlPort();
            String db = taxConfig.getMysqlDatabase();
            String user = taxConfig.getMysqlUser();
            String pass = taxConfig.getMysqlPassword();
            String table = taxConfig.getMysqlTable();
            if (host == null || host.isEmpty() || db == null || db.isEmpty()) {
                plugin.getLogger().warning("MySQL storage configured but host/database are not set — falling back to YML storage.");
            } else {
                try {
                    String url = String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC&useSSL=false", host, port, db);
                    com.skyblockexp.eztax.storage.DriverManagerDataSource ds = new com.skyblockexp.eztax.storage.DriverManagerDataSource(url, user, pass);
                    // validate connection early so failures are surfaced during startup
                    try (java.sql.Connection conn = ds.getConnection()) {
                        boolean valid = false;
                        try {
                            valid = conn.isValid(2);
                        } catch (Throwable ignored) {
                        }
                        if (!valid) {
                            throw new java.sql.SQLException("Unable to validate MySQL connection (isValid returned false)");
                        }
                    }
                    com.skyblockexp.eztax.storage.MySqlStorageProvider mysqlProvider = new com.skyblockexp.eztax.storage.MySqlStorageProvider(ds, table);
                    repository = new com.skyblockexp.eztax.repository.ProviderStatsRepository(mysqlProvider);
                    plugin.getLogger().info("Using MySQL storage for stats (table=" + table + ").");
                } catch (java.sql.SQLException ex) {
                    plugin.getLogger().log(java.util.logging.Level.WARNING, "MySQL storage configured but connection test failed: " + ex.getMessage() + ". Ensure the MySQL JDBC driver is available and credentials/host are correct. Falling back to YML storage.", ex);
                } catch (Exception ex) {
                    plugin.getLogger().log(java.util.logging.Level.WARNING, "Failed to initialize MySQL storage, falling back to YML", ex);
                }
            }
        }

        if (repository == null) {
            // default to YML provider
            repository = new com.skyblockexp.eztax.repository.ProviderStatsRepository(new com.skyblockexp.eztax.storage.YamlStorageProvider(statsFile));
            plugin.getLogger().info("Using YML storage for stats: " + statsFile.getName());
        }

        this.statsService = new com.skyblockexp.eztax.service.StatsService(plugin, taxConfig, repository, statsFile);
    }

    @Override
    public void stop() {
        if (statsService != null) statsService.save();
    }

    @Override
    public void reload() {
        // nothing
    }

    public StatsService getStatsService() { return statsService; }
}
