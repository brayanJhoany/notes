# Notes API

API REST para gestión de notas personales con sistema de usuarios, amistades y compartición de notas. Construida con **Spring Boot 3.5.5** siguiendo la **Arquitectura Hexagonal (Ports & Adapters)**.

---

## Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Configuración](#configuración)
- [Autenticación](#autenticación)
- [APIs](#apis)
  - [Auth](#auth---apíauth)
  - [Perfil de Usuario](#perfil-de-usuario---apiprofile)
  - [Usuarios (público)](#usuarios-público---apiusers)
  - [Administración de Usuarios](#administración-de-usuarios---apiadminusers)
  - [Notas](#notas---apinotes)
  - [Compartición de Notas](#compartición-de-notas---apinote-shares)
  - [Amistad](#amistad---apifriendship)
- [Modelos de Datos](#modelos-de-datos)
- [Errores](#errores)

---

## Arquitectura

El proyecto sigue **Clean Architecture / Arquitectura Hexagonal**, organizado en 5 módulos de negocio:

```
com.bescobar.notes
├── user/           # Gestión de usuarios y autenticación
├── note/           # CRUD de notas personales
├── noteshare/      # Compartición de notas entre amigos
├── friendship/     # Sistema de solicitudes de amistad
└── shared/         # Seguridad, configuración y utilidades transversales
```

Cada módulo se divide en tres capas:

| Capa | Responsabilidad |
|------|----------------|
| `domain/` | Entidades de negocio, reglas y puertos (interfaces) |
| `application/` | Casos de uso que orquestan la lógica |
| `infrastructure/` | Controladores REST, adaptadores de persistencia, seguridad |

---

## Stack Tecnológico

| Componente | Tecnología |
|------------|-----------|
| Framework | Spring Boot 3.5.5 |
| Lenguaje | Java 17 |
| Base de datos | MySQL |
| ORM | JPA / Hibernate |
| Autenticación | JWT (JJWT 0.12.3) + Spring Security |
| Hash de contraseñas | BCrypt |
| Build | Maven |
| Documentación | OpenAPI / Swagger (`/swagger-ui.html`) |
| Calidad de código | CheckStyle, PMD, SpotBugs |

---

## Configuración

### Variables de entorno requeridas

```env
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/notes
DB_USERNAME=root
DB_PASSWORD=secret

# JWT
JWT_SECRET=tu_clave_secreta_muy_larga
JWT_EXPIRATION=1800000          # 30 minutos (access token)
JWT_REFRESH_EXPIRATION=604800000 # 7 días (refresh token)

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### Ejecutar el proyecto

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

---

## Autenticación

La API usa **JWT Bearer Tokens**. El flujo es:

1. Registrarse o iniciar sesión → obtener `accessToken` y `refreshToken`
2. Incluir el `accessToken` en el header de cada petición protegida:

```
Authorization: Bearer <accessToken>
```

3. Cuando el `accessToken` expire (30 min), usar el `refreshToken` para obtener uno nuevo sin reiniciar sesión.

**Endpoints públicos** (no requieren token):
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh-token`
- `POST /api/auth/logout`
- `GET /actuator/health`

---

## APIs

---

### Auth - `/api/auth`

Gestiona el ciclo de vida de la autenticación: registro, login, renovación de tokens y cierre de sesión.

---

#### `POST /api/auth/register`

**Objetivo:** Registrar un nuevo usuario en el sistema.

**Cómo funciona:**
1. Valida el cuerpo de la petición (formato de email, longitud de contraseña, etc.).
2. Verifica que el email no esté en uso.
3. Encripta la contraseña con BCrypt.
4. Persiste el usuario con rol `REGULAR` y `active = true`.
5. Genera y retorna un par de tokens JWT (access + refresh).

**Request:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "+1234567890",
  "address": "123 Main St"
}
```

**Response `201 Created`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 1800,
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "address": "123 Main St",
    "role": "REGULAR"
  }
}
```

**Validaciones:**
- `fullName`: 3–255 caracteres
- `email`: formato válido
- `password`: 8–64 caracteres

---

#### `POST /api/auth/login`

**Objetivo:** Autenticar a un usuario existente y obtener tokens de acceso.

**Cómo funciona:**
1. Busca al usuario por email.
2. Verifica que la contraseña coincida con el hash almacenado (BCrypt).
3. Invalida cualquier refresh token anterior.
4. Genera un nuevo par de tokens y los retorna.

**Request:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response `200 OK`:** igual que `/register`.

---

#### `POST /api/auth/refresh-token`

**Objetivo:** Renovar el access token usando el refresh token, sin obligar al usuario a volver a iniciar sesión.

**Cómo funciona:**
1. Valida la firma y expiración del refresh token.
2. Busca el token en base de datos para verificar que no fue revocado.
3. Genera un nuevo access token y lo retorna.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response `200 OK`:** igual que `/register`.

---

#### `POST /api/auth/logout`

**Objetivo:** Cerrar la sesión del usuario e invalidar el refresh token.

**Cómo funciona:**
1. Recibe el refresh token.
2. Lo busca en base de datos y lo elimina, impidiendo futuros usos.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response `204 No Content`.**

---

### Perfil de Usuario - `/api/profile`

Permite al usuario autenticado ver y actualizar su propio perfil.

---

#### `GET /api/profile`

**Objetivo:** Obtener la información del perfil del usuario actualmente autenticado.

**Cómo funciona:**
1. Extrae el ID del usuario desde el token JWT.
2. Busca el usuario en base de datos.
3. Retorna sus datos en formato `UserResponse`.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "address": "123 Main St",
  "role": "REGULAR"
}
```

---

#### `PUT /api/profile`

**Objetivo:** Actualizar el nombre, email, teléfono o dirección del usuario autenticado.

**Cómo funciona:**
1. Extrae el ID del usuario desde el JWT.
2. Valida los campos del request.
3. Actualiza únicamente los campos enviados.
4. Retorna el perfil actualizado.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "fullName": "John Smith",
  "email": "johnsmith@example.com",
  "phone": "+0987654321",
  "address": "456 Oak Ave"
}
```

**Response `200 OK`:** `UserResponse` actualizado.

---

### Usuarios (público) - `/api/users`

Permite a cualquier usuario autenticado buscar otros usuarios activos.

---

#### `GET /api/users`

**Objetivo:** Listar los usuarios activos del sistema con filtros opcionales y paginación, útil para encontrar personas con quienes conectar.

**Cómo funciona:**
1. Aplica filtros opcionales sobre `email` y `fullName` (búsqueda parcial).
2. Solo retorna usuarios con `active = true`.
3. Pagina los resultados y retorna metadatos de paginación.

**Headers:** `Authorization: Bearer <token>`

**Query Params:**
| Param | Tipo | Descripción |
|-------|------|-------------|
| `email` | string (opcional) | Filtrar por email (parcial) |
| `fullname` | string (opcional) | Filtrar por nombre (parcial) |
| `page` | int (default: 0) | Página |
| `size` | int (default: 10) | Tamaño de página |

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": 2,
      "fullName": "Jane Smith",
      "email": "jane@example.com",
      "phone": "+111222333",
      "address": "789 Elm St",
      "role": "REGULAR"
    }
  ],
  "totalPages": 1,
  "numberOfElements": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

---

### Administración de Usuarios - `/api/admin/users`

Operaciones de gestión de usuarios reservadas para el rol `ADMIN`.

---

#### `GET /api/admin/users`

**Objetivo:** Listar todos los usuarios (incluyendo inactivos) para gestión administrativa.

**Cómo funciona:** Igual que `GET /api/users` pero sin filtrar por `active`, accesible solo para admins.

**Headers:** `Authorization: Bearer <token>` (rol ADMIN requerido)

**Response `200 OK`:** `PageResponse<UserResponse>`

---

#### `GET /api/admin/users/{id}`

**Objetivo:** Obtener el detalle completo de cualquier usuario por su ID.

**Response `200 OK`:** `UserResponse`

---

#### `PUT /api/admin/users/{id}`

**Objetivo:** Actualizar la información de cualquier usuario del sistema.

**Request:** igual que `PUT /api/profile`.

**Response `200 OK`:** `UserResponse` actualizado.

---

#### `DELETE /api/admin/users/{id}`

**Objetivo:** Desactivar (soft delete) un usuario del sistema. El usuario queda con `active = false` y deja de aparecer en búsquedas.

**Response `204 No Content`.**

---

### Notas - `/api/notes`

CRUD completo de notas personales. Cada nota pertenece exclusivamente a su creador.

---

#### `POST /api/notes`

**Objetivo:** Crear una nueva nota asociada al usuario autenticado.

**Cómo funciona:**
1. Extrae el ID del usuario del JWT.
2. Valida título y contenido.
3. Persiste la nota con el usuario como propietario.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "title": "Mi primera nota",
  "content": "Contenido de la nota..."
}
```

**Validaciones:**
- `title`: 3–500 caracteres
- `content`: 3–5000 caracteres

**Response `201 Created`:**
```json
{
  "id": 1,
  "title": "Mi primera nota",
  "content": "Contenido de la nota...",
  "createdAt": "2025-03-01 12:00:00",
  "userId": 1
}
```

---

#### `GET /api/notes`

**Objetivo:** Obtener todas las notas del usuario autenticado.

**Cómo funciona:**
1. Extrae el ID del usuario del JWT.
2. Consulta solo las notas cuyo propietario sea ese usuario.
3. Retorna la lista completa.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** `List<NoteWebResponse>`

---

#### `GET /api/notes/{id}`

**Objetivo:** Obtener el detalle de una nota específica.

**Cómo funciona:**
1. Busca la nota por ID.
2. Verifica que el solicitante sea el propietario (control de acceso).
3. Retorna la nota si tiene acceso.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** `NoteWebResponse`

---

#### `PUT /api/notes/{id}`

**Objetivo:** Actualizar el título o contenido de una nota existente.

**Cómo funciona:**
1. Busca la nota por ID.
2. Verifica que el solicitante sea el propietario.
3. Actualiza los campos y persiste los cambios.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "title": "Título actualizado",
  "content": "Nuevo contenido..."
}
```

**Response `200 OK`:** `NoteWebResponse` actualizado.

---

#### `DELETE /api/notes/{id}`

**Objetivo:** Eliminar permanentemente una nota.

**Cómo funciona:**
1. Verifica que la nota exista y que el solicitante sea el propietario.
2. Elimina la nota y cualquier registro de compartición asociado.

**Headers:** `Authorization: Bearer <token>`

**Response `204 No Content`.**

---

### Compartición de Notas - `/api/note-shares`

Permite compartir notas con amigos. Implementa dos niveles de permiso: solo lectura y con opción de copiar.

**Reglas de negocio clave:**
- Solo el propietario puede compartir su nota.
- El destinatario debe ser un amigo aceptado (`ACCEPTED`).
- No se puede compartir una nota consigo mismo.
- No se puede compartir la misma nota dos veces con el mismo usuario.

---

#### `POST /api/note-shares`

**Objetivo:** Compartir una nota con un amigo, especificando qué puede hacer con ella.

**Cómo funciona:**
1. Verifica que el usuario autenticado sea el dueño de la nota.
2. Verifica que exista una amistad aceptada entre ambos usuarios.
3. Verifica que no exista un share previo de esa nota para ese usuario.
4. Crea el registro de compartición con el permiso indicado.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "noteId": 1,
  "sharedWithUserId": 2,
  "permission": "CAN_COPY"
}
```

**Permisos disponibles:**
| Valor | Descripción |
|-------|-------------|
| `READ_ONLY` | El destinatario solo puede leer la nota |
| `CAN_COPY` | El destinatario puede copiar la nota a su propia colección |

**Response `201 Created`:**
```json
{
  "id": 1,
  "noteId": 1,
  "noteTitle": "Mi primera nota",
  "noteContent": "Contenido de la nota...",
  "sharedByUserId": 1,
  "sharedByUserName": "John Doe",
  "sharedWithUserId": 2,
  "sharedWithUserName": "Jane Smith",
  "permission": "CAN_COPY",
  "createdAt": "2025-03-01 12:00:00",
  "updatedAt": "2025-03-01 12:00:00"
}
```

---

#### `GET /api/note-shares/received`

**Objetivo:** Ver todas las notas que otros usuarios han compartido con el usuario autenticado.

**Cómo funciona:**
1. Consulta todos los registros de compartición donde `sharedWithUserId` = usuario actual.
2. Retorna la lista con el contenido de cada nota y los datos del remitente.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** `List<SharedNoteResponse>`

---

#### `GET /api/note-shares/sent`

**Objetivo:** Ver todas las notas que el usuario autenticado ha compartido con otros.

**Cómo funciona:**
1. Consulta todos los registros donde `sharedByUserId` = usuario actual.
2. Retorna la lista con los datos del destinatario.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** `List<SharedNoteResponse>`

---

#### `GET /api/note-shares/{id}`

**Objetivo:** Obtener el detalle de un registro de compartición específico.

**Cómo funciona:**
1. Busca el share por ID.
2. Verifica que el solicitante sea el propietario de la nota o el destinatario.
3. Retorna el detalle completo.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** `SharedNoteResponse`

---

#### `POST /api/note-shares/{id}/copy`

**Objetivo:** Copiar una nota compartida a la colección propia del usuario (patrón copy-on-write).

**Cómo funciona:**
1. Verifica que el share exista y que el solicitante sea el destinatario.
2. Verifica que el permiso sea `CAN_COPY`.
3. Crea una nueva nota independiente con el mismo título y contenido, pero con el usuario actual como propietario.
4. La nota original permanece inalterada.

**Headers:** `Authorization: Bearer <token>`

**Response `201 Created`:** `NoteWebResponse` (la nueva nota creada en la colección del usuario)

---

#### `DELETE /api/note-shares/{id}`

**Objetivo:** Dejar de compartir una nota (el propietario la "deja de compartir" con un amigo específico).

**Cómo funciona:**
1. Verifica que el share exista.
2. Verifica que el solicitante sea el propietario de la nota compartida.
3. Elimina el registro de compartición.

**Headers:** `Authorization: Bearer <token>`

**Response `204 No Content`.**

---

### Amistad - `/api/friendship`

Gestiona el sistema de relaciones entre usuarios: enviar solicitudes, aceptarlas, rechazarlas o bloquear.

**Ciclo de vida de una amistad:**
```
[Sin relación] → PENDING → ACCEPTED
                         → REJECTED
                         → BLOCKED
```

---

#### `POST /api/friendship/requests`

**Objetivo:** Enviar una solicitud de amistad a otro usuario.

**Cómo funciona:**
1. Verifica que el destinatario exista y esté activo.
2. Verifica que no exista ya una solicitud `PENDING` o amistad `ACCEPTED` entre ambos.
3. Crea el registro de amistad con estado `PENDING`.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "addresseeId": 2
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "requesterId": 1,
  "addresseeId": 2,
  "status": "PENDING",
  "createdAt": "2025-03-01 12:00:00",
  "updatedAt": "2025-03-01 12:00:00"
}
```

---

#### `PUT /api/friendship/{id}/accept`

**Objetivo:** Aceptar una solicitud de amistad recibida.

**Cómo funciona:**
1. Busca la solicitud por ID.
2. Verifica que el solicitante sea el destinatario de la solicitud (`addresseeId`).
3. Cambia el estado a `ACCEPTED`.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** `FriendResponseDTO` con `status: "ACCEPTED"`.

---

#### `PUT /api/friendship/{id}/reject`

**Objetivo:** Rechazar una solicitud de amistad recibida.

**Cómo funciona:**
1. Verifica que el solicitante sea el destinatario de la solicitud.
2. Cambia el estado a `REJECTED`.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** `FriendResponseDTO` con `status: "REJECTED"`.

---

#### `PUT /api/friendship/{id}/block`

**Objetivo:** Bloquear a un usuario para impedir interacciones futuras.

**Cómo funciona:**
1. Verifica que el solicitante sea parte de la relación.
2. Cambia el estado a `BLOCKED`.
3. Un usuario bloqueado no puede enviar nuevas solicitudes ni compartir notas.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`:** `FriendResponseDTO` con `status: "BLOCKED"`.

---

#### `GET /api/friendship/status/{status}`

**Objetivo:** Obtener la lista de usuarios relacionados con el autenticado filtrada por estado de la relación, con paginación.

**Cómo funciona:**
1. Busca todas las amistades donde el usuario autenticado sea requester o addressee.
2. Filtra por el estado indicado.
3. Retorna el resumen de los usuarios relacionados (no el objeto Friendship en sí).

**Headers:** `Authorization: Bearer <token>`

**Path Params:**
| Param | Valores posibles |
|-------|-----------------|
| `status` | `PENDING`, `ACCEPTED`, `REJECTED`, `BLOCKED` |

**Query Params:**
| Param | Tipo | Descripción |
|-------|------|-------------|
| `page` | int (default: 0) | Página |
| `size` | int (default: 10) | Tamaño de página |

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": 2,
      "fullName": "Jane Smith",
      "email": "jane@example.com"
    }
  ],
  "totalPages": 1,
  "numberOfElements": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

---

## Modelos de Datos

### User
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | ID único |
| `fullName` | String | Nombre completo |
| `email` | String | Email único |
| `password` | String | Hash BCrypt |
| `phone` | String | Teléfono |
| `address` | String | Dirección |
| `role` | Enum | `ADMIN` \| `REGULAR` |
| `active` | Boolean | Soft delete (default: true) |
| `createdAt` | LocalDateTime | Fecha de creación |
| `updatedAt` | LocalDateTime | Última actualización |

### Note
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | ID único |
| `title` | String | Título (3–500 chars) |
| `content` | String | Contenido (3–5000 chars) |
| `owner` | User | Propietario (FK) |
| `createdAt` | LocalDateTime | Fecha de creación |

### SharedNote
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | ID único |
| `noteId` | Long | Nota compartida (FK) |
| `sharedByUserId` | Long | Quién comparte (FK) |
| `sharedWithUserId` | Long | Con quién se comparte (FK) |
| `permission` | Enum | `READ_ONLY` \| `CAN_COPY` |
| `createdAt` | LocalDateTime | Fecha de compartición |
| `updatedAt` | LocalDateTime | Última actualización |

### Friendship
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | ID único |
| `requesterId` | Long | Quien envió la solicitud (FK) |
| `addresseeId` | Long | Quien recibe la solicitud (FK) |
| `friendshipStatus` | Enum | `PENDING` \| `ACCEPTED` \| `REJECTED` \| `BLOCKED` |
| `createdAt` | LocalDateTime | Fecha de creación |
| `updatedAt` | LocalDateTime | Última actualización |

---

## Errores

La API retorna errores con el siguiente formato estándar:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "timestamp": "2025-03-01T12:00:00"
}
```

| Código | Descripción |
|--------|-------------|
| `400` | Datos de entrada inválidos o violación de regla de negocio |
| `401` | Token ausente, expirado o inválido |
| `403` | Acceso denegado (recurso de otro usuario o rol insuficiente) |
| `404` | Recurso no encontrado |
| `409` | Conflicto (email duplicado, solicitud de amistad duplicada, etc.) |
| `500` | Error interno del servidor |
