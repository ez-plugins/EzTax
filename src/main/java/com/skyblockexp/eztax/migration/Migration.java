package com.skyblockexp.eztax.migration;

import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;

/**
 * A single, versioned database migration.
 *
 * <p>Each implementation carries a monotonically increasing {@link #version()}
 * number. {@link MigrationRunner} records applied versions in a dedicated
 * tracking table and only calls {@link #up} for migrations not yet applied.
 */
public interface Migration {

    /** Monotonically increasing migration version. Must be unique. */
    int version();

    /** Human-readable description stored alongside the version in the DB. */
    String description();

    /**
     * Apply this migration to the given store.
     *
     * @param store the JDBC store to execute DDL/DML against
     * @throws Exception on any failure (causes the migration run to abort)
     */
    void up(DataSourceJdbcStore store) throws Exception;
}
