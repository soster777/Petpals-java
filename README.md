# PetPals 🐾

Sistema de gestión para una veterinaria, hecho en **Java** con base de datos **MySQL**. Lo hicimos como proyecto de la materia Programación Avanzada en la Escuela Da Vinci.

Maneja turnos, consultas, vacunas, guardería, historial clínico, notificaciones y planes de suscripción. Hay tres tipos de usuario y cada uno tiene su propio menú.

## Funcionalidades

| Rol | Qué puede hacer |
| --- | --- |
| **Dueño** | Registrarse, cargar sus mascotas, pedir turnos (consulta, vacunación o guardería), ver y cancelar sus turnos, ver el historial clínico, recibir notificaciones y suscribirse a un plan |
| **Empleado** | Ver la agenda, registrar turnos, confirmar o rechazar los turnos que piden los dueños asignando veterinario, reprogramar y cancelar turnos, registrar ingresos a la guardería, modificar datos de las mascotas y enviar notificaciones |
| **Veterinario** | Ver sus turnos, atender un turno cargando la consulta o la vacuna (con fecha de próxima dosis), y cargar o consultar el historial clínico |

**Cómo funciona un turno:**

1. El dueño pide el turno y queda **pendiente**.
2. El empleado lo **confirma** y le asigna un veterinario.
3. El veterinario lo atiende y queda **realizado**.

En cada paso, al dueño le llega una notificación. Lo que se hace en el turno queda guardado solo en el historial clínico de la mascota.

## Tecnologías

- Java 8 (interfaz con `JOptionPane` de Swing)
- MySQL con JDBC (`PreparedStatement`)
- BCrypt para guardar las contraseñas hasheadas
- JUnit 5 para los tests
- Eclipse

## Cómo está organizado

```
src/
├── BLL/          lógica de negocio (modelos y menús de cada rol)
├── DLL/          acceso a datos: Conexion (singleton) y un Controller por tabla
├── GUI/          Main, pantalla de inicio
├── repository/   interfaces DAO y validaciones
├── excepciones/  DatoInvalidoException
└── img/
test/
└── tests/        tests unitarios con JUnit 5
database/
└── petpals.sql   script de la base con datos de prueba
```

### Conceptos de programación orientada a objetos

- **Herencia y clases abstractas:**
  - `Usuario` es abstracta y de ella heredan `Duenio`, `Veterinario` y `Empleado`.
  - `Servicio` es abstracta y de ella heredan `Consulta`, `Vacunacion` y `Guarderia`.
- **Polimorfismo:**
  - Cada rol implementa su propio `menu()`, y el `Main` lo llama sin saber qué tipo de usuario es.
  - Cada servicio arma su propio `resumen()`, y el historial lo registra sin importar de qué tipo es.
- **Patrón DAO:**
  - Las interfaces de `repository/` definen las operaciones de cada controller.
  - `CrudRepository<T>` es una interfaz genérica que implementan varios controllers.
- **Genéricos:** `Validaciones.elegirDeLista()` sirve para elegir de cualquier lista (mascotas, turnos, planes, veterinarios).
- **Excepciones propias:** `DatoInvalidoException` se usa para las reglas del negocio, por ejemplo:
  - no se puede pedir un turno para una fecha que ya pasó;
  - solo se atienden turnos confirmados.
- **Singleton:** `Conexion` mantiene una sola conexión a la base para toda la app.

### Base de datos

Tiene 10 tablas: usuarios, mascotas, turnos, consultas, vacunaciones, guarderías, historiales, notificaciones, planes y suscripciones. Están relacionadas con claves foráneas, y al borrar una mascota se borran también sus turnos e historial.

## Cómo correrlo

1. Tener MySQL prendido (por ejemplo con XAMPP o MySQL Workbench).
2. Ejecutar el script [`database/petpals.sql`](database/petpals.sql). Crea la base `PetPals` con datos de prueba.
3. Abrir el proyecto en Eclipse (*File → Import → Existing Projects into Workspace*). Los `.jar` del conector de MySQL y de BCrypt ya están en `lib/`.
4. Si tu MySQL tiene otra clave, cambiarla en `src/DLL/Conexion.java`.
5. Ejecutar `GUI/Main.java`.

Para correr los tests: clic derecho en la carpeta `test` → *Run As → JUnit Test*.

### Usuarios de prueba

Todos tienen la contraseña `1234`.

| Rol | Mail |
| --- | --- |
| Dueño | duenio@petpals.com |
| Veterinario | vet@petpals.com |
| Empleado | empleado@petpals.com |

## Equipo

Proyecto grupal hecho por:

- **Valentina Soster** · [github.com/soster777](https://github.com/soster777)
- **Lola**
- **Miranda**
