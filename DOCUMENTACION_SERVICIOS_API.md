# DOCUMENTACIÓN TÉCNICA DE SERVICIOS WEB
## Proyecto Plazmo Drive

**Evidencia:** GA7-220501096-AA5-EV03  
**Proyecto:** Plazmo Drive  
**Tecnologías:** Java 21, Jakarta EE 10, Jakarta REST (JAX-RS), JPA, EclipseLink, PostgreSQL y Payara Micro.

## 1. Introducción

Plazmo Drive cuenta con una API REST que permite administrar usuarios, vehículos y reservas, validar el inicio de sesión y consultar estadísticas del sistema. Los servicios intercambian información en formato JSON y se conectan a PostgreSQL mediante JPA.

## 2. Dirección base

Durante la ejecución local:

`http://localhost:8080/plazmodrive/api`

Los servicios utilizan los métodos HTTP GET, POST, PUT y DELETE, según la operación.

## 3. Servicio de autenticación - AuthResource

**Ruta base:** `/auth`

**Objetivo:** validar las credenciales de los usuarios registrados.

### Endpoint: iniciar sesión

- Método: POST
- Ruta: `/auth/login`
- Content-Type: application/json

Solicitud:

```json
{
  "cedula": "9988776655",
  "password": "PruebaAA5_2026"
}
```

Respuesta exitosa (200):

```json
{
  "mensaje": "Autenticación satisfactoria",
  "usuario": {
    "id": 2,
    "nombres": "Usuario",
    "apellidos": "Prueba AA5",
    "cedula": "9988776655",
    "rol": "USUARIO"
  }
}
```

Posibles respuestas:
- 200: autenticación satisfactoria.
- 400: faltan datos obligatorios.
- 401: usuario o contraseña incorrectos.

La contraseña se verifica mediante PasswordUtil. El servicio devuelve información pública y no expone el hash de la contraseña.

## 4. Servicio de usuarios - UsuarioResource

**Ruta base:** `/usuarios`

**Objetivo:** registrar, consultar, actualizar y eliminar usuarios.

### 4.1 Listar usuarios

- Método: GET
- Ruta: `/usuarios`
- Respuesta: lista de usuarios registrados.

### 4.2 Registrar usuario

- Método: POST
- Ruta: `/usuarios`
- Content-Type: application/json

Solicitud:

```json
{
  "nombres": "Carlos",
  "apellidos": "Pérez",
  "cedula": "1020304050",
  "fechaNacimiento": "2000-05-15",
  "password": "ClaveSegura2026"
}
```

Respuesta:
- 201: usuario creado.
- 400: faltan campos obligatorios.
- 409: la cédula ya está registrada.

El sistema aplica hash a la contraseña antes de guardarla. El rol inicial es USUARIO.

### 4.3 Consultar usuario

- Método: GET
- Ruta: `/usuarios/{id}`
- Parámetro: id del usuario.
- Respuestas: 200 si existe; 404 si no existe.

### 4.4 Actualizar usuario

- Método: PUT
- Ruta: `/usuarios/{id}`
- Recibe los campos que se desean actualizar en formato JSON.
- Permite modificar nombres, apellidos, cédula, fecha de nacimiento, rol y contraseña.
- Si se actualiza la contraseña, se genera un nuevo hash.
- Respuestas: 200 si se actualiza; 404 si no existe.

### 4.5 Eliminar usuario

- Método: DELETE
- Ruta: `/usuarios/{id}`
- Respuestas: 204 si se elimina; 404 si no existe.

## 5. Servicio de vehículos - VehiculoResource

**Ruta base:** `/vehiculos`

**Objetivo:** administrar los vehículos disponibles para el sistema.

### 5.1 Listar vehículos

- Método: GET
- Ruta: `/vehiculos`
- Devuelve los vehículos registrados.

### 5.2 Registrar vehículo

- Método: POST
- Ruta: `/vehiculos`
- Content-Type: application/json

Solicitud:

```json
{
  "placa": "ABC123",
  "marca": "Toyota",
  "modelo": "Corolla",
  "color": "Blanco",
  "tipo": "Automóvil",
  "anio": 2025,
  "estado": "DISPONIBLE"
}
```

Respuestas:
- 201: vehículo creado.
- 400: faltan marca, modelo o placa.
- 409: ya existe un vehículo con esa placa.

La placa se normaliza a mayúsculas y, si no se indica estado, se asigna DISPONIBLE.

