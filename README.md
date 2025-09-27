# Sistema de Gestión para Ferretería (ferreteriagd)

Proyecto Java (Maven) simple para gestión de productos, clientes, proveedores, usuarios y ventas.

Características principales
- CRUD para clientes, productos, proveedores, roles y usuarios.
- Gestión de ventas y detalle de ventas.
- Conexión a SQL Server (DatabaseManager) y script de inicialización en `src/main/resources/init_db.sql`.

Requisitos
- Java 11+ (o versión compatible con el proyecto)
- Maven
- SQL Server (si quieres ejecutar el script SQL)

Uso rápido
1) Compilar:

```bash
mvn clean package
```

2) Ejecutar la aplicación (desde la clase principal `App`):

```bash
mvn exec:java -Dexec.mainClass="com.ferreteriagd.sistemaFerreteria.App"
```

Inicializar la base de datos
- Opción A (desde la app): DatabaseManager incluye `initializeDatabase()` que crea tablas e inserta datos por defecto (roles y admin). Puedes invocarlo desde `App` o usar la aplicación para inicializar.
- Opción B (manual): ejecutar el script `src/main/resources/init_db.sql` en tu servidor SQL Server. Ejemplo con sqlcmd (Windows cmd):

```cmd
sqlcmd -S <SERVER> -U <USER> -P <PASSWORD> -i "src\main\resources\init_db.sql"
```

Licencia
Este proyecto incluye un archivo `LICENSE` (MIT) en la raíz. Revisa ese archivo para los términos de uso.
