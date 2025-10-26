-- Datos de prueba para Twitter Clone API

-- Insertar usuarios de prueba
INSERT INTO usuarios (username, email, password, created_at) VALUES ('juanperez', 'juan@example.com', 'password123', CURRENT_TIMESTAMP);
INSERT INTO usuarios (username, email, password, created_at) VALUES ('mariagarcia', 'maria@example.com', 'password123', CURRENT_TIMESTAMP);
INSERT INTO usuarios (username, email, password, created_at) VALUES ('carloslopez', 'carlos@example.com', 'password123', CURRENT_TIMESTAMP);

-- Insertar streams de prueba
INSERT INTO streams (nombre, descripcion, created_at) VALUES ('General', 'Stream principal de posts generales', CURRENT_TIMESTAMP);
INSERT INTO streams (nombre, descripcion, created_at) VALUES ('Tecnología', 'Posts sobre tecnología y programación', CURRENT_TIMESTAMP);
INSERT INTO streams (nombre, descripcion, created_at) VALUES ('Deportes', 'Noticias y comentarios deportivos', CURRENT_TIMESTAMP);

-- Insertar posts de prueba
INSERT INTO posts (usuario_id, stream_id, contenido, created_at) VALUES (1, 1, 'Hola mundo! Este es mi primer post en la plataforma', CURRENT_TIMESTAMP);
INSERT INTO posts (usuario_id, stream_id, contenido, created_at) VALUES (2, 1, 'Increíble plataforma para compartir ideas cortas', CURRENT_TIMESTAMP);
INSERT INTO posts (usuario_id, stream_id, contenido, created_at) VALUES (1, 2, 'Aprendiendo Spring Boot es más fácil de lo que pensaba', CURRENT_TIMESTAMP);
INSERT INTO posts (usuario_id, stream_id, contenido, created_at) VALUES (3, 2, 'Java 17 trae características geniales para desarrollo moderno', CURRENT_TIMESTAMP);
INSERT INTO posts (usuario_id, stream_id, contenido, created_at) VALUES (2, 3, 'Gran partido de fútbol hoy!', CURRENT_TIMESTAMP);