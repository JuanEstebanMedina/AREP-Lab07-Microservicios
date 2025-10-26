# AREP-Lab07-Microservicios
Tarea de Microservicios

# Autores
- Juan Esteban Medina Rivas
- María Paula Sánchez Macías
- Sergio Andrés Bejarano Rodríguez

# Twitter Clone API - Monolito Spring Boot

API REST monolítico tipo Twitter que permite crear posts de 140 caracteres y registrarlos en streams (hilos).

## 📋 Características

- ✅ CRUD completo de Usuarios
- ✅ CRUD completo de Streams (hilos)
- ✅ CRUD completo de Posts
- ✅ Validación de 140 caracteres máximo por post
- ✅ Relaciones entre entidades (Usuario → Posts, Stream → Posts)
- ✅ Ordenamiento de posts por fecha (más recientes primero)
- ✅ Base de datos H2 en memoria (desarrollo)
- ✅ Manejo de excepciones global
- ✅ CORS configurado

## 🛠️ Tecnologías

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Validation
- H2 Database (desarrollo)
- MySQL (producción)
- Lombok
- Maven

## 📁 Estructura del Proyecto

```
twitter-clone-api/
├── src/
│   ├── main/
│   │   ├── java/com/twitter/api/
│   │   │   ├── TwitterApiApplication.java
│   │   │   ├── config/
│   │   │   │   └── CorsConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java
│   │   │   │   ├── StreamController.java
│   │   │   │   └── PostController.java
│   │   │   ├── dto/
│   │   │   │   ├── UserDTO.java
│   │   │   │   ├── StreamDTO.java
│   │   │   │   └── PostDTO.java
│   │   │   ├── entity/
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Stream.java
│   │   │   │   └── Post.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── StreamRepository.java
│   │   │   │   └── PostRepository.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── StreamService.java
│   │   │   │   └── PostService.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── ResourceNotFoundException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   └── test/
├── pom.xml
└── run.sh
```

## 🚀 Instalación y Ejecución

### Prerequisitos

- Java 17 o superior
- Maven 3.6 o superior

### Opción 1: Script de ejecución (Linux/Mac)

```bash
chmod +x run.sh
./run.sh
```

### Opción 2: Comandos Maven

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

### Opción 3: JAR ejecutable

```bash
mvn clean package
java -jar target/twitter-clone-api-1.0.0.jar
```

La aplicación estará disponible en: `http://localhost:8080`

## 🗄️ Base de Datos

### H2 Console (Desarrollo)

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:twitterdb`
- Username: `sa`
- Password: (vacío)

### MySQL (Producción)

Para usar MySQL, consulta el archivo `CONFIGURACION-MYSQL.md`

**Quick Start MySQL:**
```bash
# 1. Crear base de datos
mysql -u root -p -e "CREATE DATABASE twitterdb;"

# 2. Ejecutar con perfil MySQL
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Configuración en `application-mysql.properties`

## 📡 Endpoints del API

### Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| POST | `/api/users` | Crear nuevo usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario |

**Ejemplo POST /api/users:**
```json
{
  "username": "juanperez",
  "email": "juan@example.com",
  "password": "password123"
}
```

### Streams

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/streams` | Listar todos los streams |
| GET | `/api/streams/{id}` | Obtener stream por ID |
| POST | `/api/streams` | Crear nuevo stream |
| GET | `/api/streams/{id}/posts` | Obtener posts de un stream |

**Ejemplo POST /api/streams:**
```json
{
  "nombre": "General",
  "descripcion": "Stream principal de posts"
}
```

### Posts

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/posts` | Listar todos los posts |
| GET | `/api/posts/{id}` | Obtener post por ID |
| POST | `/api/posts` | Crear nuevo post |
| DELETE | `/api/posts/{id}` | Eliminar post |
| GET | `/api/posts/user/{userId}` | Obtener posts de un usuario |

**Ejemplo POST /api/posts:**
```json
{
  "usuarioId": 1,
  "streamId": 1,
  "contenido": "Este es mi primer post! 🚀"
}
```

**Nota:** El contenido del post tiene un máximo de 140 caracteres.

## 🧪 Pruebas con cURL

### Crear usuario
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juanperez",
    "email": "juan@example.com",
    "password": "password123"
  }'
```

### Crear stream
```bash
curl -X POST http://localhost:8080/api/streams \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "General",
    "descripcion": "Stream principal"
  }'
```

### Crear post
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "streamId": 1,
    "contenido": "Mi primer post en el sistema!"
  }'
```

