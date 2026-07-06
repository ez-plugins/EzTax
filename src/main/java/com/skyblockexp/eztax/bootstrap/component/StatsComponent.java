package com.skyblockexp.eztax.bootstrap.component;

import com.skyblockexp.eztax.bootstrap.Component;
import com.skyblockexp.eztax.EzTaxPlugin;
import com.github.ezframework.jaloquent.model.TableRegistry;
import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.skyblockexp.eztax.migration.Migration001_CreateStatsTable;
import com.skyblockexp.eztax.migration.Migration002_CreateTaxHistoryTable;
import com.skyblockexp.eztax.migration.Migration003_CreateTrackedPlayersTable;
import com.skyblockexp.eztax.migration.MigrationRunner;
import com.skyblockexp.eztax.repository.JaloquentStatsRepository;
import com.skyblockexp.eztax.repository.JaloquentTaxHistoryRepository;
import com.skyblockexp.eztax.repository.JaloquentTrackedPlayerRepository;
import com.skyblockexp.eztax.repository.TaxHistoryRepository;
import com.skyblockexp.eztax.repository.TrackedPlayerRepository;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.storage.BukkitYamlDataStore;
import com.skyblockexp.eztax.storage.TaxHistoryModel;
import com.skyblockexp.eztax.storage.TrackedPlayerModel;
import com.skyblockexp.eztax.storage.StatsModel;
import com.skyblockexp.eztax.config.TaxConfig;
import com.github.ezframework.jaloquent.config.JaloquentConfig;

public class StatsComponent implements Component {
    private final EzTaxPlugin plugin;
    private final TaxConfig taxConfig;
    private StatsService statsService;
    private TaxHistoryRepository taxHistoryRepository;
    private TrackedPlayerRepository trackedPlayerRepository;

    public StatsComponent(EzTaxPlugin plugin, TaxConfig taxConfig) {
        this.plugin = plugin;
        this.taxConfig = taxConfig;
    }

    @Override
    public void start() {
        // Suppress verbose INFO logs from Jaloquent internals using Jaloquent's own API.
        JaloquentConfig.enableLogging(false);

        String storage = taxConfig.getStorageType();
        java.io.File statsFile = new java.io.File(plugin.getDataFolder(), taxConfig.getStorageFile());
        com.skyblockexp.eztax.repository.StatsRepository repository = null;

        if ("mysql".equalsIgnoreCase(storage)) {
            String host = taxConfig.getMysqlHost();
            int    port = taxConfig.getMysqlPort();
            String db   = taxConfig.getMysqlDatabase();
            String user = taxConfig.getMysqlUser();
            String pass = taxConfig.getMysqlPassword();
            String table = taxConfig.getMysqlTable();

            if (host == null || host.isEmpty() || db == null || db.isEmpty()) {
                plugin.getLogger().warning("MySQL storage configured but host/database are not set — falling back to YAML storage.");
            } else {
                try {
                    String url = String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC&useSSL=false", host, port, db);
                    com.skyblockexp.eztax.storage.DriverManagerDataSource ds =
                            new com.skyblockexp.eztax.storage.DriverManagerDataSource(url, user, pass);
                    try (java.sql.Connection conn = ds.getConnection()) {
                        try { if (!conn.isValid(2)) throw new java.sql.SQLException("isValid returned false"); }
                        catch (Throwable ignored) { }
                    }
                    DataSourceJdbcStore jdbcStore = new DataSourceJdbcStore(ds);

                    String historyTable = table + "_history";
                    String playersTable = table + "_players";

                    // Register tables with Jaloquent's TableRegistry
                    TableRegistry.register("eztax_stats",   table,        StatsModel.COLUMN_DEFS);
                    TableRegistry.register("eztax_history", historyTable, TaxHistoryModel.COLUMN_DEFS);
                    TableRegistry.register("eztax_players", playersTable, TrackedPlayerModel.COLUMN_DEFS);

                    // Run versioned migrations (idempotent — already-applied versions are skipped)
                    MigrationRunner.run(jdbcStore, "eztax", java.util.List.of(
                            new Migration001_CreateStatsTable(table),
                            new Migration002_CreateTaxHistoryTable(historyTable),
                            new Migration003_CreateTrackedPlayersTable(playersTable)
                    ), plugin.getLogger());

                    repository = new JaloquentStatsRepository(jdbcStore, "eztax_stats", SqlDialect.MYSQL);
                    taxHistoryRepository    = new JaloquentTaxHistoryRepository(jdbcStore,    "eztax_history", SqlDialect.MYSQL);
                    trackedPlayerRepository = new JaloquentTrackedPlayerRepository(jdbcStore, "eztax_players", SqlDialect.MYSQL);

                    plugin.getLogger().info("Storage: MySQL (table: " + table + ")");
                } catch (java.sql.SQLException ex) {
                    plugin.getLogger().log(java.util.logging.Level.WARNING,
                            "MySQL storage configured but connection test failed: " + ex.getMessage()
                                    + ". Falling back to YAML storage.", ex);
                } catch (Exception ex) {
                    plugin.getLogger().log(java.util.logging.Level.WARNING,
                            "Failed to initialise MySQL storage, falling back to YAML.", ex);
                }
            }
        }

        if (repository == null) {
            BukkitYamlDataStore yamlStore = new BukkitYamlDataStore(statsFile, plugin.getLogger());
            repository = new JaloquentStatsRepository(yamlStore, "eztax_stats");
            plugin.getLogger().info("Storage: YAML (" + statsFile.getName() + ")");
            // History and player tracking require MySQL; unavailable on YAML path
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

    /** Non-null only when MySQL storage is active. */
    public TaxHistoryRepository getTaxHistoryRepository() { return taxHistoryRepository; }

    /** Non-null only when MySQL storage is active. */
    public TrackedPlayerRepository getTrackedPlayerRepository() { return trackedPlayerRepository; }
}
