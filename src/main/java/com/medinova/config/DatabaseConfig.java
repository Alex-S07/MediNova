package com.medinova.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Database configuration and connection pool management
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private final Properties dbProperties;
    private final ConcurrentLinkedQueue<Connection> connectionPool;
    private final AtomicInteger connectionCount;
    private final int maxConnections;
    
    private DatabaseConfig() {
        this.dbProperties = loadProperties();
        this.maxConnections = Integer.parseInt(dbProperties.getProperty("db.maxConnections", "10"));
        this.connectionPool = new ConcurrentLinkedQueue<>();
        this.connectionCount = new AtomicInteger(0);
        
        // Load MySQL driver
        try {
            Class.forName(dbProperties.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }
    
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                // Default properties if file not found
                props.setProperty("db.url", "jdbc:mysql://localhost:3306/medinova_hospital");
                props.setProperty("db.username", "root");
                props.setProperty("db.password", "password");
                props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                props.setProperty("db.maxConnections", "10");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
        return props;
    }
    
    public Connection getConnection() throws SQLException {
        Connection connection = connectionPool.poll();
        
        if (connection == null || connection.isClosed()) {
            if (connectionCount.get() < maxConnections) {
                connection = createNewConnection();
                connectionCount.incrementAndGet();
            } else {
                // Wait for available connection or create new one if pool is full
                connection = createNewConnection();
            }
        }
        
        return connection;
    }
    
    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(
            dbProperties.getProperty("db.url"),
            dbProperties.getProperty("db.username"),
            dbProperties.getProperty("db.password")
        );
    }
    
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connectionPool.offer(connection);
                }
            } catch (SQLException e) {
                // Log error and close connection
                try {
                    connection.close();
                } catch (SQLException ex) {
                    // Ignore
                }
                connectionCount.decrementAndGet();
            }
        }
    }
    
    public void closeAllConnections() {
        Connection connection;
        while ((connection = connectionPool.poll()) != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
        connectionCount.set(0);
    }
    
    public String getDatabaseUrl() {
        return dbProperties.getProperty("db.url");
    }
    
    public String getDatabaseUsername() {
        return dbProperties.getProperty("db.username");  
    }
}