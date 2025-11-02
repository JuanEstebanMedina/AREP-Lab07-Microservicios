package com.example.stream.service;

import com.example.stream.db.DatabaseConfig;
import com.example.stream.entity.StreamEntity;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Stream service to create or fetch stream metadata (columns: nombre,
 * descripcion)
 */
public class StreamService {

    public StreamEntity createStream(String nombre, String descripcion) throws SQLException {
        String insert = "INSERT INTO streams (nombre, descripcion, created_at) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    return new StreamEntity(id, nombre, descripcion, LocalDateTime.now());
                }
            }
        }
        // If no generated key, try to select existing id
        String sel = "SELECT id, descripcion, created_at FROM streams WHERE nombre = ? LIMIT 1";
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement ps = c.prepareStatement(sel)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StreamEntity(rs.getLong("id"), nombre, rs.getString("descripcion"),
                            rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
        }
        return null;
    }
}
