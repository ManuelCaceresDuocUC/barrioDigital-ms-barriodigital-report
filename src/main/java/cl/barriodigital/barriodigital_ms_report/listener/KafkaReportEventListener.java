package cl.barriodigital.barriodigital_ms_report.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.barriodigital.barriodigital_ms_report.dto.RequestEvent;
import cl.barriodigital.barriodigital_ms_report.entity.ProcedureMetric;
import cl.barriodigital.barriodigital_ms_report.repository.ProcedureMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaReportEventListener {

    private final ProcedureMetricRepository metricRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "requests.events", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consumeRequestEvent(String payload) {
        try {
            RequestEvent event = objectMapper.readValue(payload, RequestEvent.class);
            log.info("Procesando métricas de reporte para evento: {}", event);

            String action = (event.getAction() != null) ? event.getAction() : "UNKNOWN";

            ProcedureMetric metric = metricRepository.findById(action)
                    .orElseGet(() -> ProcedureMetric.builder()
                            .actionName(action)
                            .totalRequests(0L)
                            .successCount(0L)
                            .failedCount(0L)
                            .build());

            metric.setTotalRequests(metric.getTotalRequests() + 1);

            if ("OK".equalsIgnoreCase(event.getStatus()) || "APPROVED".equalsIgnoreCase(event.getStatus())) {
                metric.setSuccessCount(metric.getSuccessCount() + 1);
            } else {
                metric.setFailedCount(metric.getFailedCount() + 1);
            }

            metricRepository.save(metric);
            log.info("Métricas actualizadas para la acción [{}]", action);

        } catch (Exception e) {
            log.error("Error al deserializar o procesar el mensaje de Kafka payload=[{}]: {}", payload, e.getMessage(), e);
        }
    }
}