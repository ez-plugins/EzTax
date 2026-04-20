package com.skyblockexp.eztax.storage;

import com.github.ezframework.jaloquent.model.Model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Jaloquent {@link Model} for a single tax-collection event.
 *
 * <p>Each successful tax withdrawal produces one row. The {@code id} column
 * is a random UUID generated at record time.
 */
public class TaxHistoryModel extends Model {

    /** Column definitions used when registering the SQL table. */
    public static final Map<String, String> COLUMN_DEFS;

    static {
        COLUMN_DEFS = new LinkedHashMap<>();
        COLUMN_DEFS.put("id",             "VARCHAR(36) NOT NULL");
        COLUMN_DEFS.put("player_uuid",    "VARCHAR(36) NOT NULL");
        COLUMN_DEFS.put("player_name",    "VARCHAR(64)");
        COLUMN_DEFS.put("sink",           "VARCHAR(32) NOT NULL");
        COLUMN_DEFS.put("amount",         "DOUBLE NOT NULL");
        COLUMN_DEFS.put("balance_before", "DOUBLE NOT NULL");
        COLUMN_DEFS.put("timestamp_ms",   "BIGINT NOT NULL");
    }

    public TaxHistoryModel(String id) {
        super(id);
    }

    // -------------------------------------------------------------------------
    // Getters / setters
    // -------------------------------------------------------------------------

    public String getPlayerUuid()              { return getAs("player_uuid",    String.class, null); }
    public void   setPlayerUuid(String v)      { set("player_uuid",    v); }

    public String getPlayerName()              { return getAs("player_name",    String.class, null); }
    public void   setPlayerName(String v)      { set("player_name",    v); }

    public String getSink()                    { return getAs("sink",           String.class, null); }
    public void   setSink(String v)            { set("sink",           v); }

    public double getAmount()                  { return getAs("amount",         Double.class, 0.0); }
    public void   setAmount(double v)          { set("amount",         v); }

    public double getBalanceBefore()           { return getAs("balance_before", Double.class, 0.0); }
    public void   setBalanceBefore(double v)   { set("balance_before", v); }

    public long   getTimestampMs()             { return getAs("timestamp_ms",   Long.class, 0L); }
    public void   setTimestampMs(long v)       { set("timestamp_ms",   v); }

    // -------------------------------------------------------------------------
    // DDL helper
    // -------------------------------------------------------------------------

    /**
     * Generates a {@code CREATE TABLE IF NOT EXISTS} statement with an index
     * on {@code player_uuid} for efficient per-player lookups.
     */
    public static String buildCreateTableDdl(String tableName) {
        StringJoiner cols = new StringJoiner(", ");
        for (Map.Entry<String, String> col : COLUMN_DEFS.entrySet()) {
            cols.add("`" + col.getKey() + "` " + col.getValue());
        }
        cols.add("PRIMARY KEY (`id`)");
        cols.add("INDEX `idx_history_player` (`player_uuid`)");
        cols.add("INDEX `idx_history_sink` (`sink`)");
        cols.add("INDEX `idx_history_ts` (`timestamp_ms`)");
        return "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + cols + ")";
    }
}
