package com.skyblockexp.eztax.migration;

import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;
import com.skyblockexp.eztax.storage.TaxHistoryModel;

import java.util.List;

/**
 * Creates the per-event tax history table.
 */
public class Migration002_CreateTaxHistoryTable implements Migration {

    private final String tableName;

    public Migration002_CreateTaxHistoryTable(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public int version() { return 2; }

    @Override
    public String description() { return "Create tax history table"; }

    @Override
    public void up(DataSourceJdbcStore store) throws Exception {
        store.executeUpdate(TaxHistoryModel.buildCreateTableDdl(tableName), List.of());
    }
}