### 5.3 Consultar vehículo

- Método: GET
- Ruta: `/vehiculos/{id}`
- Respuestas: 200 si existe; 404 si no existe.

### 5.4 Actualizar vehículo

- Método: PUT
- Ruta: `/vehiculos/{id}`
- Permite actualizar placa, marca, modelo, color, tipo, año y estado.
- Respuestas: 200 si se actualiza; 404 si no existe.

### 5.5 Eliminar vehículo

- Método: DELETE
- Ruta: `/vehiculos/{id}`
- Respuestas: 204 si se elimina; 404 si no existe.

## 6. Servicio de reservas - ReservaResource

**Ruta base:** `/reservas`

**Objetivo:** gestionar las reservas de vehículos, consultar su historial y cancelar operaciones.

### 6.1 Consultar reservas

- Método: GET
- Ruta: `/reservas`
- Parámetro opcional: `usuarioId`.

Ejemplo para consultar todas:

`GET /reservas`

Ejemplo para filtrar por usuario:

`GET /reservas?usuarioId=1`

### 6.2 Consultar una reserva

- Método: GET
- Ruta: `/reservas/{id}`
- Respuestas: 200 si existe; 404 si no existe.

### 6.3 Crear reserva

- Método: POST
- Ruta: `/reservas`
- Content-Type: application/json

Solicitud:

```json
{
  "usuarioId": 2,
  "vehiculoId": 1,
  "fechaInicio": "2026-12-10",
  "fechaFin": "2026-12-15"
}
```

Respuestas:
- 201: reserva creada.
- 400: faltan datos o la fecha final es anterior a la inicial.
- 404: el usuario o vehículo no existe.
- 409: el vehículo ya tiene una reserva activa que se cruza con las fechas solicitadas.

Las fechas se reciben en formato ISO `AAAA-MM-DD`. El estado inicial de la reserva es ACTIVA.

### 6.4 Cancelar reserva

- Método: PUT
- Ruta: `/reservas/{id}/cancelar`
- Cambia el estado de la reserva a CANCELADA sin eliminarla del historial.
- Respuestas: 200 si se cancela; 404 si no existe.

## 7. Servicio de reportes - ReporteResource

**Ruta base:** `/reportes`

**Objetivo:** consultar indicadores generales del sistema.

### Consultar resumen

- Método: GET
- Ruta: `/reportes`

Respuesta de ejemplo:

```json
{
  "usuarios": 10,
  "vehiculos": 8,
  "vehiculosDisponibles": 5,
  "reservasActivas": 3,
  "reservasCanceladas": 2
}
```

Los valores son ilustrativos. El servicio calcula los totales directamente desde la base de datos.

## 8. Códigos de respuesta HTTP

| Código | Significado |
|---|---|
| 200 | Solicitud procesada correctamente. |
| 201 | Recurso creado correctamente. |
| 204 | Operación realizada sin contenido de respuesta. |
| 400 | Datos obligatorios faltantes o inválidos. |
| 401 | Credenciales incorrectas. |
| 404 | Recurso no encontrado. |
| 409 | Conflicto con un registro existente o una reserva. |

## 9. Persistencia y conexión con la base de datos

Los servicios utilizan EntityManager y la unidad de persistencia `plazmoPU` para consultar y modificar los registros.

Las entidades principales son:

- Usuario: representa a los usuarios registrados.
- Vehiculo: almacena la información de los vehículos.
- Reserva: relaciona usuarios y vehículos con fechas y estados.

La base de datos utilizada es PostgreSQL.

## 10. Seguridad

- Las contraseñas se procesan mediante PasswordUtil.
- Se utiliza hash con PBKDF2WithHmacSHA256.
- Las respuestas de usuarios no exponen el hash de la contraseña.
- La autenticación rechaza credenciales incorrectas.
- Se validan los datos obligatorios antes de registrar información.

## 11. Herramientas de versionamiento

El proyecto utiliza Git para controlar los cambios y GitHub para alojar el repositorio remoto.

Repositorio:

https://github.com/h04929662-lang/Plazmo_Drive

## 12. Conclusión

Plazmo Drive dispone de cinco recursos REST que permiten realizar las operaciones principales del sistema: autenticación, administración de usuarios, administración de vehículos, gestión de reservas y consulta de reportes. La documentación facilita comprender las rutas, los datos intercambiados y las respuestas de cada servicio.
