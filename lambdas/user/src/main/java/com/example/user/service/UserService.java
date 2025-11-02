package com.example.user.service;

import com.example.user.db.DatabaseConfig;
import com.example.user.entity.User;

import java.sql.*;
import java.time.LocalDateTime;

public class UserService {

    public User createUser(String username, String email, String password) throws SQLException {
        String insert = "INSERT INTO users (username, email, password, created_at) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    return new User(id, username, email, password, LocalDateTime.now());
                }
            }
        }
        return null;
    }

    public User findByUsername(String username) throws SQLException {
        String q = "SELECT id, username, email, password, created_at FROM users WHERE username = ? LIMIT 1";
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime created = ts != null ? ts.toLocalDateTime() : null;
                    return new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"),
                            rs.getString("password"), created);
                }
            }
        }
        return null;
    }
}
