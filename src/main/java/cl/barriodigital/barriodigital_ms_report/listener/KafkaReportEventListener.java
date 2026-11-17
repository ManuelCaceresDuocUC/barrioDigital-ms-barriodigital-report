package cl.barriodigital.barriodigital_ms_report.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.barriodigital.barriodigital_ms_report.dto.RequestEvent;
import cl.barriodigital.barriodigital_ms_report.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Único consumidor de requests.events. Un mensaje que no es JSON válido se descarta con un error en el log
 * (reintentarlo no lo arreglaría); un fallo de base de datos se propaga y lo reintenta el manejador de errores.
 *
 * QUÉ ES: la entrada de datos de este servicio: recibe el mensaje de Kafka, lo convierte en objeto y
 * se lo pasa a MetricsService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaReportEventListener {

    private final MetricsService metrics;
    // Conversor JSON <-> objetos (Jackson 2). Se crea a mano porque Spring Boot 4 trae Jackson 3 por defecto.
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Se suscribe a requests.events; el grupo viene de application.yaml (ms-report-consumer-group-v2).
    // El grupo es distinto al de audit, por eso ambos servicios reciben todos los mensajes.
    @KafkaListener(topics = "requests.events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRequestEvent(String payload) {
        RequestEvent event;
        try {
            // Convierte el texto JSON en un objeto RequestEvent.
            event = objectMapper.readValue(payload, RequestEvent.class);
        } catch (JsonProcessingException ex) {
            // JSON roto: se descarta (return) en lugar de lanzar la excepción, porque reintentar no ayudaría.
            log.error("Mensaje de requests.events descartado, no es JSON válido: {}", payload, ex);
            return;
        }
        // apply() devuelve true solo si el evento se contó (no repetido ni inválido).
        if (metrics.apply(event)) {
            log.info("Métricas actualizadas con el evento {} ({})", event.getEventId(), event.getEventType());
        }
    }
}
