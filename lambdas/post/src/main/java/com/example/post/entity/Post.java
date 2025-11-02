package com.example.post.entity;

import java.time.LocalDateTime;

/**
 * Post entity aligned with the DB schema provided by the user.
 * Columns: id, user_id, stream_id, contenido, created_at
 */
public class Post {
    private Long id;
    private Long userId;
    private Long streamId;
    private String contenido;
    private LocalDateTime createdAt;

    public Post() {
    }

    public Post(Long id, Long userId, Long streamId, String contenido, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.streamId = streamId;
        this.contenido = contenido;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStreamId() {
        return streamId;
    }

    public void setStreamId(Long streamId) {
        this.streamId = streamId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
