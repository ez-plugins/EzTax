package com.skyblockexp.eztax.migration;

import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;
import com.skyblockexp.eztax.storage.StatsModel;

import java.util.List;

/**
 * Creates the aggregated stats table.
 */
public class Migration001_CreateStatsTable implements Migration {

    private final String tableName;

    public Migration001_CreateStatsTable(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public int version() { return 1; }

    @Override
    public String description() { return "Create stats table"; }

    @Override
    public void up(DataSourceJdbcStore store) throws Exception {
        store.executeUpdate(StatsModel.buildCreateTableDdl(tableName), List.of());
    }
}
