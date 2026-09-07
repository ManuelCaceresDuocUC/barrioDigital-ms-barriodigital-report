# Proyecto: BarrioDigital - Servicio de Reportería

**Componente:** Microservicio de Dominio (Analítica y KPIs)
**Integrantes:** [Nombre Apellido 1], [Nombre Apellido 2], [Nombre Apellido 3]

## Descripción
Encargado de generar el panel de operaciones en tiempo real para los administradores. Este servicio recopila datos mediante streaming de eventos sin impactar el rendimiento transaccional del sistema base. Genera KPIs vitales como: trámites por hora, tiempos promedio de resolución, estados activos y los procedimientos más demandados.

## Tecnologías a usar
* **Framework:** Spring Boot (Java)
* **Base de Datos:** Oracle (Esquema optimizado para lectura/agregación)
* **Streaming (Consumer):** Apache Kafka (Tópico: `requests.events`)
* **Integración:** API REST (Endpoints de solo lectura)
