package com.skyblockexp.eztax.storage;

import java.io.IOException;
import java.sql.*;
import javax.sql.DataSource;

/**
 * MySQL-backed StorageProvider. This is a minimal implementation that creates a single-row
 * stats table and persists numeric fields. Driver/connection must be provided by the caller.
 * <p>
 * NOTE: this class is intentionally lightweight so it can be used when a JDBC driver is
 * available at runtime. Tests continue to use the YML provider by default.
 */
public class MySqlStorageProvider implements StorageProvider {
    private final DataSource dataSource;
    private final String tableName;

    public MySqlStorageProvider(DataSource dataSource) {
        this(dataSource, "eztax_stats");
    }

    public MySqlStorageProvider(DataSource dataSource, String tableName) {
        this.dataSource = dataSource;
        this.tableName = tableName;
    }

    private void ensureTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                "id INT PRIMARY KEY, " +
                "total_removed DOUBLE, daily_removed DOUBLE, weekly_removed DOUBLE, treasury_balance DOUBLE, " +
                "daily_reset VARCHAR(32), weekly_reset VARCHAR(32), " +
                "sink_transaction DOUBLE, sink_wealth DOUBLE, sink_inactivity DOUBLE, sink_death DOUBLE" +
                ")";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    @Override
    public StatsData load() throws IOException {
        try (Connection conn = dataSource.getConnection()) {
            ensureTable(conn);
            String q = "SELECT * FROM `" + tableName + "` WHERE id = 1 LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(q)) {
                try (ResultSet rs = ps.executeQuery()) {
                    StatsData d = new StatsData();
                    if (!rs.next()) {
                        return d;
                    }
                    d.setTotalRemoved(rs.getDouble("total_removed"));
                    d.setDailyRemoved(rs.getDouble("daily_removed"));
                    d.setWeeklyRemoved(rs.getDouble("weekly_removed"));
                    d.setTreasuryBalance(rs.getDouble("treasury_balance"));
                    String daily = rs.getString("daily_reset");
                    String weekly = rs.getString("weekly_reset");
                    if (daily != null && !daily.isEmpty()) {
                        d.setDailyResetDate(java.time.LocalDate.parse(daily));
                    }
                    if (weekly != null && !weekly.isEmpty()) {
                        d.setWeeklyResetDate(java.time.LocalDate.parse(weekly));
                    }
                    d.getSinkTotals().put(com.skyblockexp.eztax.service.TaxSink.TRANSACTION, rs.getDouble("sink_transaction"));
                    d.getSinkTotals().put(com.skyblockexp.eztax.service.TaxSink.WEALTH, rs.getDouble("sink_wealth"));
                    d.getSinkTotals().put(com.skyblockexp.eztax.service.TaxSink.INACTIVITY, rs.getDouble("sink_inactivity"));
                    d.getSinkTotals().put(com.skyblockexp.eztax.service.TaxSink.DEATH, rs.getDouble("sink_death"));
                    return d;
                }
            }
        } catch (SQLException ex) {
            throw new IOException("Failed to load stats from MySQL", ex);
        }
    }

    @Override
    public void save(StatsData data) throws IOException {
        String insert = "INSERT INTO `" + tableName + "` (id, total_removed, daily_removed, weekly_removed, treasury_balance, daily_reset, weekly_reset, " +
            "sink_transaction, sink_wealth, sink_inactivity, sink_death) VALUES (1, ?,?,?,?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE total_removed = VALUES(total_removed), daily_removed = VALUES(daily_removed), weekly_removed = VALUES(weekly_removed), " +
                "treasury_balance = VALUES(treasury_balance), daily_reset = VALUES(daily_reset), weekly_reset = VALUES(weekly_reset), " +
                "sink_transaction = VALUES(sink_transaction), sink_wealth = VALUES(sink_wealth), sink_inactivity = VALUES(sink_inactivity), sink_death = VALUES(sink_death)";
        try (Connection conn = dataSource.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setDouble(1, data.getTotalRemoved());
                ps.setDouble(2, data.getDailyRemoved());
                ps.setDouble(3, data.getWeeklyRemoved());
                ps.setDouble(4, data.getTreasuryBalance());
                ps.setString(5, data.getDailyResetDate() != null ? data.getDailyResetDate().toString() : null);
                ps.setString(6, data.getWeeklyResetDate() != null ? data.getWeeklyResetDate().toString() : null);
                ps.setDouble(7, data.getSinkTotals().getOrDefault(com.skyblockexp.eztax.service.TaxSink.TRANSACTION, 0.0D));
                ps.setDouble(8, data.getSinkTotals().getOrDefault(com.skyblockexp.eztax.service.TaxSink.WEALTH, 0.0D));
                ps.setDouble(9, data.getSinkTotals().getOrDefault(com.skyblockexp.eztax.service.TaxSink.INACTIVITY, 0.0D));
                ps.setDouble(10, data.getSinkTotals().getOrDefault(com.skyblockexp.eztax.service.TaxSink.DEATH, 0.0D));
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new IOException("Failed to save stats to MySQL", ex);
        }
    }
}
