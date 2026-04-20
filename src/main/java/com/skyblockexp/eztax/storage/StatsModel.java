package com.skyblockexp.eztax.storage;

import com.github.ezframework.jaloquent.model.Model;
import com.skyblockexp.eztax.service.TaxSink;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Jaloquent {@link Model} that persists aggregated tax statistics.
 *
 * <p>There is always exactly one record, stored with ID {@value #MODEL_ID}.
 * Numeric attributes use {@code Double}; date attributes are stored as ISO strings.
 */
public class StatsModel extends Model {

    public static final String MODEL_ID = "stats";

    /** Column definitions used when registering the SQL table. */
    public static final Map<String, String> COLUMN_DEFS;

    static {
        COLUMN_DEFS = new LinkedHashMap<>();
        COLUMN_DEFS.put("id",                "VARCHAR(36) NOT NULL");
        COLUMN_DEFS.put("total_removed",     "DOUBLE NOT NULL DEFAULT 0");
        COLUMN_DEFS.put("daily_removed",     "DOUBLE NOT NULL DEFAULT 0");
        COLUMN_DEFS.put("weekly_removed",    "DOUBLE NOT NULL DEFAULT 0");
        COLUMN_DEFS.put("treasury_balance",  "DOUBLE NOT NULL DEFAULT 0");
        COLUMN_DEFS.put("daily_reset_date",  "VARCHAR(10)");
        COLUMN_DEFS.put("weekly_reset_date", "VARCHAR(10)");
        COLUMN_DEFS.put("sink_transaction",  "DOUBLE NOT NULL DEFAULT 0");
        COLUMN_DEFS.put("sink_wealth",       "DOUBLE NOT NULL DEFAULT 0");
        COLUMN_DEFS.put("sink_inactivity",   "DOUBLE NOT NULL DEFAULT 0");
        COLUMN_DEFS.put("sink_death",        "DOUBLE NOT NULL DEFAULT 0");
    }

    public StatsModel(String id) {
        super(id);
    }

    // -------------------------------------------------------------------------
    // Convenience getters / setters
    // -------------------------------------------------------------------------

    public double getTotalRemoved()          { return getAs("total_removed",    Double.class, 0.0); }
    public void   setTotalRemoved(double v)  { set("total_removed",    v); }

    public double getDailyRemoved()          { return getAs("daily_removed",    Double.class, 0.0); }
    public void   setDailyRemoved(double v)  { set("daily_removed",    v); }

    public double getWeeklyRemoved()         { return getAs("weekly_removed",   Double.class, 0.0); }
    public void   setWeeklyRemoved(double v) { set("weekly_removed",   v); }

    public double getTreasuryBalance()          { return getAs("treasury_balance", Double.class, 0.0); }
    public void   setTreasuryBalance(double v)  { set("treasury_balance",  v); }

    public String getDailyResetDate()         { return getAs("daily_reset_date",  String.class, null); }
    public void   setDailyResetDate(String v) { set("daily_reset_date",  v); }

    public String getWeeklyResetDate()         { return getAs("weekly_reset_date", String.class, null); }
    public void   setWeeklyResetDate(String v) { set("weekly_reset_date", v); }

    /**
     * Return the accumulated total for the given {@link TaxSink}.
     * The attribute key is {@code sink_<sink_name_lowercase>} (e.g. {@code sink_wealth}).
     */
    public double getSinkTotal(TaxSink sink) {
        return getAs("sink_" + sink.name().toLowerCase(), Double.class, 0.0);
    }

    public void setSinkTotal(TaxSink sink, double v) {
        set("sink_" + sink.name().toLowerCase(), v);
    }

    /**
     * Generate a {@code CREATE TABLE IF NOT EXISTS} DDL statement derived from
     * {@link #COLUMN_DEFS}, with {@code id} as the explicit {@code PRIMARY KEY}.
     *
     * <p>Using backtick-quoted identifiers so the statement is safe against
     * reserved-word conflicts on MySQL.
     *
     * @param tableName SQL table name
     * @return ready-to-execute DDL string
     */
    public static String buildCreateTableDdl(String tableName) {
        StringJoiner cols = new StringJoiner(", ");
        for (Map.Entry<String, String> col : COLUMN_DEFS.entrySet()) {
            cols.add("`" + col.getKey() + "` " + col.getValue());
        }
        cols.add("PRIMARY KEY (`id`)");
        return "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + cols + ")";
    }
}
