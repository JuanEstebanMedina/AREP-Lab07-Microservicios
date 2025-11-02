package com.example.stream.entity;

import java.time.LocalDateTime;

/**
 * Stream entity aligned with DB schema: id, nombre, descripcion, created_at
 */
public class StreamEntity {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime createdAt;

    public StreamEntity() {
    }

    public StreamEntity(Long id, String nombre, String descripcion, LocalDateTime createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
