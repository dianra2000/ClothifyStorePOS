package com.clothify.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {

    // ==================== HIKARI CONNECTION POOL ====================
    private static HikariDataSource dataSource;
    private static final String URL = "jdbc:mysql://localhost:3306/clothify_store?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";  // Change to your MySQL username
    private static final String PASSWORD = "";      // Change to your MySQL password

    // Static initializer - runs once when class is loaded
    static {
        try {
            // Check if MySQL JDBC driver is available
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Configure HikariCP connection pool
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);

            // Connection pool settings
            config.setMaximumPoolSize(10);              // Maximum connections
            config.setMinimumIdle(5);                    // Minimum idle connections
            config.setConnectionTimeout(30000);          // 30 seconds timeout
            config.setIdleTimeout(600000);                // 10 minutes idle timeout
            config.setMaxLifetime(1800000);               // 30 minutes max lifetime
            config.setConnectionTestQuery("SELECT 1");    // Test query
            config.setValidationTimeout(5000);             // 5 seconds validation timeout

            // Additional performance settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);

            System.out.println("Database connection pool initialized successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection pool!");
            e.printStackTrace();
        }
    }

    // ==================== GET CONNECTION ====================
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized!");
        }
        return dataSource.getConnection();
    }

    // ==================== CLOSE CONNECTION POOL ====================
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed.");
        }
    }

    // ==================== HELPER METHODS FOR CLEANUP ====================

    // Close ResultSet, Statement, and Connection
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }

    // Close ResultSet, Statement (no connection)
    public static void close(ResultSet rs, Statement stmt) {
        closeResultSet(rs);
        closeStatement(stmt);
    }

    // Close Statement and Connection
    public static void close(Statement stmt, Connection conn) {
        closeStatement(stmt);
        closeConnection(conn);
    }

    // Close Connection only
    public static void close(Connection conn) {
        closeConnection(conn);
    }

    private static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
    }

    private static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
    }

    private static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // Returns connection to pool, doesn't actually close it
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }

    // ==================== TEST CONNECTION ====================
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Database connection successful!");
                System.out.println("   Database: " + conn.getCatalog());
                System.out.println("   Driver: " + conn.getMetaData().getDriverName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("   Error: " + e.getMessage());
        }
        return false;
    }

    // ==================== GET POOL STATUS ====================
    public static String getPoolStatus() {
        if (dataSource != null) {
            return String.format(
                    "Active: %d, Idle: %d, Total: %d, Waiting: %d",
                    dataSource.getHikariPoolMXBean().getActiveConnections(),
                    dataSource.getHikariPoolMXBean().getIdleConnections(),
                    dataSource.getHikariPoolMXBean().getTotalConnections(),
                    dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
            );
        }
        return "DataSource not initialized";
    }
}