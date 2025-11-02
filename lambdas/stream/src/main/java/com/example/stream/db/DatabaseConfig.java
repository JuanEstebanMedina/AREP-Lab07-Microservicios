package com.example.stream.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Minimal JDBC configuration for the stream module.
 */
public class DatabaseConfig {
    private static final String HOST = "db-microservicios.czom6keqa1uc.us-east-1.rds.amazonaws.com";
    private static final String DB = "db-microservicios";
    private static final String USER = "admin";
    private static final String PASS = "AREP-microservicios";
    private static final String URL = "jdbc:mysql://" + HOST + ":3306/" + DB + "?serverTimezone=UTC";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
