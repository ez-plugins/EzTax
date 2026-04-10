package com.skyblockexp.eztax.storage;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Minimal DataSource wrapper around DriverManager for simple JDBC connections
 * (used to avoid adding external dependencies). Not intended as a production
 * connection pool.
 */
public class DriverManagerDataSource implements DataSource {
    private final String url;
    private final String user;
    private final String password;

    public DriverManagerDataSource(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Connection getConnection(String username, String pwd) throws SQLException {
        return DriverManager.getConnection(url, username, pwd);
    }

    // --- the remaining methods are not used by our code but must be implemented ---
    @Override
    public PrintWriter getLogWriter() throws SQLException { throw new UnsupportedOperationException(); }
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException { throw new UnsupportedOperationException(); }
    @Override
    public void setLoginTimeout(int seconds) throws SQLException { throw new UnsupportedOperationException(); }
    @Override
    public int getLoginTimeout() throws SQLException { throw new UnsupportedOperationException(); }
    @Override
    public Logger getParentLogger() { throw new UnsupportedOperationException(); }
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("Not a wrapper"); }
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
}
