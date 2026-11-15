package cl.barriodigital.barriodigital_ms_report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.barriodigital.barriodigital_ms_report.dto.RequestEvent;
import cl.barriodigital.barriodigital_ms_report.entity.ProcedureMetric;
import cl.barriodigital.barriodigital_ms_report.entity.ProcessedEvent;
import cl.barriodigital.barriodigital_ms_report.repository.ProcedureMetricRepository;
import cl.barriodigital.barriodigital_ms_report.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Métricas por tipo de trámite: total = trámites ingresados, éxito = resueltos, fallidos = rechazados.
 * Cada evento se cuenta una sola vez (por eventId) dentro de la misma transacción que la métrica.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    static final String RESOLVED = "RESUELTO";
    static final String REJECTED = "RECHAZADO";
    static final String NO_TYPE = "Sin tipo";

    private final ProcedureMetricRepository metrics;
    private final ProcessedEventRepository processed;

    /** @return true si el evento se contó; false si estaba repetido o no es válido */
    @Transactional
    public boolean apply(RequestEvent event) {
        if (!isValid(event)) {
            log.warn("Evento descartado por incompleto: {}", event);
            return false;
        }
        if (processed.existsById(event.getEventId())) {
            log.info("Evento {} ya contado: se ignora", event.getEventId());
            return false;
        }

        String procedure = hasText(event.getProcedureType()) ? event.getProcedureType().trim() : NO_TYPE;
        if (RequestEvent.CREATED.equals(event.getEventType())) {
            ProcedureMetric metric = metricFor(procedure);
            metric.setTotalRequests(metric.getTotalRequests() + 1);
            metrics.save(metric);
        } else if (RequestEvent.STATUS_CHANGED.equals(event.getEventType())) {
            applyStatusChange(procedure, event.getStatus());
        } else {
            log.warn("Tipo de evento desconocido '{}': se ignora", event.getEventType());
        }

        processed.save(new ProcessedEvent(event.getEventId(), LocalDateTime.now()));
        return true;
    }

    private void applyStatusChange(String procedure, String status) {
        if (RESOLVED.equals(status)) {
            ProcedureMetric metric = metricFor(procedure);
            metric.setSuccessCount(metric.getSuccessCount() + 1);
            metrics.save(metric);
        } else if (REJECTED.equals(status)) {
            ProcedureMetric metric = metricFor(procedure);
            metric.setFailedCount(metric.getFailedCount() + 1);
            metrics.save(metric);
        }
        // Los estados intermedios (ADMITIDO, EN_GESTION, EN_TERRENO) no cambian estas métricas.
    }

    private ProcedureMetric metricFor(String procedure) {
        return metrics.findByActionName(procedure).orElseGet(() -> ProcedureMetric.builder()
                .actionName(procedure).totalRequests(0L).successCount(0L).failedCount(0L).build());
    }

    private static boolean isValid(RequestEvent event) {
        return event != null && hasText(event.getEventId()) && hasText(event.getEventType());
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
