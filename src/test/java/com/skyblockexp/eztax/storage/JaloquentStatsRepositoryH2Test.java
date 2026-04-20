package com.skyblockexp.eztax.storage;

import com.github.ezframework.jaloquent.model.TableRegistry;
import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;
import com.skyblockexp.eztax.repository.JaloquentStatsRepository;
import com.skyblockexp.eztax.service.TaxSink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for {@link JaloquentStatsRepository} backed by an H2 in-memory
 * database in MySQL-compatibility mode.
 *
 * <p>Follows the same pattern as Jaloquent's own
 * {@code DataSourceJdbcStoreFeatureTest}: each test run uses a UUID-suffixed
 * prefix and table name so that the JVM-static {@link TableRegistry} never
 * accumulates stale cross-test state.
 */
public class JaloquentStatsRepositoryH2Test {

    private static final String JDBC_URL =
            "jdbc:h2:mem:eztax_jaloquent_test;MODE=MySQL;DB_CLOSE_DELAY=-1";

    private JaloquentStatsRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        String uid   = UUID.randomUUID().toString().replace("-", "");
        String prefix = "eztax_stats_" + uid;
        String table  = "eztax_stats_" + uid;

        DriverManagerDataSource ds = new DriverManagerDataSource(JDBC_URL, "sa", "");
        DataSourceJdbcStore store = new DataSourceJdbcStore(ds);

        // Register schema with Jaloquent's TableRegistry
        TableRegistry.register(prefix, table, StatsModel.COLUMN_DEFS);

        // DDL — Jaloquent does not auto-create tables
        StringBuilder ddl = new StringBuilder("CREATE TABLE IF NOT EXISTS `")
                .append(table).append("` (");
        for (var col : StatsModel.COLUMN_DEFS.entrySet()) {
            ddl.append("`").append(col.getKey()).append("` ").append(col.getValue()).append(", ");
        }
        ddl.append("PRIMARY KEY (`id`))");
        store.executeUpdate(ddl.toString(), java.util.List.of());

        repository = new JaloquentStatsRepository(store, prefix);
    }

    @Test
    void loadReturnsDefaultsWhenNoRowExists() throws Exception {
        StatsData data = repository.load();
        assertEquals(0.0, data.getTotalRemoved(), 1e-9);
        assertEquals(0.0, data.getDailyRemoved(), 1e-9);
        assertEquals(0.0, data.getWeeklyRemoved(), 1e-9);
        assertEquals(0.0, data.getTreasuryBalance(), 1e-9);
    }

    @Test
    void saveAndLoadRoundTrip() throws Exception {
        StatsData original = new StatsData();
        original.setTotalRemoved(999.99);
        original.setDailyRemoved(100.0);
        original.setWeeklyRemoved(500.0);
        original.setTreasuryBalance(1234.56);
        original.getSinkTotals().put(TaxSink.TRANSACTION, 10.0);
        original.getSinkTotals().put(TaxSink.WEALTH, 20.0);
        original.getSinkTotals().put(TaxSink.INACTIVITY, 30.0);
        original.getSinkTotals().put(TaxSink.DEATH, 40.0);

        repository.save(original);
        StatsData loaded = repository.load();

        assertEquals(999.99, loaded.getTotalRemoved(), 1e-9);
        assertEquals(100.0,  loaded.getDailyRemoved(),   1e-9);
        assertEquals(500.0,  loaded.getWeeklyRemoved(),  1e-9);
        assertEquals(1234.56, loaded.getTreasuryBalance(), 1e-9);
        assertEquals(10.0, loaded.getSinkTotals().getOrDefault(TaxSink.TRANSACTION, 0.0), 1e-9);
        assertEquals(20.0, loaded.getSinkTotals().getOrDefault(TaxSink.WEALTH,      0.0), 1e-9);
        assertEquals(30.0, loaded.getSinkTotals().getOrDefault(TaxSink.INACTIVITY,  0.0), 1e-9);
        assertEquals(40.0, loaded.getSinkTotals().getOrDefault(TaxSink.DEATH,       0.0), 1e-9);
    }

    @Test
    void upsertOverwritesPreviousRow() throws Exception {
        StatsData first = new StatsData();
        first.setTotalRemoved(100.0);
        repository.save(first);

        StatsData second = new StatsData();
        second.setTotalRemoved(200.0);
        repository.save(second);

        StatsData loaded = repository.load();
        assertEquals(200.0, loaded.getTotalRemoved(), 1e-9,
                "Second save should overwrite via upsert");
    }
}
