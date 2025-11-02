package com.example.post.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Minimal JDBC configuration for connecting to the provided MySQL RDS instance.
 * NOTE: Credentials are stored here for convenience per user's request.
 * Consider
 * moving them to environment variables or AWS Secrets Manager for production.
 */
public class DatabaseConfig {
    private static final String HOST = "db-microservicios.czom6keqa1uc.us-east-1.rds.amazonaws.com";
    private static final String DB = "db-microservicios";
    private static final String USER = "admin";
    private static final String PASS = "AREP-microservicios";
    private static final String URL = "jdbc:mysql://" + HOST + ":3306/" + DB + "?serverTimezone=UTC";

    static {
        try {
            // MySQL driver name for Connector/J 8+
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // If driver not present, connections will fail later; keep the exception trace.
            System.err.println("MySQL JDBC driver not found on classpath: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
