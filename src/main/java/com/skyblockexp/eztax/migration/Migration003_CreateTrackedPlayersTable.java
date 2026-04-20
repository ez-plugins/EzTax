package com.skyblockexp.eztax.migration;

import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;
import com.skyblockexp.eztax.storage.TrackedPlayerModel;

import java.util.List;

/**
 * Creates the tracked players table.
 */
public class Migration003_CreateTrackedPlayersTable implements Migration {

    private final String tableName;

    public Migration003_CreateTrackedPlayersTable(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public int version() { return 3; }

    @Override
    public String description() { return "Create tracked players table"; }

    @Override
    public void up(DataSourceJdbcStore store) throws Exception {
        store.executeUpdate(TrackedPlayerModel.buildCreateTableDdl(tableName), List.of());
    }
}
