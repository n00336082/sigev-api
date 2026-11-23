SIGEV API (sigev-api)

Backend del proyecto SIGEV - Sistema Web Distribuido para la Gestión de Inventarios y Control de Vencimiento de Productos.

Tecnologías usadas
- Java 21
- Spring Boot 4.1.2
- Spring Data JPA
- Base de datos H2 (archivo local en data/sigev)
- Maven Wrapper

Requisitos
- Tener instalado Java 21 o superior.

Cómo ejecutar el proyecto
1. Abrir una terminal dentro de la carpeta sigev-api.
2. Ejecutar el comando:
   .\mvnw.cmd spring-boot:run
3. El servidor quedará funcionando en http://localhost:8080

Acceso a la consola de H2
Con el servidor encendido, abrir en el navegador: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:file:./data/sigev
- Usuario: sa
- Contraseña: (dejar vacío)
