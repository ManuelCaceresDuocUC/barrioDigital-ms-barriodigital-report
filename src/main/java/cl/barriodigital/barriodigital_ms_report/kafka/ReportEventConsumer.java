package cl.barriodigital.barriodigital_ms_report.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.barriodigital.barriodigital_ms_report.entity.ProcedureMetric;
import cl.barriodigital.barriodigital_ms_report.repository.ProcedureMetricRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportEventConsumer {

    private final ProcedureMetricRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @KafkaListener(topics = "requests.events", groupId = "ms-report-consumer-group")
    public void consumeRequestEvent(String payload) {
        try {
            RequestEvent event = objectMapper.readValue(payload, RequestEvent.class);
            log.info("Evento recibido para agregación: {}", event);

            String actionKey = (event.getAction() != null) ? event.getAction() : "UNKNOWN";

            // Corregido: Buscar por el nombre de la acción (findByActionName) en lugar de findById
            ProcedureMetric metric = repository.findByActionName(actionKey)
                    .orElseGet(() -> ProcedureMetric.builder()
                            .actionName(actionKey)
                            .totalRequests(0L)
                            .successCount(0L)
                            .failedCount(0L)
                            .build());

            // Incrementar total de solicitudes
            metric.setTotalRequests(metric.getTotalRequests() + 1);

            // Corregido: Incluir "COMPLETED" y "SUCCESS" en la condición de exito
            String status = event.getStatus();
            if ("OK".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status) || 
                "COMPLETED".equalsIgnoreCase(status) || "SUCCESS".equalsIgnoreCase(status)) {
                metric.setSuccessCount(metric.getSuccessCount() + 1);
            } else {
                metric.setFailedCount(metric.getFailedCount() + 1);
            }

            repository.save(metric);
            log.info("Métrica actualizada correctamente para la acción: {}", actionKey);

        } catch (Exception e) {
            log.error("Error al procesar y deserializar el mensaje de Kafka: {}", payload, e);
        }
    }
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class RequestEvent {
    private Long requestId;
    private String action;
    private String status;
}