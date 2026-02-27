package com.clothify.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static HikariDataSource dataSource;
    private static String connectionError = null;

    // ==================== DATABASE SETTINGS ====================
    // Change PASSWORD below if your MySQL has a password set
    private static final String URL = "jdbc:mysql://localhost:3308/clothify_store?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    // ===========================================================

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(5000); // 5 second timeout
            config.setInitializationFailTimeout(5000);

            dataSource = new HikariDataSource(config);
            System.out.println("✅ Database connected successfully!");

        } catch (Exception e) {
            connectionError = e.getMessage();
            System.err.println("❌ Database connection FAILED: " + e.getMessage());
            System.err.println("   → Make sure MySQL is running and 'clothify_store' database exists.");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not connected. Error: " + connectionError);
        }
        return dataSource.getConnection();
    }

    /** Returns true if the database pool is up and reachable */
    public static boolean isConnected() {
        if (dataSource == null || dataSource.isClosed())
            return false;
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /** Returns the connection error message if connection failed, or null if OK */
    public static String getConnectionError() {
        return connectionError;
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database pool closed.");
        }
    }
}