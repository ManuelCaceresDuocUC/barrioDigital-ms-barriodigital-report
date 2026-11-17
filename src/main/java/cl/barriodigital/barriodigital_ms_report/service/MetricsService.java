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
 *
 * QUÉ ES: la lógica de negocio del servicio (el "cerebro" de la reportería).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    // Estados finales que cuentan como éxito / fallo. NO_TYPE agrupa eventos sin tipo de trámite.
    static final String RESOLVED = "RESUELTO";
    static final String REJECTED = "RECHAZADO";
    static final String NO_TYPE = "Sin tipo";

    private final ProcedureMetricRepository metrics;
    private final ProcessedEventRepository processed;

    /** @return true si el evento se contó; false si estaba repetido o no es válido */
    // @Transactional: el contador y el registro del evento se guardan juntos o no se guarda nada.
    // Así nunca queda un evento contado sin registrar (ni al revés) si algo falla a mitad.
    @Transactional
    public boolean apply(RequestEvent event) {
        // 1) Validación: sin eventId o eventType no se puede procesar.
        if (!isValid(event)) {
            log.warn("Evento descartado por incompleto: {}", event);
            return false;
        }
        // 2) Idempotencia: si el eventId ya está en PROCESSED_EVENTS, ya se contó antes.
        if (processed.existsById(event.getEventId())) {
            log.info("Evento {} ya contado: se ignora", event.getEventId());
            return false;
        }

        // 3) Se determina la fila a actualizar: el nombre del trámite (o "Sin tipo").
        String procedure = hasText(event.getProcedureType()) ? event.getProcedureType().trim() : NO_TYPE;
        if (RequestEvent.CREATED.equals(event.getEventType())) {
            // Solicitud nueva: suma 1 al total de ese trámite.
            ProcedureMetric metric = metricFor(procedure);
            metric.setTotalRequests(metric.getTotalRequests() + 1);
            metrics.save(metric);
        } else if (RequestEvent.STATUS_CHANGED.equals(event.getEventType())) {
            // Cambio de estado: solo importan RESUELTO y RECHAZADO.
            applyStatusChange(procedure, event.getStatus());
        } else {
            log.warn("Tipo de evento desconocido '{}': se ignora", event.getEventType());
        }

        // 4) Se registra el eventId como procesado (misma transacción que el paso 3).
        processed.save(new ProcessedEvent(event.getEventId(), LocalDateTime.now()));
        return true;
    }

    // Suma al contador de éxito o de fallo según el estado nuevo.
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

    // Devuelve la fila existente del trámite o crea una nueva con los tres contadores en cero.
    private ProcedureMetric metricFor(String procedure) {
        return metrics.findByActionName(procedure).orElseGet(() -> ProcedureMetric.builder()
                .actionName(procedure).totalRequests(0L).successCount(0L).failedCount(0L).build());
    }

    // Un evento es válido si tiene identificador y tipo.
    private static boolean isValid(RequestEvent event) {
        return event != null && hasText(event.getEventId()) && hasText(event.getEventType());
    }

    // true si el texto no es null, ni vacío, ni solo espacios.
    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
