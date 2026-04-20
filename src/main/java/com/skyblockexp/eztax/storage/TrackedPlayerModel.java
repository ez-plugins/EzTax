package com.skyblockexp.eztax.storage;

import com.github.ezframework.jaloquent.model.Model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Jaloquent {@link Model} that tracks every player the tax system has ever
 * touched.
 *
 * <p>The {@code id} column stores the player's UUID string so that the row
 * survives username changes. {@code total_taxed} is accumulated over all
 * time; use the {@code eztax_*_history} table for a full event log.
 */
public class TrackedPlayerModel extends Model {

    /** Column definitions used when registering the SQL table. */
    public static final Map<String, String> COLUMN_DEFS;

    static {
        COLUMN_DEFS = new LinkedHashMap<>();
        COLUMN_DEFS.put("id",             "VARCHAR(36) NOT NULL");  // player UUID
        COLUMN_DEFS.put("name",           "VARCHAR(64)");
        COLUMN_DEFS.put("first_seen_ms",  "BIGINT NOT NULL");
        COLUMN_DEFS.put("last_seen_ms",   "BIGINT NOT NULL");
        COLUMN_DEFS.put("total_taxed",    "DOUBLE NOT NULL DEFAULT 0");
    }

    public TrackedPlayerModel(String id) {
        super(id);
    }

    // -------------------------------------------------------------------------
    // Getters / setters
    // -------------------------------------------------------------------------

    public String getName()                  { return getAs("name",           String.class, null); }
    public void   setName(String v)          { set("name",           v); }

    public long   getFirstSeenMs()           { return getAs("first_seen_ms",  Long.class, 0L); }
    public void   setFirstSeenMs(long v)     { set("first_seen_ms",  v); }

    public long   getLastSeenMs()            { return getAs("last_seen_ms",   Long.class, 0L); }
    public void   setLastSeenMs(long v)      { set("last_seen_ms",   v); }

    public double getTotalTaxed()            { return getAs("total_taxed",    Double.class, 0.0); }
    public void   setTotalTaxed(double v)    { set("total_taxed",    v); }

    // -------------------------------------------------------------------------
    // DDL helper
    // -------------------------------------------------------------------------

    public static String buildCreateTableDdl(String tableName) {
        StringJoiner cols = new StringJoiner(", ");
        for (Map.Entry<String, String> col : COLUMN_DEFS.entrySet()) {
            cols.add("`" + col.getKey() + "` " + col.getValue());
        }
        cols.add("PRIMARY KEY (`id`)");
        return "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + cols + ")";
    }
}
