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
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaReportEventListener {

    private final MetricsService metrics;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "requests.events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRequestEvent(String payload) {
        RequestEvent event;
        try {
            event = objectMapper.readValue(payload, RequestEvent.class);
        } catch (JsonProcessingException ex) {
            log.error("Mensaje de requests.events descartado, no es JSON válido: {}", payload, ex);
            return;
        }
        if (metrics.apply(event)) {
            log.info("Métricas actualizadas con el evento {} ({})", event.getEventId(), event.getEventType());
        }
    }
}
