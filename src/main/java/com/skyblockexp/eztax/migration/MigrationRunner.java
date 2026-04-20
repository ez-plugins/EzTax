package com.skyblockexp.eztax.migration;

import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Lightweight migration runner that tracks applied migrations in a dedicated
 * SQL table ({@code <prefix>_migrations}).
 *
 * <p>Jaloquent has no built-in migration support, so this class implements
 * the pattern from scratch using {@link DataSourceJdbcStore#executeUpdate} and
 * {@link DataSourceJdbcStore#query} directly.
 *
 * <p>Algorithm:
 * <ol>
 *   <li>Create the tracking table if it does not exist.</li>
 *   <li>Load the set of already-applied version numbers.</li>
 *   <li>Sort the supplied migrations by version.</li>
 *   <li>For each pending migration: call {@link Migration#up}, then record it.</li>
 * </ol>
 */
public class MigrationRunner {

    private MigrationRunner() {}

    /**
     * Run all pending migrations.
     *
     * @param store      JDBC store to execute against
     * @param tablePrefix prefix used to derive the tracking table name
     *                    (result: {@code <tablePrefix>_migrations})
     * @param migrations  full list of known migrations (may be in any order)
     * @param logger      logger for progress messages (may be null)
     * @throws Exception  if any migration fails; already-applied migrations are
     *                    left intact
     */
    public static void run(DataSourceJdbcStore store,
                           String tablePrefix,
                           List<Migration> migrations,
                           Logger logger) throws Exception {

        String migsTable = tablePrefix + "_migrations";

        // Bootstrap: ensure the tracking table itself exists
        String createMigsTable =
                "CREATE TABLE IF NOT EXISTS `" + migsTable + "` (" +
                        "`version` INT NOT NULL, " +
                        "`description` VARCHAR(255), " +
                        "`applied_at` BIGINT NOT NULL, " +
                        "PRIMARY KEY (`version`))";
        store.executeUpdate(createMigsTable, List.of());

        // Load already-applied versions
        List<Map<String, Object>> rows = store.query(
                "SELECT `version` FROM `" + migsTable + "`", List.of());
        Set<Integer> applied = new HashSet<>();
        for (Map<String, Object> row : rows) {
            Object v = row.get("version");
            if (v instanceof Number) {
                applied.add(((Number) v).intValue());
            }
        }

        // Sort and apply pending migrations
        List<Migration> sorted = new ArrayList<>(migrations);
        sorted.sort(Comparator.comparingInt(Migration::version));

        for (Migration m : sorted) {
            if (applied.contains(m.version())) {
                continue;
            }
            if (logger != null) {
                logger.info(String.format(
                        "[EzTax] Applying migration v%d: %s", m.version(), m.description()));
            }
            m.up(store);
            store.executeUpdate(
                    "INSERT INTO `" + migsTable + "` (`version`, `description`, `applied_at`) VALUES (?, ?, ?)",
                    List.of(m.version(), m.description(), System.currentTimeMillis()));
            if (logger != null) {
                logger.info(String.format(
                        "[EzTax] Migration v%d applied successfully.", m.version()));
            }
        }
    }
}
