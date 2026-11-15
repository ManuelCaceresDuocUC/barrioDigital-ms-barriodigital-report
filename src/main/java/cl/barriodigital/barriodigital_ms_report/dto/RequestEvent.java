package cl.barriodigital.barriodigital_ms_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Evento de requests.events (lo publica ms-requests). No trae datos personales del vecino.
 * Los campos desconocidos se ignoran para que ms-requests pueda agregar datos sin romper este consumidor.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestEvent {

    public static final String CREATED = "REQUEST_CREATED";
    public static final String STATUS_CHANGED = "REQUEST_STATUS_CHANGED";

    private String eventId;
    private String eventType;
    private String occurredAt;
    private Long requestId;
    private String procedureType;
    private String previousStatus;
    private String status;
}
