package com.example.post.service;

import com.example.post.db.DatabaseConfig;
import com.example.post.entity.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PostService: helpers to create posts and list them using the DB schema
 * provided
 * by the user: users(id), streams(id,nombre,descripcion),
 * posts(id,user_id,stream_id,contenido,created_at)
 */
public class PostService {
    private static final int MAX_LEN = 140;

    /**
     * Insert a post given numeric userId and streamId.
     */
    public Post createPost(long userId, long streamId, String contenido) throws SQLException {
        validateContenido(contenido);

        String insertSql = "INSERT INTO posts (user_id, stream_id, contenido, created_at) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setLong(2, streamId);
            ps.setString(3, contenido);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                Long id = null;
                if (rs.next())
                    id = rs.getLong(1);
                return new Post(id, userId, streamId, contenido, LocalDateTime.now());
            }
        }
    }

    /**
     * Create a post by resolving username and stream nombre. If stream doesn't
     * exist, create it safely.
     */
    public Post createPostByNames(String username, String streamNombre, String contenido) throws SQLException {
        validateContenido(contenido);

        try (Connection c = DatabaseConfig.getConnection()) {
            Long userId = findUserIdByUsername(c, username);
            if (userId == null)
                throw new SQLException("User not found: " + username);

            Long streamId = findStreamIdByNombre(c, streamNombre);
            if (streamId == null) {
                // create stream using INSERT ... ON DUPLICATE KEY UPDATE to avoid race
                String ins = "INSERT INTO streams (nombre, descripcion, created_at) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE nombre = nombre";
                try (PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, streamNombre);
                    ps.setString(2, "");
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next())
                            streamId = rs.getLong(1);
                    }
                }

                // If still null (duplicate inserted by another tx), query again
                if (streamId == null)
                    streamId = findStreamIdByNombre(c, streamNombre);
            }

            return createPost(userId, streamId, contenido);
        }
    }

    private void validateContenido(String contenido) {
        if (contenido == null)
            throw new IllegalArgumentException("contenido cannot be null");
        if (contenido.length() > MAX_LEN)
            throw new IllegalArgumentException("contenido exceeds " + MAX_LEN + " characters");
    }

    private Long findUserIdByUsername(Connection c, String username) throws SQLException {
        String q = "SELECT id FROM users WHERE username = ? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getLong("id");
            }
        }
        return null;
    }

    private Long findStreamIdByNombre(Connection c, String nombre) throws SQLException {
        String q = "SELECT id FROM streams WHERE nombre = ? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getLong("id");
            }
        }
        return null;
    }

    /**
     * List recent posts (no joins) returning Post entities with userId and
     * streamId.
     */
    public List<Post> listRecentPosts(int limit) throws SQLException {
        List<Post> out = new ArrayList<>();
        String q = "SELECT p.id, p.user_id, p.stream_id, p.contenido, p.created_at FROM posts p ORDER BY p.created_at DESC LIMIT ?";
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    Long userId = rs.getLong("user_id");
                    Long streamId = rs.getLong("stream_id");
                    String contenido = rs.getString("contenido");
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;
                    out.add(new Post(id, userId, streamId, contenido, createdAt));
                }
            }
        }
        return out;
    }

}
