package cl.barriodigital.barriodigital_ms_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Evento de requests.events (lo publica ms-requests). No trae datos personales del vecino.
 * Los campos desconocidos se ignoran para que ms-requests pueda agregar datos sin romper este consumidor.
 *
 * QUÉ ES: un DTO (objeto de transferencia de datos) que representa el JSON recibido de Kafka.
 * Ejemplo de mensaje: {"eventId":"...","eventType":"REQUEST_CREATED","requestId":22,"procedureType":"Poda",...}
 */
// @Data (Lombok) genera getters, setters, equals, hashCode y toString.
@Data
// Si el JSON trae campos que esta clase no declara, se ignoran en vez de lanzar error.
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestEvent {

    // Valores posibles de eventType (los define el productor, el microservicio requests).
    public static final String CREATED = "REQUEST_CREATED";
    public static final String STATUS_CHANGED = "REQUEST_STATUS_CHANGED";

    // Identificador único del evento: permite contarlo una sola vez (idempotencia).
    private String eventId;
    // Qué ocurrió: creación o cambio de estado.
    private String eventType;
    // Fecha/hora en que ocurrió el hecho (texto ISO-8601).
    private String occurredAt;
    // Id de la solicitud afectada.
    private Long requestId;
    // Nombre del tipo de trámite (por ejemplo "Poda de árboles"): clave del reporte.
    private String procedureType;
    // Estado anterior (solo en cambios de estado).
    private String previousStatus;
    // Estado nuevo (INGRESADO, ADMITIDO, EN_GESTION, EN_TERRENO, RESUELTO, RECHAZADO).
    private String status;
}