### Listar todos los posts
```bash
curl http://localhost:8080/api/posts
```

### Obtener posts de un stream específico
```bash
curl http://localhost:8080/api/streams/1/posts
```

### Obtener posts de un usuario específico
```bash
curl http://localhost:8080/api/posts/user/1
```

## 🎯 Modelo de Datos

### Entidades

**Usuario**
- id (Long, PK)
- username (String, único)
- email (String, único)
- password (String)
- createdAt (LocalDateTime)
- posts (List<Post>, relación 1:N)

**Stream**
- id (Long, PK)
- nombre (String)
- descripcion (String)
- createdAt (LocalDateTime)
- posts (List<Post>, relación 1:N)

**Post**
- id (Long, PK)
- usuarioId (Long, FK)
- streamId (Long, FK)
- contenido (String, max 140 caracteres)
- createdAt (LocalDateTime)

### Relaciones

- Un Usuario puede crear muchos Posts (1:N)
- Un Stream contiene muchos Posts (1:N)
- Un Post pertenece a un Usuario y a un Stream (N:1)

## ✅ Validaciones

- Username: 3-50 caracteres, único
- Email: formato válido, único
- Password: mínimo 6 caracteres
- Contenido del post: 1-140 caracteres
- IDs: requeridos para crear posts

## 🔒 Manejo de Errores

El API maneja los siguientes errores:

- **404 Not Found:** Recurso no encontrado
- **400 Bad Request:** Validación fallida o datos inválidos
- **500 Internal Server Error:** Error del servidor

**Ejemplo de respuesta de error:**
```json
{
  "timestamp": "2025-10-26T10:30:00",
  "status": 404,
  "message": "Usuario no encontrado con id: 5"
}
```

## 📊 Datos de Prueba

El proyecto incluye datos de prueba en `data.sql`:
- 3 usuarios
- 3 streams
- 5 posts

Se cargan automáticamente al iniciar la aplicación.

## 🔍 Verificación del Sistema

Checklist de verificación:

- [ ] Servidor inicia en puerto 8080
- [ ] H2 Console accesible
- [ ] Crear usuario mediante POST
- [ ] Crear stream mediante POST
- [ ] Crear post de 140 caracteres
- [ ] Validación rechaza posts > 140 caracteres
- [ ] Posts listados por fecha descendente
- [ ] Relaciones entre entidades funcionan
- [ ] CORS configurado correctamente

## 🎨 Arquitectura

### Capas

1. **Controller:** Maneja peticiones HTTP y respuestas
2. **Service:** Contiene lógica de negocio
3. **Repository:** Acceso a datos (JPA)
4. **Entity:** Modelo de dominio
5. **DTO:** Transferencia de datos

### Patrón de Diseño

- Arquitectura en capas (Layered Architecture)
- Repository Pattern
- DTO Pattern
- Dependency Injection

## 🚧 Próximos Pasos

- [ ] Implementar seguridad con JWT (Punto 4)
- [ ] Separar en microservicios con Lambda (Punto 5)
- [ ] Desplegar en AWS Lambda (Punto 6)
- [ ] Crear frontend JavaScript (Punto 2)
- [ ] Deploy en S3 (Punto 3)






## Despliegue en S3  

Se crea el bucket donde se almacenarán los archivos estáticos:

<img width="2879" height="1184" alt="image" src="https://github.com/user-attachments/assets/bd878602-0425-441d-97a5-d059838be67b" />


<img width="2879" height="1302" alt="image" src="https://github.com/user-attachments/assets/bd68ffc0-710a-4a90-b2d3-3646ca9272f6" />


Y para cada archivo se habilita el acceso público para que esté disponible en internet.

<img width="2879" height="1384" alt="Captura de pantalla 2025-10-26 152104" src="https://github.com/user-attachments/assets/50fec28f-2bfb-4062-b171-410b39f04bed" />

Resultado:

<img width="2879" height="1461" alt="image" src="https://github.com/user-attachments/assets/4c522a0f-5b9b-400c-86e4-8f723a5db9af" />



## 📝 Logs

Los logs están configurados en nivel DEBUG para desarrollo. Verás:
- Queries SQL ejecutadas
- Peticiones HTTP recibidas
- Respuestas enviadas

## 🤝 Contribución

Este proyecto es parte de un taller académico.

## 📄 Licencia

Proyecto educativo - Uso académico

---

**Autor:** Taller Spring Boot  
**Fecha:** Octubre 2025  
**Versión:** 1.0.0
