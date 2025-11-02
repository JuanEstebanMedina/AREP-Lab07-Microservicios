-- Script de inicialización para MySQL
-- Ejecutar este script si necesitas crear la base de datos manualmente

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS twitterdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE twitterdb;

-- Las tablas se crearán automáticamente por Hibernate con ddl-auto=update
-- Este script es opcional, solo si quieres crear la BD manualmente

-- Para verificar las tablas creadas:
-- SHOW TABLES;
-- DESCRIBE usuarios;
-- DESCRIBE streams;
-- DESCRIBE posts;

-- Para ver datos:
-- SELECT * FROM usuarios;
-- SELECT * FROM streams;
-- SELECT * FROM posts ORDER BY created_at DESC;
